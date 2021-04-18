package com.tanhua.server.service;

import com.alibaba.fastjson.JSON;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.mongo.Video;
import com.tanhua.dubbo.api.mongo.PublishApi;
import com.tanhua.dubbo.api.mongo.VideoApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class VideoMQService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Reference
    private VideoApi videoApi;


    /**
     * 发布视频
     */
    public void videoMsg(String videoId) {
        this.sendMsg(videoId, 1);
    }
    /**
     * 对视频点赞
     */
    public void likeVideoMsg(String videoId) {
        this.sendMsg(videoId, 2);
    }
    /**
     * 取消视频点赞
     */
    public void disLikeVideoMsg(String videoId) {
        this.sendMsg(videoId, 3);
    }
    /**
     * 评论视频
     */
    public void commentVideoMsg(String videoId) {
        this.sendMsg(videoId, 4);
    }

    /**
     * 发送消息
     *  参数：动态id
     *  参数type：
     *     type 1-发动态，2-点赞， 3-取消点赞，4-评论
     */
    public void sendMsg(String videoId,Integer type) {
        try {
            // 根据动态id查询
            Video video = videoApi.findById(videoId);
            // 准备数据
            Map<String,String> map = new HashMap<>();
            map.put("userId", UserHolder.getUserId().toString());
            map.put("videoId", videoId);
            map.put("type", type.toString());
            map.put("vid", video.getVid().toString());
            // 发送消息
            rocketMQTemplate.convertAndSend("tanhua-video2", JSON.toJSONString(map));
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
