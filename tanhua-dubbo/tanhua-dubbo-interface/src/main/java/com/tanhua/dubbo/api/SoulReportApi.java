package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.domain.db.SoulReport;

import java.util.List;

public interface SoulReportApi {
    List<SoulReport> queryReportList(Long userId);

    SoulReport queryReport(Long userId, Long paperid);

    void updateReport(SoulReport soulReport, Long userId);

    void insert(SoulReport soulReport);

    SoulReport queryReportById(String id);


    List<SoulReport> querySimiliar(Long score, Long userid, Long paperid);
}
