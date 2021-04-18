package com.tanhua.server.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
    /**
     * 生成token
     */
    public static String createToken(String id,String mobile,String secret) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("mobile", mobile);
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256,secret).setClaims(map).compact();
    }
}
