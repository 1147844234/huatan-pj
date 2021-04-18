package com.tanhua.manage.test;

import com.tanhua.manage.ManagerServerApplication;
import com.tanhua.manage.service.AdminService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest//(classes = ManagerServerApplication.class)
public class AdminServiceTest {
    @Autowired
    private AdminService adminService;

    @Test
    public void test() {
        System.out.println(adminService.getById(1));
    }
}
