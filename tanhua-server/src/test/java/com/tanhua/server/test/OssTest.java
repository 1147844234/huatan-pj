package com.tanhua.server.test;

import com.tanhua.commons.template.OssTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OssTest {

    @Autowired
    private OssTemplate ossTemplate;

    @Test
    public void oss() throws FileNotFoundException {
        String file = "F:\\1.jpg";
        String url = ossTemplate.upload(file, new FileInputStream(file));
        System.out.println("url = " + url);
    }

}
