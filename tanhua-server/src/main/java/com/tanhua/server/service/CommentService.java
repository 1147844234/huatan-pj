package com.tanhua.server.service;

import com.alibaba.fastjson.JSON;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.mongo.Visitors;
import com.tanhua.domain.vo.*;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.mongo.CommentApi;
import com.tanhua.dubbo.api.mongo.PublishApi;
import com.tanhua.dubbo.api.mongo.VisitorsApi;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.utils.ComputeUtil;
import com.tanhua.server.utils.RelativeDateFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {

    @Reference
    private CommentApi commentApi;
    @Reference
    private PublishApi publishApi;
    @Reference
    private UserInfoApi userInfoApi;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 接口名称：动态-点赞
     * 接口路径：GET/movements/:id/like
     */
    public ResponseEntity<Object> likeComment(String publishId) {
        //1. 创建Comment对象, 封装点赞数据
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(publishId));
        comment.setCommentType(1);
        comment.setPubType(1);
        comment.setUserId(UserHolder.getUserId());
        comment.setCreated(System.currentTimeMillis());

        //2. 保存点赞数据quanzi_comment，获取总的点赞数量
        long count = commentApi.save(comment);

        //3. 当前动态已经点赞，存储点赞标记到redis；再在动态中获取redis中点赞数据设置hasLike=1.
        String key = "publish_like_" + UserHolder.getUserId() + "_" +publishId;
        redisTemplate.opsForValue().set(key,"1");
        return ResponseEntity.ok(count);
    }

    /**
     * 接口名称：动态-取消点赞
     * 接口路径：GET/movements/:id/dislike
     */
    public ResponseEntity<Object> dislikeComment(String publishId) {
        //1. 创建评论对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(publishId));
        comment.setCommentType(1);
        comment.setUserId(UserHolder.getUserId());
        //2. 取消点赞，删除评论表数据
        long count = commentApi.delete(comment);
        //3. 删除redis中该动态的点赞标记
        String key = "publish_like_" + UserHolder.getUserId() + "_" +publishId;
        redisTemplate.delete(key);
        return ResponseEntity.ok(count);
    }

    /**
     * 接口名称：动态-喜欢
     * 接口路径：GET/movements/:id/love
     */
    public ResponseEntity<Object> loveComment(String publishId) {
        //1. 创建Comment对象, 封装喜欢数据
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(publishId));
        comment.setCommentType(3);  // 喜欢评论
        comment.setPubType(1);
        comment.setUserId(UserHolder.getUserId());
        comment.setCreated(System.currentTimeMillis());

        //2. 保存喜欢数据quanzi_comment，获取总的喜欢数量
        long count = commentApi.save(comment);

        //3. 当前动态已经点赞，存储点赞标记到redis；再在动态中获取redis中点赞数据设置hasLike=1.
        String key = "publish_love_" + UserHolder.getUserId() + "_" +publishId;
        redisTemplate.opsForValue().set(key,"1");
        return ResponseEntity.ok(count);
    }

    /**
     * 接口名称：动态-取消喜欢
     * 接口路径：GET/movements/:id/unlove
     */
    public ResponseEntity<Object> unloveComment(String publishId) {
        //1. 创建评论对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(publishId));
        comment.setCommentType(3);
        comment.setUserId(UserHolder.getUserId());
        //2. 取消喜欢，删除评论表数据
        long count = commentApi.delete(comment);
        //3. 删除redis中该动态的喜欢标记
        String key = "publish_love_" + UserHolder.getUserId() + "_" +publishId;
        redisTemplate.delete(key);
        return ResponseEntity.ok(count);
    }

    /**
     * 接口名称：单条动态
     * 需求分析：根据publishId查询单条动态，再把查询的Publish对象封装为MovementsVo对象
     */
    public ResponseEntity<Object> queryMovementsById(String publishId) {
        //1. 根据动态id查询
        Publish publish = publishApi.findById(publishId);
        //2. 创建vo对象，publish--->vo
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
        return ResponseEntity.ok(vo);
    }

    /**
     * 接口名称：评论列表
     */
    public ResponseEntity<Object> queryCommentsList(
            String movementId, Integer page, Integer pagesize) {
        //1. 分页查询评论列表，查询条件：动态id、commentType
        PageResult pageResult = commentApi.queryCommentsList(movementId,page,pagesize);
        //2. 获取查询的分页数据
        List<Comment> commentList = (List<Comment>) pageResult.getItems();
        //3. 创建返回的vo集合
        List<CommentVo> voList = new ArrayList<>();
        //4. 封装vo集合
        if (commentList != null && commentList.size()>0) {
            for (Comment comment : commentList) {
                // 创建vo对象
                CommentVo vo = new CommentVo();
                // 根据用户id查询
                UserInfo userInfo = userInfoApi.findById(comment.getUserId());
                if (userInfo != null) {
                    BeanUtils.copyProperties(userInfo,vo);
                }
                // 设置评论id
                vo.setId(comment.getId().toString());
                // 设置评论内容
                vo.setContent(comment.getContent());
                // 设置评论时间
                vo.setCreateDate(new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date(comment.getCreated())));
                // 点赞数量、是否点赞给默认值
                vo.setLikeCount(0);
                vo.setHasLiked(0);

                // 添加到集合
                voList.add(vo);
            }
        }
        //5. 把vo集合设置到分页对象中并返回
        pageResult.setItems(voList);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 接口名称：评论-提交
     * 接口路径：POST/comments
     */
    public ResponseEntity<Object> saveComments(String publishId, String content) {
        Long userId = UserHolder.getUserId();
        //1. 构建评论对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(publishId));
        comment.setCommentType(2);
        comment.setPubType(1);
        comment.setContent(content);
        comment.setUserId(userId);
        comment.setCreated(System.currentTimeMillis());
        //判断是否被封禁发言,并给出提示
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
            //如果为冻结发言才进入下面的操作
            if ("2".equals(freezingRange)) {
                if ("1".equals(freezingTime)) {
                    //计算解封时间
                    String afterThree = ComputeUtil.offsetDay(date, 3);
                    //提示冻结时间
                    return ResponseEntity.status(500).body(ErrorResult.freezeComment("3", afterThree));
                } else if ("2".equals(freezingTime)) {
                    //计算解封时间
                    String afterThree = ComputeUtil.offsetDay(date, 7);
                    //提示冻结时间
                    return ResponseEntity.status(500).body(ErrorResult.freezeComment("7", afterThree));
                } else {
                    //提示冻结时间
                    return ResponseEntity.status(500).body(ErrorResult.freezeCommentNever());
                }
            }
        }else {
            //如果该键已过期,则解封用户,设置userinfo的状态
            UserInfo userInfo = userInfoApi.findById(userId);
            userInfo.setUserStatus("1");
            userInfoApi.updateUserStatus(userInfo);
        }
        //直接保存评论
        commentApi.save(comment);
        return ResponseEntity.ok(null);
    }


    @Reference
    private VisitorsApi visitorsApi;
    /**
     * 接口名称：谁看过我
     * 接口路径：GET/movements/visitors
     * 需求描述：第一次显示最近5位访客；再次访问显示最近的访客
     */
    public ResponseEntity<Object> queryVisitorList() {

        //1. 获取登陆用户
        Long userId = UserHolder.getUserId();

        //2. 先从redis中获取上次访问时间
        String key = "visitors_" + userId;
        String time = redisTemplate.opsForValue().get(key);

        List<Visitors> visitorsList = null;

        //3. 调用api查询最近访客
        if (StringUtils.isEmpty(time)) {
            // 3.1. 如果是第一次访问，查询最近5位访客
            visitorsList = visitorsApi.queryVisitorList(userId, 5);
        } else {
            // 3.2. 再次访问显示最近的访客
            visitorsList = visitorsApi.queryVisitorList(userId, Long.parseLong(time));
        }

        //4. 记录访问时间到redis
        redisTemplate.opsForValue().set(key,System.currentTimeMillis()+"");

        //5. 创建vo集合，封装数据
        List<VisitorsVo> voList = new ArrayList<>();
        if (visitorsList != null && visitorsList.size()>0) {
            for (Visitors visitors : visitorsList) {
                //5.1 创建vo
                VisitorsVo vo = new VisitorsVo();
                //5.2 根据最近访客visitorUserId查询用户
                UserInfo userInfo = userInfoApi.findById(visitors.getVisitorUserId());
                if (userInfo != null) {
                    BeanUtils.copyProperties(userInfo,vo);
                    if (userInfo.getTags() != null) {
                        vo.setTags(userInfo.getTags().split(","));
                    }
                }
                vo.setId(visitors.getVisitorUserId());
                vo.setFateValue(visitors.getScore().intValue());
                //5.3 添加到集合
                voList.add(vo);
            }
        }
        return ResponseEntity.ok(voList);
    }
}



















