package com.tanhua.server.test.huanxin;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class HuanxinTest {
    /**
     * 目标：通过RestTemplate对象，发送一个http请求获取环信返回的token
     * 参考：环信官网，服务端集成，用户体系集成
     */
    @Test
    public void getToken() {
        // 发送rest风格的http请求的工具类
        RestTemplate restTemplate = new RestTemplate();

        // 发送请求:参数1 url
        String url = "http://a1.easemob.com/1124210104046145/demo/token";
        // 发送请求:参数2 请求的数据，用map封装
        Map<String,Object> map = new HashMap<>();
        map.put("grant_type","client_credentials");
        map.put("client_id","YXA6D3iKYg76TA-2n4T8wa6yrg");
        map.put("client_secret","YXA6IX1j7MvZVSvxuewaO8GwHl7GvNc");
        // map转换为json
        String requestBody = JSON.toJSONString(map);

        // 发送请求
        ResponseEntity<String> entity = restTemplate.postForEntity(url, requestBody, String.class);

        // 获取响应的数据
        String body = entity.getBody();
        System.out.println("body = " + body);

        // 从响应数据中获取token  json--->map
        Map<String,Object> result = JSON.parseObject(body, Map.class);
        String token = (String) result.get("access_token");
        System.out.println("token = " + token);

    }
}

















