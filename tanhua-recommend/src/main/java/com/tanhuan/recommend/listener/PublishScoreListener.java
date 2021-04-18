package com.tanhuan.recommend.listener;
import com.tanhua.domain.mongo.Publish;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import com.alibaba.fastjson.JSON;
import com.tanhua.domain.mongo.PublishScore;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Component
@RocketMQMessageListener(topic = "tanhua-quanzi2",consumerGroup = "tanhua-quanzi-group2")
public class PublishScoreListener implements RocketMQListener<String> {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void onMessage(String message) {
        // 转换
        Map<String,String> map = JSON.parseObject(message, Map.class);
        // 获取消息内容
        Long pid = Long.parseLong(map.get("pid"));
        Integer type = Integer.parseInt(map.get("type") + "");
        Long userId = Long.parseLong(map.get("userId"));
        String publishId = map.get("publishId");

        // 创建操作评分对象
        PublishScore ps = new PublishScore();
        ps.setUserId(userId);
        ps.setPublishId(pid);
        ps.setDate(System.currentTimeMillis());
        //4、根据不同的type，设置不同的评分
        //1-发动态，2-浏览动态， 3-点赞， 4-喜欢， 5-评论，6-取消点赞，7-取消喜欢
        switch (type) {
            case 1: {//1-发动态
                int score = 0;
                Publish publish = this.mongoTemplate.findById(new ObjectId(publishId), Publish.class);
                if (StringUtils.length(publish.getTextContent()) < 50) {
                    score += 1;
                } else if (StringUtils.length(publish.getTextContent()) < 100) {
                    score += 2;
                } else if (StringUtils.length(publish.getTextContent()) >= 100) {
                    score += 3;
                }
                if (!CollectionUtils.isEmpty(publish.getMedias())) {
                    score += publish.getMedias().size();
                }
                ps.setScore(Double.valueOf(score));
                break;
            }
            case 2: {//2-浏览动态
                ps.setScore(1d);
                break;
            }
            case 3: {//3-点赞
                ps.setScore(5d);
                break;
            }
            case 4: {// 4-喜欢
                ps.setScore(8d);
                break;
            }
            case 5: {// 5-评论
                ps.setScore(10d);
                break;
            }
            case 6: {//6-取消点赞
                ps.setScore(-5d);
                break;
            }
            case 7: {//7-取消喜欢
                ps.setScore(-8d);
                break;
            }
            default: {
                ps.setScore(0d);
                break;
            }
        }

        // 保存评分
        mongoTemplate.insert(ps);

    }
}
