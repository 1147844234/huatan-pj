package com.tanhua.dubbo.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.mapper.UserInfoMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Service
public class UserInfoApiImpl implements UserInfoApi {
    @Autowired
    private UserInfoMapper userInfoMapper;


    @Override
    public void save(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }

    @Override
    public void update(UserInfo userInfo) {
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public UserInfo  findById(Long id) {
        return userInfoMapper.selectById(id);
    }

    @Override
    public IPage<UserInfo> findByPage(Integer page, Integer pagesize) {
        return userInfoMapper.selectPage(new Page<>(page,pagesize),null);
    }

    /**
     * 修改用户状态
     * @param userInfo
     */
    @Override
    public void updateUserStatus(UserInfo userInfo) {
        update(userInfo);
    }

    @Override
    public List<UserInfo> selectList(List<Object> userIds) {
        QueryWrapper<UserInfo> queryWrapper=new QueryWrapper<>();
        queryWrapper.in("id",userIds);
        return userInfoMapper.selectList(queryWrapper);
    }


}
