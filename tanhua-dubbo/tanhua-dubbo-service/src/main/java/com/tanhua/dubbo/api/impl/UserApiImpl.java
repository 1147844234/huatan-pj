package com.tanhua.dubbo.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.domain.db.User;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.mapper.UserMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

/**
 * 服务类：
 * 1、对外发布的dubbo服务类
 * 2、导入的包
 *    org.springframework.stereotype.Service  错误
 *    org.apache.dubbo.config.annotation.Service 正确
 *
 */
@Service
public class UserApiImpl implements UserApi {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Long save(User user) {
        userMapper.insert(user);
        // 保存后回自动把主键值设置到user对象中 （mp）
        return user.getId();
    }

    @Override
    public User findByMobile(String mobile) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile",mobile);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public void update(User user) {
        userMapper.updateById(user);
    }

    //修改手机号
    @Override
    public void updatePhone(User user) {
        userMapper.updateById(user);
    }
}
