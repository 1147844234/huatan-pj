package com.tanhua.dubbo.api;

import com.tanhua.domain.db.SoulOptions;

import java.util.List;

public interface SoulOptionsApi {
    List<SoulOptions> queryOptions(Long questionid);

    SoulOptions selectOne(Long questionid, String option);
}
