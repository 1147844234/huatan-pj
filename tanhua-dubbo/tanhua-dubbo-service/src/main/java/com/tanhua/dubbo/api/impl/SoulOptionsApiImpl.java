package com.tanhua.dubbo.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.tanhua.domain.db.SoulOptions;
import com.tanhua.dubbo.api.SoulOptionsApi;
import com.tanhua.dubbo.mapper.SoulOptionsMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service
public class SoulOptionsApiImpl implements SoulOptionsApi {

    @Autowired
    private SoulOptionsMapper soulOptionsMapper;

    @Override
    public List<SoulOptions> queryOptions(Long questionid) {
        //构造查询选项的条件
        QueryWrapper<SoulOptions> soulOptionsQueryWrapper = new QueryWrapper<>();
        soulOptionsQueryWrapper.eq("questionid", questionid);
        return soulOptionsMapper.selectList(soulOptionsQueryWrapper);
    }

    @Override
    public SoulOptions selectOne(Long questionid, String option) {
        QueryWrapper<SoulOptions> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("questionid", questionid);
        queryWrapper.eq("id", option);
        return soulOptionsMapper.selectOne(queryWrapper);
    }
}
