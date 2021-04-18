package com.tanhua.manage.listener;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.tanhua.manage.domain.Log;
import com.tanhua.manage.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 消息监听器，监听日志消息
 */
@Component
@RocketMQMessageListener(topic = "tanhua-log2",consumerGroup = "tanhua-log2-consumer")
@Slf4j
public class LogMessageListener implements RocketMQListener<String> {
    @Autowired
    private LogService logService;
    @Override
    public void onMessage(String message) {
        log.info("监听器中消息内容：" + message);
        //1. 日志的json字符串转换为map对象
        Map<String,String> map = JSON.parseObject(message, Map.class);
        //2. 获取数据
        Long userId = Long.parseLong(map.get("userId")+"");
        String type = map.get("type");
        String date = map.get("date");
        //3. 创建日志对象，封装数据
        Log log = new Log();
        log.setUserId(userId);
        log.setLogTime(date);
        log.setType(type);
        log.setCreated(new Date());
        log.setUpdated(new Date());

        //4. 保存
        logService.save(log);
    }
}
