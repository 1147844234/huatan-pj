package com.tanhua.dubbo.test;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.User;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.mapper.UserInfoMapper;
import com.tanhua.dubbo.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserInfoApiTest {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Test
    public void findByPage() {
        //1. 创建分页对象，封装分页参数: 当前页、页大小
        Page<UserInfo> page = new Page<>(1,2);
        //2. 分页查询,返回分页对象
        IPage<UserInfo> iPage = userInfoMapper.selectPage(page, null);

        System.out.println("总记录数： = " + iPage.getTotal());
        System.out.println("总页数： " + iPage.getPages());
        System.out.println("当前页数据： " + iPage.getRecords());
        System.out.println("当前页: " + iPage.getCurrent());
        System.out.println("页大小: " + iPage.getSize());
    }
    

}
