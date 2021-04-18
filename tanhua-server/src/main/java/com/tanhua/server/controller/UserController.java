package com.tanhua.server.controller;



import com.tanhua.domain.db.UserInfo;
import com.tanhua.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 接口名称：新用户---1填写资料
     * 接口路径：POST/user/loginReginfo
     * 需求分析：完善用户信息，保存到tb_user_info表中
     */
    @PostMapping("loginReginfo")
    public ResponseEntity<Object> saveUserInfo(
            @RequestBody UserInfo userInfo,
            @RequestHeader("Authorization")String token) {
        return userService.saveUserInfo(userInfo,token);
    }

    /**
     * 接口名称：新用户---2选取头像
     * 接口路径：POST/user/loginReginfo/head
     * 需求分析：修改tb_user_info表，设置头像
     */
    @PostMapping("loginReginfo/head")
    public ResponseEntity<Object> updateHead(
            MultipartFile headPhoto,@RequestHeader("Authorization") String token) throws IOException {
        return userService.updateHead(headPhoto,token);
    }
}
