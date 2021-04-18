package com.tanhua.dubbo.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.domain.db.SoulReport;
import com.tanhua.dubbo.api.SoulPaperApi;
import com.tanhua.dubbo.api.SoulReportApi;
import com.tanhua.dubbo.mapper.SoulReportMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service(timeout = 300000)
public class SoulReportApiImpl implements SoulReportApi {

    @Autowired
    private SoulReportMapper soulReportMapper;

    @Override
    public List<SoulReport> queryReportList(Long userId) {
        QueryWrapper<SoulReport> soulReportQueryWrapper = new QueryWrapper<>();
        soulReportQueryWrapper.eq("userid", userId);

        return soulReportMapper.selectList(soulReportQueryWrapper);
    }

    @Override
    public SoulReport queryReport(Long userId, Long paperid) {
        QueryWrapper<SoulReport> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("userid", userId).eq("paperid", paperid);
        return soulReportMapper.selectOne(queryWrapper2);
    }

    @Override
    public void updateReport(SoulReport soulReport, Long userId) {
        QueryWrapper<SoulReport> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("userid",userId).eq("paperid",soulReport.getPaperid());
        soulReportMapper.update(soulReport,queryWrapper1);
    }

    @Override
    public void insert(SoulReport soulReport) {
        soulReportMapper.insert(soulReport);
    }

    @Override
    public SoulReport queryReportById(String id) {
        QueryWrapper<SoulReport> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        return soulReportMapper.selectOne(queryWrapper);
    }

    @Override
    public List<SoulReport> querySimiliar(Long score, Long userid, Long paperid) {
        QueryWrapper<SoulReport> queryListWrapper = new QueryWrapper<>();
        queryListWrapper.between("score", score - 5, score + 5).ne("userid",userid).eq("paperid",paperid);
        return soulReportMapper.selectList(queryListWrapper);
    }


}
