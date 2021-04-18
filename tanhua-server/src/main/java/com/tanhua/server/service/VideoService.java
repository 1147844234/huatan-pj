package com.tanhua.server.service;
import com.alibaba.fastjson.JSON;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.commons.template.OssTemplate;
import com.tanhua.domain.mongo.FollowUser;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.utils.ComputeUtil;
import org.bson.types.ObjectId;

import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.FollowUser;
import com.tanhua.domain.mongo.Video;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.VideoVo;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.mongo.VideoApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class VideoService {

    @Reference
    private VideoApi videoApi;
    @Reference
    private UserInfoApi userInfoApi;
    @Autowired
    private OssTemplate ossTemplate;
    @Autowired
    private FastFileStorageClient storageClient;
    @Autowired
    private FdfsWebServer fdfsWebServer;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 接口名称：小视频列表
     */
    @Cacheable(value = "videoList",key = "#page + '_' + #pagesize")
    public PageResult queryVideoList(Integer page, Integer pagesize) {

        //1. 调用api，分页查询小视频, 获取分页对象
        // 【先从redis中查询推荐结果，没有查找再查询mongo】
        PageResult pageResult = findRecommend(page,pagesize);
        if (pageResult == null) {
            System.out.println(" 查询mongo数据库：............");
            pageResult = videoApi.findByPage(page, pagesize);
        }
        //2. 获取分页数据
        List<Video> videoList = (List<Video>) pageResult.getItems();
        //3. 创建vo集合，把分页数据转换为vo集合
        List<VideoVo> voList = new ArrayList<>();
        //4. 分装数据
        if (videoList != null && videoList.size()>0) {
            for (Video video : videoList) {
                // 4.1 创建vo对象
                VideoVo vo = new VideoVo();
                // 4.2 封装vo对象
                BeanUtils.copyProperties(video,vo);
                vo.setId(video.getId().toString());

                // 根据小视频的用户id查询
                UserInfo userInfo = userInfoApi.findById(video.getUserId());
                if (userInfo != null) {
                    BeanUtils.copyProperties(userInfo,vo);
                }
                // 设置返回数据中的签名：就是文本
                vo.setSignature(video.getText());
                vo.setCover(video.getPicUrl());
                String key = "followUser_" + UserHolder.getUserId() + "_" + video.getUserId();
                if (redisTemplate.hasKey(key)) {
                    vo.setHasFocus(1);
                }else{
                    vo.setHasFocus(0);
                }
                vo.setHasLiked(0);

                // 4.3 添加到集合
                voList.add(vo);
            }
        }
        //5. 设置vo集合到pageResult中
        pageResult.setItems(voList);
        return pageResult;
    }

    private PageResult findRecommend(Integer page, Integer pagesize) {
        //1. 先从redis中获取推荐视频的vid
        String key = "QUANZI_VIDEO_RECOMMEND_" + UserHolder.getUserId();
        // value = 18,20,20,22,4,10,23,25,24,10
        String value = redisTemplate.opsForValue().get(key);
        //2. 分割字符串
        String[] vids = value.split(",");
        int counts = vids.length;

        //3. 查询的开始下标
        int startIndex = (page-1) * pagesize;

        if(startIndex < vids.length) { //起始条数小于数据总数
            int endIndex = startIndex + pagesize - 1;
            if (endIndex >= vids.length) {
                endIndex = vids.length - 1;
            }
            List<Long> vidList = new ArrayList<>();   //本页查询的所有动态的pid列表
            for (int i = startIndex; i <= endIndex; i++) {
                vidList.add(Long.valueOf(vids[i]));
            }
            //本次分页的数据列表
            List<Video> list = videoApi.findByVids(vidList);
            return new PageResult(page, pagesize, counts, list);
        }
        return null;
    }

    @Autowired
    private VideoMQService videoMQService;

    /**
     * 接口名称：视频上传
     */
    @CacheEvict(value = "videoList",allEntries = true)
    public ResponseEntity<Object> uploadVideos(String text,
            MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
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
        // 1. 视频封面图片上传到阿里云
        String picUrl = ossTemplate.upload(
                videoThumbnail.getOriginalFilename(), videoThumbnail.getInputStream());

        // 2. 小视频上传到FasfDFS服务器
        // 2.1 获取视频文件名
        String videoName = videoFile.getOriginalFilename();
        StorePath storePath = storageClient.uploadFile(
                videoFile.getInputStream(),
                videoFile.getSize(),
                videoName.substring(videoName.lastIndexOf(".") + 1), null
        );
        // 2.2 拼接视频完整地址
        String videoUrl = fdfsWebServer.getWebServerUrl() + storePath.getFullPath();

        // 3. 创建封装video对象
        Video video = new Video();
        video.setCreated(System.currentTimeMillis());
        video.setUserId(UserHolder.getUserId());
        if (!StringUtils.isEmpty(text)) {
            video.setText(text);
        } else {
            video.setText("超级飞侠我爱你~");
        }
        video.setPicUrl(picUrl);
        video.setVideoUrl(videoUrl);

        // 4. 保存视频
        // 【设置主键id】
        video.setId(ObjectId.get());
        videoApi.save(video);

        // 【发送mq消息】
        videoMQService.videoMsg(video.getId().toString());
        return ResponseEntity.ok(null);
    }

    /**
     * 接口名称：视频用户关注
     */
    public ResponseEntity<Object> followUser(Long followUserId) {
        //1. 获取登陆用户id
        Long userId = UserHolder.getUserId();
        //2. 创建视频用户关注对象
        FollowUser followUser = new FollowUser();
        followUser.setCreated(System.currentTimeMillis());
        followUser.setUserId(userId);
        followUser.setFollowUserId(followUserId);

        //3. 保存
        videoApi.followUser(followUser);

        //4. 保存当前视频的关注状态; 再在小视频列表中设置关注状态
        String key = "followUser_" + userId + "_" + followUserId;
        redisTemplate.opsForValue().set(key,"1");
        return ResponseEntity.ok(null);
    }

    /**
     * 接口名称：视频用户关注 - 取消
     */
    public ResponseEntity<Object> unfollowUser(Long followUserId) {
        Long userId = UserHolder.getUserId();
        //1. 创建封装对象
        FollowUser followUser = new FollowUser();
        followUser.setUserId(userId);
        followUser.setFollowUserId(followUserId);
        //2. 调用api，取消关注
        videoApi.unfollowUser(followUser);
        //3. 删除redis中关注用户的状态
        String key = "followUser_" + userId + "_" + followUserId;
        redisTemplate.delete(key);
        return ResponseEntity.ok(null);
    }
}
