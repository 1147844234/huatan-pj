package com.tanhua.manage.listener;

import com.tanhua.commons.template.HuaWeiUGCTemplate;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.dubbo.api.mongo.PublishApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 消息监听器，监听动态消息：动态id
 */
@Component
@RocketMQMessageListener(topic = "tanhua-publish2",consumerGroup = "tanhua-publish2-consumer")
@Slf4j
public class PublishMessageListener implements RocketMQListener<String> {
    // 动态审核
    @Autowired
    private HuaWeiUGCTemplate huaWeiUGCTemplate;
    // 注入动态Api
    @Reference
    private PublishApi publishApi;

    @Override
    public void onMessage(String publishId) {
        log.info("监听器中动态ID：" + publishId);
        //1. 根据动态id查询
        Publish publish = publishApi.findById(publishId);
        //2. 判断: 动态对象不为空，且是未审核状态
        if (publish != null && publish.getState() == 0) {
            //3. 获取动态文本内容、调用华为云进行文字审核
            String textContent = publish.getTextContent();
            boolean contentCheck = huaWeiUGCTemplate.textContentCheck(textContent);
            // 状态默认为审核失败
            Integer state = 2;
            //4. 判断：内容审核通过，进行图片审核
            if (contentCheck) {
                List<String> medias = publish.getMedias();
                // 图片审核
                boolean imgCheck =
                        huaWeiUGCTemplate.imageContentCheck(medias.toArray(new String[]{}));
                //5. 判断：图片审核
                if (imgCheck) {
                    state = 1;
                }
            }
            //6. 修改状态
            publishApi.updateState(publishId,state);
        }
    }
}
