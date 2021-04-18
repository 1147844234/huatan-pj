package com.tanhua.dubbo.api.impl;

import com.tanhua.domain.db.SoulQuestion;
import com.tanhua.dubbo.api.SoulPaperApi;
import com.tanhua.dubbo.api.SoulQuestionApi;
import com.tanhua.dubbo.mapper.SoulQuestionMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
@Service
public class SoulQuestionApiImpl implements SoulQuestionApi {

    @Autowired
    private SoulQuestionMapper soulQuestionMapper;

    @Override
    public SoulQuestion selectById(Long questionid) {
        return soulQuestionMapper.selectById(questionid);
    }
}
