package com.tanhua.server.controller;

import com.tanhua.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("user")
@Slf4j
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 接口名称：登录第一步---手机号登录
     * 接口路径：POST/user/login
     * 需求描述：输入手机号码，发送验证码，存储到redis中,设置有效时间5分钟
     */
    @PostMapping("login")
    public ResponseEntity<Object> login(@RequestBody Map<String,String> map){
        // 获取请求参数
        String phone = map.get("phone");
        // 调用service处理业务
        return userService.login(phone);
    }

    /**
     * 接口名称：登录第二步---验证码校验
     * 接口路径：POST/user/loginVerification
     * 需求分析：从redis获取验证码校验是否登陆成功 （自动注册）
     */
    @PostMapping("/loginVerification")
    public ResponseEntity<Object> loginVerification(@RequestBody Map<String,String> map){
        String phone = map.get("phone");
        String verificationCode = map.get("verificationCode");
        log.info("登录第二步---验证码校验，请求参数{},{}",phone,verificationCode);
        return userService.loginVerification(phone,verificationCode);
    }
}
