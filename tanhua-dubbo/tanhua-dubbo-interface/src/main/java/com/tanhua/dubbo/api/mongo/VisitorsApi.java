package com.tanhua.dubbo.api.mongo;

import com.tanhua.domain.mongo.Visitors;

import java.util.List;

public interface VisitorsApi {
    /**
     * 查询最近几位访客
     */
    List<Visitors> queryVisitorList(Long userId, int top);

    /**
     * 显示最近的访客
     */
    List<Visitors> queryVisitorList(Long userId, Long time);
}
