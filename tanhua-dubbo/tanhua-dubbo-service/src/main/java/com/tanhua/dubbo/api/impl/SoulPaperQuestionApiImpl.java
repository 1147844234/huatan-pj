package com.tanhua.dubbo.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.domain.db.SoulPaperQuestion;
import com.tanhua.dubbo.api.SoulPaperApi;
import com.tanhua.dubbo.api.SoulPaperQuestionApi;
import com.tanhua.dubbo.mapper.SoulPaperQuestionMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service
public class SoulPaperQuestionApiImpl implements SoulPaperQuestionApi {

    @Autowired
    private SoulPaperQuestionMapper soulPaperQuestionMapper;

    @Override
    public List<SoulPaperQuestion> queryPaperQuestionList(int paperid) {
        QueryWrapper<SoulPaperQuestion> soulPaperQuestionQueryWrapper = new QueryWrapper<>();
        soulPaperQuestionQueryWrapper.eq("paperid", paperid);
        return soulPaperQuestionMapper.selectList(soulPaperQuestionQueryWrapper);
    }

    @Override
    public SoulPaperQuestion selectOne(Long questionid) {
        QueryWrapper queryWrapper = new QueryWrapper();

        queryWrapper.eq("questionid", questionid);
        return soulPaperQuestionMapper.selectOne(queryWrapper);
    }
}
