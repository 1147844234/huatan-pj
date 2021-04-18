package com.tanhua.server.controller;

import com.tanhua.domain.db.Settings;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.SettingsService;
import com.tanhua.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("users")
@Slf4j
public class SettingsController {
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private UserService userService;

    /**
     * 接口名称：用户通用设置
     * 接口路径：GET/users/settings
     * 需求分析：在通用设置中，查询通知设置
     */
    @GetMapping("settings")
    public ResponseEntity<Object> querySettings(){
        return settingsService.querySettings();
    }

    /**
     * 接口名称：通知设置 - 保存
     * 接口路径：POST/users/notifications/setting
     * 需求分析：保存或者修改通知设置
     */
    @PostMapping("notifications/setting")
    public ResponseEntity<Object> saveNotification(@RequestBody Settings settings) {
        return settingsService.saveNotification(settings);
    }

    /**
     * 接口名称：设置陌生人问题 - 保存
     * 接口路径：POST/users/questions
     * 需求分析：添加或修改陌生人问题 tb_question
     */
    @PostMapping("questions")
    public ResponseEntity<Object> saveQuestion(@RequestBody Map<String,String> map){
        log.info("接口名称：设置陌生人问题 - 保存");
        String content = map.get("content");
        return settingsService.saveQuestion(content);
    }

    /**
     * 接口名称：黑名单 - 翻页列表
     * 接口路径：GET/users/blacklist
     * 需求分析：分页查询黑名单用户信息，需要表关联查询
     */
    @GetMapping("blacklist")
    public ResponseEntity<Object> blacklist(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pagesize){
        log.info("接口名称：黑名单 - 翻页列表");
        return settingsService.blacklist(page,pagesize);
    }

    /**
     * 接口名称：黑名单 - 移除
     * 接口路径：DELETE/users/blacklist/:uid
     * 需求描述：根据登陆用户id、黑名单用户id实现移除黑名单
     */
    @DeleteMapping("blacklist/{uid}")
    public ResponseEntity<Object> deleteBlacklist(@PathVariable("uid") String blackUserId){
        log.info("黑名单 - 移除");
        return settingsService.deleteBlacklist(blackUserId);
    }

    /**
     * 接口名称：修改手机号- 1 发送短信验证码
     * 接口路径：POST/users/phone/sendVerificationCode
     */
    @PostMapping("phone/sendVerificationCode")
    public ResponseEntity<Object> sendVerificationCode(){
        String phone = UserHolder.get().getMobile();
        return userService.login(phone);
    }

    /**
     * 接口名称：修改手机号 - 2 校验验证码
     * 接口路径：POST/users/phone/checkVerificationCode
     */
    @PostMapping("/phone/checkVerificationCode")
    public ResponseEntity<Object> checkVerificationCode(
            @RequestBody Map<String,String> paramMap){
        String code = paramMap.get("verificationCode");
        return userService.checkVerificationCode(code);
    }

    /**
     * 接口名称：修改手机号 - 3 保存
     * 接口路径：POST/users/phone
     */
    @PostMapping("phone")
    public ResponseEntity<Object> phone(
            @RequestBody Map<String, String> paramMap){
        String phone = paramMap.get("phone");
        return userService.updateUserPhone(phone);
    }
}
