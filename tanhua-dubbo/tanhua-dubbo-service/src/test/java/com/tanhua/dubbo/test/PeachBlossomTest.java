package com.tanhua.dubbo.test;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.domain.db.User;
import com.tanhua.domain.mongo.PeachBlossom;
import com.tanhua.domain.mongo.RemainingTimes;
import com.tanhua.dubbo.mapper.UserMapper;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PeachBlossomTest {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired(required = false)
    private UserMapper userMapper;

    //桃花传音测试数据
    @Test
    public void test() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        System.out.println(userList);

        String url = "https://dean-tanhua.oss-cn-guangzhou.aliyuncs.com/images/2021/04/15/c91b2906-f411-4593-96bc-2a7fb2499406.m4a";

        for (int i = 0; i <= 500; i++) {
            PeachBlossom peachBlossom = new PeachBlossom();
            peachBlossom.setId(ObjectId.get());
            peachBlossom.setSoundUrl(url);
            Integer a = userList.size();
            peachBlossom.setUserId(Long.valueOf(new Random().nextInt(a) + 1));
            peachBlossom.setState(0);
            mongoTemplate.save(peachBlossom);
        }
    }


    //桃花传音用户次数测试数据
    @Test
    public void test3() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        System.out.println(userList);

        for (int i = 1; i < 100; i++) {
            RemainingTimes remainingTimes = new RemainingTimes();
            remainingTimes.setId(ObjectId.get());
            remainingTimes.setRimes(10);
            remainingTimes.setUserId(Long.valueOf(i));
            remainingTimes.setSend(10);
            mongoTemplate.save(remainingTimes);
        }
    }

}
