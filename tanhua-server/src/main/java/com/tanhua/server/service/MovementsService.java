package com.tanhua.server.service;

import com.alibaba.fastjson.JSON;
import com.tanhua.commons.template.OssTemplate;
import com.tanhua.domain.db.User;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.domain.vo.MovementsVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.mongo.PublishApi;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.utils.ComputeUtil;
import com.tanhua.server.utils.RelativeDateFormat;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class MovementsService {
    @Reference
    private PublishApi publishApi;
    @Autowired
    private OssTemplate ossTemplate;
    @Reference
    private UserInfoApi userInfoApi;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    /**
     * 接口名称：动态-发布
     * 需求描述：发布动态，往动态表、自己的相册表、好友的时间线表记录动态
     */
    public ResponseEntity<Object> saveMovements(Publish publish, MultipartFile[] imageContent) throws IOException {
        //1. 获取登陆用户id
        Long userId = UserHolder.getUserId();


        //判断是否被封禁发布动态,并给出提示
        String key = "Freeze_User_" + userId;
        if (redisTemplate.hasKey(key)) {
            //判断冻结时间和冻结范围
            String freezeJson = redisTemplate.opsForValue().get(key);
            Map<String, Object> map = JSON.parseObject(freezeJson, Map.class);
            String freezingTime = (String) map.get("freezingTime");
            String freezingRange = (String) map.get("freezingRange");
            Long nowTime = (Long) map.get("nowTime");
            Date date = new Date();
            date.setTime(nowTime);
            //如果为冻结发布动态才进入下面的操作
            if ("3".equals(freezingRange)) {
                if ("1".equals(freezingTime)) {
                    //计算解封时间
                    String afterThree = ComputeUtil.offsetDay(date, 3);
                    //提示冻结时间
                    return ResponseEntity.status(500).body(ErrorResult.freezePublish("3", afterThree));
                } else if ("2".equals(freezingTime)) {
                    //计算解封时间
                    String afterThree = ComputeUtil.offsetDay(date, 7);
                    //提示冻结时间
                    return ResponseEntity.status(500).body(ErrorResult.freezePublish("7", afterThree));
                } else {
                    //提示冻结时间
                    return ResponseEntity.status(500).body(ErrorResult.freezePublishNever());
                }
            }
        }else {
            //如果该键已过期,则解封用户,设置userinfo的状态
            UserInfo userInfo = userInfoApi.findById(userId);
            userInfo.setUserStatus("1");
            userInfoApi.updateUserStatus(userInfo);
        }
        //2. 准备图片的集合
        List<String> medias = new ArrayList<>();
        if (imageContent != null && imageContent.length>0) {
            for (MultipartFile file : imageContent) {
                // 处理文件上传，返回url地址
                String url =
                        ossTemplate.upload(file.getOriginalFilename(), file.getInputStream());
                medias.add(url);
            }
        }
        //3. 调用api,发布动态
        publish.setUserId(userId);
        publish.setMedias(medias);
        //【设置动态id】
        publish.setId(ObjectId.get());
        publish.setState(0);
        publishApi.save(publish);

        try {
            //【发送MQ消息】
            rocketMQTemplate.convertAndSend("tanhua-publish2",publish.getId().toString());
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(null);
    }

    /**
     * 接口名称：好友动态
     * 接口路径：GET/movements
     * 需求分析：查询好友发布的动态（朋友圈），查询quanzi_time_line_登陆用户id
     */
    public ResponseEntity<Object> queryPublishMovementsList(Integer page, Integer pagesize) {
        //1. 获取登陆用户id
        Long userId = UserHolder.getUserId();
        //2. 根据登陆用户id，分页查询好友动态
        PageResult pageResult = publishApi.queryPublishList(page,pagesize,userId);
        //3. 封装返回结果：MovementsVo
        this.setMovementsVo(pageResult);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 接口名称：推荐动态
     * 需求分析：查询登陆用户的推荐动态，查询recommend_quanzi表
     */
    public ResponseEntity<Object> queryRecommendPublishList(Integer page, Integer pagesize) {
        //1. 获取登陆用户id
        Long userId = UserHolder.getUserId();
        //2. 根据登陆用户id，分页查询推荐动态：recommend_quanzi
        // 【查询推荐动态，先从redis中查询推荐数据，如果没有查找再查询mongo】
        PageResult pageResult = findByRecommend(page,pagesize) ;
        if (pageResult == null) {
            pageResult = publishApi.queryRecommendList(page, pagesize, userId);
        }
        //3. 封装返回结果：MovementsVo
        this.setMovementsVo(pageResult);
        return ResponseEntity.ok(pageResult);
    }

    // 分页查询推荐数据
    private PageResult findByRecommend(Integer page, Integer pagesize) {
        //1. 先从redis中获取推荐动态的pid
        String key = "QUANZI_PUBLISH_RECOMMEND_" + UserHolder.getUserId();
        // value = 100092,82,18,20,20,22,23,25,24,33
        String value = redisTemplate.opsForValue().get(key);
        //2. 分割字符串
        String[] pids = value.split(",");
        int counts = pids.length;

        //3. 查询的开始下标
        int startIndex = (page-1) * pagesize;

        if(startIndex < pids.length) { //起始条数小于数据总数
            int endIndex = startIndex + pagesize - 1;
            if (endIndex >= pids.length) {
                endIndex = pids.length - 1;
            }
            List<Long> pidList = new ArrayList<>();   //本页查询的所有动态的pid列表
            for (int i = startIndex; i <= endIndex; i++) {
                pidList.add(Long.valueOf(pids[i]));
            }
            //本次分页的数据列表
            List<Publish> list = publishApi.findByPids(pidList);
            return new PageResult(page, pagesize, counts, list);
        }
        return null;
    }

    //用户动态
    public ResponseEntity<Object> queryUserPublishList(Integer page, Integer pageSize) {
        //得到当前用户
        User user = UserHolder.get();
        //根据当前用户id查到时间线分页对象
        PageResult result = publishApi.queryUserPublishList(user.getId(),page,pageSize);
        setMovementsVo(result);
        return ResponseEntity.ok(result);
    }

    // 抽取公用方法: 把List<Publish>封装为List<MovementsVo>，再设置到pageResult中
    private void setMovementsVo(PageResult pageResult) {
        //获取查询的动态列表
        List<Publish> publishList = (List<Publish>) pageResult.getItems();
        //创建vo集合、封装返回结果
        List<MovementsVo> voList = new ArrayList<>();
        if (publishList!=null && publishList.size()>0) {
            for (Publish publish : publishList) {
                // 创建并封装vo对象
                MovementsVo vo = new MovementsVo();
                // 对象拷贝：设置publis动态数据
                BeanUtils.copyProperties(publish,vo);
                vo.setId(publish.getId().toString());
                vo.setImageContent(publish.getMedias().toArray(new String[]{}));

                // 根据用户id查询
                UserInfo userInfo = userInfoApi.findById(publish.getUserId());
                if (userInfo != null) {
                    // 对象拷贝：设置userInfo动态数据
                    BeanUtils.copyProperties(userInfo,vo);
                    if (userInfo.getTags() != null) {
                        vo.setTags(userInfo.getTags().split(","));
                    }
                }
                // 设置动态的用户id
                vo.setUserId(publish.getUserId());
                vo.setDistance("50米");
                // 设置时间
                vo.setCreateDate(RelativeDateFormat.format(new Date(publish.getCreated())));

                // 如果该动态已经点赞，设置hasLike设置为1，前端App自动点亮图标
                String key = "publish_like_" + UserHolder.getUserId() + "_" +vo.getId();
                if (redisTemplate.hasKey(key)) {
                    vo.setHasLiked(1);
                } else {
                    vo.setHasLiked(0);
                }
                String keyLove = "publish_love_" + UserHolder.getUserId() + "_" +vo.getId();;
                if (redisTemplate.hasKey(keyLove)) {
                    vo.setHasLoved(1);
                }else{
                    vo.setHasLoved(0);
                }
                voList.add(vo);

            }
        }
        // 设置vo集合
        pageResult.setItems(voList);
    }

    /**
     * 接口名称：用户动态
     */
    public ResponseEntity<Object> queryAlbumList(int page, int pagesize, Long userId) {
        PageResult pageResult = publishApi.findByAlbum(userId,page,pagesize);
        setMovementsVo(pageResult);
        return ResponseEntity.ok(pageResult);
    }
}
