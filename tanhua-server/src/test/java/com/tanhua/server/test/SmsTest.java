package com.tanhua.server.test;

import com.tanhua.commons.template.SmsTemplate;
import com.tanhua.domain.db.User;
import com.tanhua.dubbo.api.UserApi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SmsTest {

    @Autowired
    private SmsTemplate smsTemplate;

    @Test
    public void sms() {
        smsTemplate.sendSms("18665591009","123456");
    }

}
