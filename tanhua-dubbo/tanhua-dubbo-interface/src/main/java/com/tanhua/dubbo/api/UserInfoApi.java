package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.domain.db.UserInfo;

import java.util.List;

public interface UserInfoApi {
    void save(UserInfo userInfo);

    void update(UserInfo userInfo);

    UserInfo findById(Long id);

    IPage<UserInfo> findByPage(Integer page, Integer pagesize);

    void updateUserStatus(UserInfo userInfo);

    List<UserInfo> selectList(List<Object> userIds);
}
