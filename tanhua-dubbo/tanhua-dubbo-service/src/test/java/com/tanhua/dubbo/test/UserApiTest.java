package com.tanhua.dubbo.test;

import com.tanhua.domain.db.User;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.api.mongo.RecommendUserApi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserApiTest {
    /**
     * 注入dubbo服务对象
     * 1. 如果在服务消费者中注入dubbo服务对象，使用@Reference  (接口代理)
     * 2. 如果在服务提供者中注入dubbo服务对象，使用@Autowired
     *
     */
    @Autowired
    private UserApi userApi;

    @Test
    public void save() {
        User user = new User();
        user.setMobile("18000110011");
        user.setPassword("123456");
        user.setCreated(new Date());
        user.setUpdated(new Date());
        // 保存
        System.out.println("user = " + user);
        userApi.save(user);
        System.out.println("user = " + user);
    }

    @Test
    public void findByMobile() {
        System.out.println(userApi.findByMobile("18000110011"));
    }



}
