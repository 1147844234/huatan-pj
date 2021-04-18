package com.tanhua.server.service;

import com.alibaba.fastjson.JSON;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.dubbo.api.mongo.PublishApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MovementsMQService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Reference
    private PublishApi publishApi;

    /**
     * 发布动态消息
     */
    public void publishMsg(String publishId) {
        this.sendMsg(publishId, 1);
    }

    /**
     * 浏览动态消息
     */
    public void queryPublishMsg(String publishId) {
        this.sendMsg(publishId, 2);
    }

    /**
     * 点赞动态消息
     */
    public void likePublishMsg(String publishId) {
        this.sendMsg(publishId, 3);
    }

    /**
     * 取消点赞动态消息
     */
    public void disLikePublishMsg(String publishId) {
        this.sendMsg(publishId, 6);
    }

    /**
     * 喜欢动态消息
     */
    public void lovePublishMsg(String publishId) {
        this.sendMsg(publishId, 4);
    }

    /**
     * 取消喜欢动态消息
     */
    public void disLovePublishMsg(String publishId) {
        this.sendMsg(publishId, 7);
    }

    /**
     * 评论动态消息
     */
    public void commentPublishMsg(String publishId) {
        this.sendMsg(publishId, 5);
    }

    /**
     * 发送消息
     * 1-发动态，2-浏览动态， 3-点赞， 4-喜欢， 5-评论，6-取消点赞，7-取消喜欢时候
     */
    public void sendMsg(String publishId,Integer type) {
        try {
            // 根据动态id查询
            Publish publish = publishApi.findById(publishId);
            // 准备数据
            Map<String,String> map = new HashMap<>();
            map.put("userId", UserHolder.getUserId().toString());
            map.put("publishId", publishId);
            map.put("type", type.toString());
            map.put("pid", publish.getPid().toString());
            // 发送消息
            rocketMQTemplate.convertAndSend("tanhua-quanzi2", JSON.toJSONString(map));
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
