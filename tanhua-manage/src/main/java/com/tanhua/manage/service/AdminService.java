package com.tanhua.manage.service;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tanhua.manage.domain.Admin;
import com.tanhua.manage.exception.BusinessException;
import com.tanhua.manage.mapper.AdminMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Mybatis-plus 提供开发效率：
 * 1. BaseMapper 在数据访问层提供了crud的通用方法
 * 2. ServiceImpl 在业务员层提供了crud的通用方法
 */
@Service
public class AdminService extends ServiceImpl<AdminMapper, Admin> {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    // 验证码的前缀
    private final String MANAGE_CAP = "MANAGE_CAP_";
    // token的前缀
    private final String MANAGE_TOKEN = "MANAGE_TOKEN_";
    @Value("${tanhua.secret}")
    private String secret;

    /**
     * 把验证码存储到redis中
     */
    public void saveCap(String uuid, String code) {
        String key = MANAGE_CAP + uuid;
        redisTemplate.opsForValue().set(key,code, Duration.ofMinutes(5));
    }

    /**
     * 接口名称：用户登录
     * 实现步骤：
     * 1、获取请求参数：username、password、uuid、verificationCode
     * 2、验证码校验
     *    通过：执行下一步
     *    不通过：返回自定义异常类、交给全局异常处理
     * 3、校验用户名密码
     * 4、生成token并保存到redis
     * 5、返回token
     */
    public ResponseEntity<Object> login(Map<String, String> map) {
        //1、获取请求参数：
        String username = map.get("username");
        String password = map.get("password");
        String verificationCode = map.get("verificationCode");
        String uuid = map.get("uuid");

        //2、验证码校验
        //2.1 从redis获取验证码
        String key = MANAGE_CAP + uuid;
        String redisCode = redisTemplate.opsForValue().get(key);
        //2.2 与用户输入的验证码进行校验 (verificationCode)
        if (StringUtils.isEmpty(redisCode) || !redisCode.equals(verificationCode)) {
            // 返回自定义异常、最后会被spring全局异常捕获处理
            throw new BusinessException("验证码校验失败！");
        }
        //2.3 校验完毕，删除验证码
        redisTemplate.delete(key);

        //3、校验用户名密码
        //3.1 根据用户名查询
        Admin admin = query().eq("username", username).one();
        //3.2 判断用户名是否存在
        if (admin == null) {
            throw new BusinessException("用户名输入错误！");
        }
        //3.3 校验密码：获取数据库中的密码（加密）、与用户输入的加密后的密码
        if (!SecureUtil.md5(password).equals(admin.getPassword())) {
            throw new BusinessException("密码错误！");
        }

        //4、生成token并保存到redis
        //4.0 创建加密的数据
        Map<String,Object> claimsMap = new HashMap<>();
        claimsMap.put("id",admin.getId());
        claimsMap.put("username",admin.getUsername());
        //4.1 通过jwt生成token
        String token = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256,secret).setClaims(claimsMap).compact();
        //4.2 对象转换为json
        String adminJson = JSON.toJSONString(admin);
        //4.3 token存储到redis中，存储的值就是登陆的对象的json格式
        redisTemplate.opsForValue().set(MANAGE_TOKEN+token,adminJson,Duration.ofHours(4));

        //5、返回token
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("token",token);
        return ResponseEntity.ok(resultMap);
    }

    /**
     * 根据token查询用户
     */
    public Admin findUserByToken(String token) {
        // 根据tonken从redis中获取存储的用户的json字符串
        String adminJson = redisTemplate.opsForValue().get(MANAGE_TOKEN + token);
        // json字符串转换为对象
        Admin admin = JSON.parseObject(adminJson, Admin.class);
        return admin;
    }

    public ResponseEntity<Object> logout(String token) {
        redisTemplate.delete(MANAGE_TOKEN+token);
        return ResponseEntity.ok(null);
    }
}
