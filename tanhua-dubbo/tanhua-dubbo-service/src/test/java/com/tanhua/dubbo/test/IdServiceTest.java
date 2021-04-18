package com.tanhua.dubbo.test;

import com.tanhua.domain.db.User;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.utils.IdService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class IdServiceTest {
    @Autowired
    private IdService idService;

    @Test
    public void getGeneratorId() {
        Long id = idService.getNextId("test");
        System.out.println("id = " + id);
    }

    @Test
    public void getGeneratorId2() {
        Long id = idService.getNextId("video");
        System.out.println("id = " + id);
    }



}
