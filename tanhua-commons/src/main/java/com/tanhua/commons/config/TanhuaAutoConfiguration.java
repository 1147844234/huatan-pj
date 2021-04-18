package com.tanhua.commons.config;

import com.tanhua.commons.properties.*;
import com.tanhua.commons.template.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        SmsProperties.class,
        OssProperties.class,
        AipFaceProperties.class,
        HuanXinProperties.class,
        HuaWeiUGCProperties.class
})
public class TanhuaAutoConfiguration {

    /**
     * 创建发送短信的模板对象
     * 1. @Bean 注解：自动把方法返回的对象加入容器
     * 2. 方法参数： 自动会去容器中找该类型的参数注入到方法参数中。
     */
    @Bean
    public SmsTemplate smsTemplate(SmsProperties smsProperties){
        return new SmsTemplate(smsProperties);
    }

    @Bean
    public OssTemplate ossTemplate(OssProperties ossProperties){
        return new OssTemplate(ossProperties);
    }

    @Bean
    public AipFaceTemplate aipFaceTemplate(AipFaceProperties properties){
        return new AipFaceTemplate(properties);
    }

    @Bean
    public HuanXinTemplate huanXinTemplate(HuanXinProperties properties){
        return new HuanXinTemplate(properties);
    }

    @Bean
    public HuaWeiUGCTemplate huaWeiUGCTemplate(HuaWeiUGCProperties properties) {
        return new HuaWeiUGCTemplate(properties);
    }
}
