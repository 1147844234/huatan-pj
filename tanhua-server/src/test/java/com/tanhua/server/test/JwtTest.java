package com.tanhua.server.test;

import com.tanhua.domain.db.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class JwtTest {
    /**
     * 通过JWT生成Token
     * 1. JWT 分布式认证的解决方案
     * 2. 认证流程
     * 2.1 用户登陆-->服务端生成token--->发给给客户端
     * 2.2 客户端访问资源，需要在请求头中携带token
     * 2.3 服务端获取请求头中的token，解析认证
     */
    @Test
    public void jwtCreate() {
        //1. 准备数据
        Map<String, Object> map = new HashMap<>();
        map.put("id", 100);
        map.put("mobile", "18000110011");

        //2. 密钥
        String secret = "itcast";
        //3. 生成token
        String compact = Jwts.builder().signWith(SignatureAlgorithm.HS256, secret)
                .setClaims(map).compact();
        System.out.println("compact = " + compact);

    }


    // 解析token
    @Test
    public void tokenParse() {
        String secret = "itcast";
        //token数据
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJtb2JpbGUiOiIxODAwMDExMDAxMSIsImlkIjoxMDB9.zeL_Mwng5Nh5OUU9NgMLtO0qCcKLvM9Y4MiQT_rRF_s";
        //解析token
        Map<String, Object> map =
                (Map<String, Object>) Jwts.parser().setSigningKey(secret).parse(token).getBody();
        // 测试
        System.out.println("map = " + map);

    }
}
