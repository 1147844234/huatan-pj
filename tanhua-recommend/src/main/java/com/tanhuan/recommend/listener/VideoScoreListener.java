package com.tanhuan.recommend.listener;

import com.alibaba.fastjson.JSON;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.mongo.PublishScore;
import com.tanhua.domain.mongo.VideoScore;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Component
@RocketMQMessageListener(topic = "tanhua-video2",consumerGroup = "tanhua-video2-group")
public class VideoScoreListener implements RocketMQListener<String> {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void onMessage(String message) {
        // 转换
        Map<String,String> map = JSON.parseObject(message, Map.class);
        // 获取消息内容
        Long vid = Long.parseLong(map.get("vid"));
        Integer type = Integer.parseInt(map.get("type") + "");
        Long userId = Long.parseLong(map.get("userId"));
        String videoId = map.get("videoId");

        // 创建操作评分对象
        VideoScore vs = new VideoScore();
        vs.setUserId(userId);
        vs.setVideoId(vid);
        vs.setDate(System.currentTimeMillis());
        //根据不同的type，设置不同的评分
        switch (type) {
            case 1: { //发布视频
                vs.setScore(2d);
                break;
            }
            case 2: { //视频点赞
                vs.setScore(5d);
                break;
            }
            case 3: { //取消点赞
                vs.setScore(-5d);
                break;
            }
            case 4: { //发布评论
                vs.setScore(10d);
                break;
            }
            default: {
                vs.setScore(0d);
                break;
            }
        }
        // 保存评分
        mongoTemplate.insert(vs);
    }
}
