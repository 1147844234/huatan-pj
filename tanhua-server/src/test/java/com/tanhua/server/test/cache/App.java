package com.tanhua.server.test.cache;

import com.tanhua.domain.db.UserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class App {
    @Autowired
    private UserInfoTestService userInfoTestService;

    @Test
    public void findAll() {
        System.out.println(userInfoTestService.findAll());
    }

    @Test
    public void update() {
        userInfoTestService.update();
    }

    @Test
    public void findById() {
        UserInfo userInfo = userInfoTestService.findById(1L);
        System.out.println("userInfo = " + userInfo);
    }

    @Test
    public void save() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(1L);
        userInfoTestService.save(userInfo);
    }
}
