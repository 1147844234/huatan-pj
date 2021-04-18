package com.tanhua.server.test.huanxin;

import com.tanhua.commons.template.HuanXinTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class HuanxinTemplateTest {

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @Test
    public void register() {
        // 测试：注册用户到环信
        huanXinTemplate.register(6L);
    }

}
