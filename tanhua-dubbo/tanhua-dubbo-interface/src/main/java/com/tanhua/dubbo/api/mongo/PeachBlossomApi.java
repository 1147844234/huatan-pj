package com.tanhua.dubbo.api.mongo;

import com.tanhua.domain.mongo.PeachBlossom;
import com.tanhua.domain.mongo.RemainingTimes;
import org.bson.types.ObjectId;

import java.util.List;

public interface PeachBlossomApi {
    //桃花传音
    void save(PeachBlossom peachBlossom);

    //查所有语音
    List<PeachBlossom> findAll(Long userId);

    //修改state状态
    void update(ObjectId id, Integer state);

    //接收语音次数减1
    void timeMinusOne(Long userId);

    //查询当前登录用户剩余次数
    RemainingTimes findRemainingTimes(Long userId);

    //--------------------------拓展------------------------------
    //如果当前登录用户发布一条语音会获得次数+1
    void rimesPlusOne(Long userId, int i);

    //刷新接收次数
    void RefreshRimes();

    //发送语音次数加1
    void sendPlusOne(Long userId);

    //查询当前用户语音发送次数
    Integer findBySend(Long userId);

}
