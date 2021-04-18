package com.tanhua.manage.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import com.tanhua.manage.domain.Admin;
import com.tanhua.manage.interceptor.AdminHolder;
import com.tanhua.manage.service.AdminService;
import com.tanhua.manage.vo.AdminVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("system/users")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 接口名称：用户登录验证码图片
     * 接口路径：GET/system/users/verification
     * 需求描述：
     *    1. 访问首页，页面发送生成验证码的请求 （verification）， 携带uuid
     *    2. 后台生成验证码，保存验证码到redis, key就是uuid， 返回验证码
     *    3. 页面登陆时候发送登陆请求，携带uuid，后台根据uuid获取redis中验证码，校验
     */
    @GetMapping("verification")
    public void verification(String uuid, HttpServletResponse response) throws IOException {
        // 通过hutool工具类，生成验证码
        CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(299, 97);
        //获取验证码
        String code = circleCaptcha.getCode();
        System.out.println("code = " + code);
        // 把验证码存储到redis中
        adminService.saveCap(uuid,code);
        // 把验证码通过response对象响应给客户端浏览器
        circleCaptcha.write(response.getOutputStream());
    }

    /**
     * 接口名称：用户登录
     * 接口路径：POST/system/users/login
     */
    @PostMapping("login")
    public ResponseEntity<Object> login(@RequestBody Map<String,String> map) {
        return adminService.login(map);
    }

    /**
     * 接口名称：用户基本信息
     * 接口路径：POST/system/users/profile
     * 需求描述：根据用户id查询返回id、username、avatar
     */
    @PostMapping("profile")
    public ResponseEntity<Object> findById() {
        // 获取登陆的用户
        Admin admin = AdminHolder.getAdmin();
        // 创建返回的vo对象
        AdminVo vo = new AdminVo();
        // 对象拷贝
        BeanUtils.copyProperties(admin,vo);
        return ResponseEntity.ok(vo);
    }

    /**
     * 接口名称：用户登出
     * 接口路径：POST/system/users/logout
     * 需求描述：删除redis中的token
     */
    @PostMapping("logout")
    public ResponseEntity<Object> logout(@RequestHeader("Authorization") String token){
        token = token.replace("Bearer ", "");
        return adminService.logout(token);
    }
}
