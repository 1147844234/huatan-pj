package com.tanhua.dubbo.api.impl;

import com.tanhua.domain.db.SoulPaper;
import com.tanhua.dubbo.api.SoulOptionsApi;
import com.tanhua.dubbo.api.SoulPaperApi;
import com.tanhua.dubbo.mapper.SoulPaperMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
@Service
public class SoulPaperApiImpl implements SoulPaperApi {

    @Autowired
    private SoulPaperMapper soulPaperMapper;

    @Override
    public SoulPaper selectById(int id) {
        return soulPaperMapper.selectById(id);
    }
}
