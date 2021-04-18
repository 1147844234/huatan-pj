package com.tanhua.dubbo.api.mongo;

import com.tanhua.domain.vo.UserLocationVo;

import java.util.List;

public interface UserLocationApi {
    /**
     * 上报地理信息
     * @param userId 用户id
     * @param latitude 维度
     * @param longitude 经度
     * @param addrStr 地理位置
     */
    void saveLocation(Long userId, Double latitude, Double longitude, String addrStr);

    /**
     * 接口名称：搜附近
     */
    List<UserLocationVo> searNear(Long userId, Long distance);
}
