package com.tanhua.server.service;

import com.tanhua.domain.db.Question;
import com.tanhua.domain.db.Settings;
import com.tanhua.domain.db.User;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.SettingsVo;
import com.tanhua.dubbo.api.BlackListApi;
import com.tanhua.dubbo.api.QuestionApi;
import com.tanhua.dubbo.api.SettingsApi;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SettingsService {

    @Reference(retries = 2)
    private SettingsApi settingsApi;
    @Reference
    private QuestionApi questionApi;
    @Reference
    private UserApi userApi;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    /**
     * 需求分析：在通用设置中，查询通知设置
     * @return
     */
    public ResponseEntity<Object> querySettings() {
        //1. 获取当前用户
        User user = UserHolder.get();

        //2. 创建并封装返回的vo对象
        SettingsVo vo = new SettingsVo();

        //2.1 根据用户查询用户的通知设置表 tb_settings, 并封装到vo中
        Settings settings = settingsApi.findByUserId(user.getId());
        if (settings != null) {
            BeanUtils.copyProperties(settings,vo);
        }
        //2.2 设置手机号码
        vo.setPhone(user.getMobile());

        //2.3 根据用户id查询陌生人问题，并封装vo
        Question question = questionApi.findByUserId(user.getId());
        if (question != null) {
            vo.setStrangerQuestion(question.getTxt());
        }

        return ResponseEntity.ok(vo);
    }

    /**
     * 需求分析：保存或者修改通知设置
     */
    public ResponseEntity<Object> saveNotification(Settings param) {
        //1. 获取用户id
        Long userId = UserHolder.getUserId();
        //2. 先根据用户id，查询通知设置表
        Settings settings = settingsApi.findByUserId(userId);
        //3. 判断
        if (settings == null) {
            //3.1 添加
            settings = new Settings();
            // 对象拷贝
            BeanUtils.copyProperties(param,settings);
            // 设置用户id
            settings.setUserId(userId);
            // 保存
            settingsApi.save(settings);
        } else {
            //3.2 修改
            //BeanUtils.copyProperties(param,settings);
            settings.setGonggaoNotification(param.getGonggaoNotification());
            settings.setPinglunNotification(param.getPinglunNotification());
            settings.setLikeNotification(param.getLikeNotification());
            settingsApi.update(settings);
        }
        return ResponseEntity.ok(null);
    }

    /**
     * 需求分析：添加或修改陌生人问题 tb_question
     */
    public ResponseEntity<Object> saveQuestion(String content) {
        //1. 获取用户id
        Long userId = UserHolder.getUserId();
        //2. 先根据用户id，查询通知设置表
        Question question = questionApi.findByUserId(userId);
        //3. 判断
        if (question == null) {
            //3.1 添加
            question = new Question();
            question.setUserId(userId);
            question.setTxt(content);
            // 保存
            questionApi.save(question);
        } else {
            //3.2 修改
            question.setTxt(content);
            questionApi.update(question);
        }
        return ResponseEntity.ok(null);
    }

    @Reference
    private BlackListApi blackListApi;
    /**
     * 需求分析：分页查询黑名单用户信息
     */
    public ResponseEntity<Object> blacklist(Integer page, Integer pagesize) {
        //1. 获取用户
        Long userId = UserHolder.getUserId();
        //2. 根据用户id分页查询黑名单列表
        PageResult pageResult = blackListApi.findBlackList(page,pagesize,userId);
        //3. 返回
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 需求描述：根据登陆用户id、黑名单用户id实现移除黑名单
     */
    public ResponseEntity<Object> deleteBlacklist(String blackUserId) {
        // 获取登陆用户id
        Long userId = UserHolder.getUserId();
        // 远程调用api，实现移除黑名单
        blackListApi.deleteBlacklist(userId,blackUserId);
        return ResponseEntity.ok(null);
    }

    //校验验证码
    public ResponseEntity<Object> checkVerificationCode(String code) {
        //1. 获取用户信息
        User user = UserHolder.get();

        //2. 从redis中获取验证码
        String key = "SMS_KEY_" + user.getMobile();
        String redisCode = redisTemplate.opsForValue().get(key);

        //3. 判断
        Boolean verification = true;
        if (code == null || redisCode == null || !redisCode.equals(code)) {
            //3.1 校验失败
            verification = false;
        } else {
            //3.2 校验成功，从redis中删除验证码
            redisTemplate.delete(key);
        }

        //4. 构造返回结果:{"verification":false/true}
        Map<String,Boolean> resultMap = new HashMap<>();
        resultMap.put("verification",verification);
        return ResponseEntity.ok(resultMap);
    }


    //修改手机号
    public ResponseEntity<Object> updatePhone(String phone) {
        //1. 根据修改后的手机号码查询，如果手机号码已经存在返回错误信息
        User mobile = userApi.findByMobile(phone);
        if (mobile != null) {
            return ResponseEntity.status(500).body(ErrorResult.mobileError());
        }
        //得到当前登录用户
        User user = UserHolder.get();
        user.setMobile(phone);
        //调用api层根据id修改对象
        userApi.updatePhone(user);
        return ResponseEntity.ok(null);

    }
}
