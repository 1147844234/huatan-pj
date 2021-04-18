package com.tanhua.dubbo.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.BlackList;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.BlackListApi;
import com.tanhua.dubbo.mapper.BlackListMapper;
import com.tanhua.dubbo.mapper.UserInfoMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service(timeout = 100000)
public class BlackListApiImpl implements BlackListApi {

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private BlackListMapper blackListMapper;

    @Override
    public PageResult findBlackList(Integer page, Integer pagesize, Long userId) {
        // 封装分页参数
        Page<UserInfo> iPage = new Page<>(page,pagesize,userId);
        // 分页查询
        IPage<UserInfo> resultPage = userInfoMapper.findBlackList(iPage,userId);
        // 返回
        return new PageResult(page,pagesize, (int) resultPage.getTotal(),resultPage.getRecords());
    }

    @Override
    public void deleteBlacklist(Long userId, String blackUserId) {
        QueryWrapper<BlackList> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userId).eq("black_user_id",blackUserId);
        blackListMapper.delete(wrapper);
    }
}
