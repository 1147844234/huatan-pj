package com.tanhua.manage.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.manage.domain.Freeze;
import com.tanhua.manage.domain.UnFreeze;
import com.tanhua.manage.mapper.FreezeMapper;
import com.tanhua.manage.mapper.UnFreezeMapper;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class FreezeService extends ServiceImpl<FreezeMapper, Freeze> {

    @Reference
    private UserInfoApi userInfoApi;
    @Autowired
    private UnFreezeMapper unFreezeMapper;
    @Autowired
    private FreezeMapper freezeMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 冻结用户
     */
    public ResponseEntity<Object> freezeUser(Map<String, Object> map) {
        //获取参数
        Integer userId = (Integer) map.get("userId");
        String freezingTime = (String) map.get("freezingTime");
        String freezingRange = (String) map.get("freezingRange");
        String reasonsForFreezing = (String) map.get("reasonsForFreezing");
        String frozenRemarks = (String) map.get("frozenRemarks");
        //保存冻结对象到数据库
        Freeze freeze = new Freeze();
        freeze.setUserId(userId.longValue());
        freeze.setFreezingTime(Integer.valueOf(freezingTime));
        freeze.setFreezingRange(Integer.valueOf(freezingRange));
        freeze.setReasonsForFreezing(reasonsForFreezing);
        freeze.setFrozenRemarks(frozenRemarks);
        boolean result = save(freeze);
        //如果保存成功 则返回操作成功消息
        String message = "操作失败";
        if (result) {
            //查询得到当前用户对象
            UserInfo userInfo = userInfoApi.findById(userId.longValue());
            //改变userinfo中用户的状态
            userInfo.setUserStatus("2");
            userInfoApi.updateUserStatus(userInfo);
            message = "操作成功";
            //------------------将冻结时间和内容保存到redis中----------------------
            String key = "Freeze_User_"+userId;
            //保存冻结时间和冻结范围到redis中
            HashMap<String, Object> freezeMap = new HashMap<>();
            freezeMap.put("freezingTime",freezingTime);
            freezeMap.put("freezingRange",freezingRange);
            freezeMap.put("nowTime",new Date());
            String freezeJson = JSON.toJSONString(freezeMap);
            if(!redisTemplate.hasKey(key)){
                if("1".equals(freezingTime)){
                    redisTemplate.opsForValue().set(key,freezeJson,Duration.ofDays(3L));
                }else if("2".equals(freezingTime)){
                    redisTemplate.opsForValue().set(key,freezeJson,Duration.ofDays(7L));
                }else {
                    redisTemplate.opsForValue().set(key,freezeJson);
                }
            }
        }

        //封装操作结果并返回
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("message", message);
        return ResponseEntity.ok(messageMap);
    }


    /**
     * 解冻操作
     */
    public ResponseEntity<Object> unFreezeUser(Map<String, Object> map) {
        //获取参数
        Integer userId = (Integer) map.get("userId");
        String reasonsForThawing = (String) map.get("reasonsForThawing");
        String frozenRemarks = (String) map.get("frozenRemarks");
        //创建解冻实体类
        UnFreeze unFreeze = new UnFreeze();
        unFreeze.setUserId(userId.longValue());
        unFreeze.setReasonsForThawing(reasonsForThawing);
        unFreeze.setFrozenRemarks(frozenRemarks);
        //保存
        int result = unFreezeMapper.insert(unFreeze);
        //判断
        String message = "操作失败";
        if (result > 0) {
            //根据userId删除冻结表中数据
            UpdateWrapper wrapper = new UpdateWrapper();
            wrapper.eq("user_id", userId);
            freezeMapper.delete(wrapper);
            //查询得到当前用户对象
            UserInfo userInfo = userInfoApi.findById(userId.longValue());
            userInfo.setUserStatus("1");
            //修改userinfo中该用户的状态
            userInfoApi.update(userInfo);
            message = "操作成功";
            //解冻将删除redis中的冻结键
            String key = "Freeze_User_"+userId;
            //有此键才删除
            if(redisTemplate.hasKey(key)) {
                redisTemplate.delete(key);
            }
        }
        //封装操作结果并返回
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("message", message);
        return ResponseEntity.ok(messageMap);
    }
}
