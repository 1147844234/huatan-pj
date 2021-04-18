package com.tanhua.server.service;

import com.tanhua.dubbo.api.mongo.UserLocationApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class BaiduService {
    @Reference
    private UserLocationApi userLocationApi;
    /**
     * 接口名称：上报地理信息
     * 接口路径：POST/baidu/location
     */
    public ResponseEntity<Object> saveLocation(Double latitude, Double longitude, String addrStr) {
        Long userId = UserHolder.getUserId();
        userLocationApi.saveLocation(userId,latitude,longitude,addrStr);
        return ResponseEntity.ok(null);
    }
}
