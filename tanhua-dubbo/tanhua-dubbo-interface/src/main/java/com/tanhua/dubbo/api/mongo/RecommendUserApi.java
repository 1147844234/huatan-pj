package com.tanhua.dubbo.api.mongo;

import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.PageResult;

public interface RecommendUserApi {
    /**
     * 根据登陆用户id，查询今日佳人
     */
    RecommendUser queryWithMaxScore(Long userId);

    /**
     * 根据登陆用户id，分页查询推荐用户
     */
    PageResult queryRecommendation(Integer page, Integer pagesize, Long userId);

    /**
     * 查询登陆用户id与推荐用户id之间的缘分值
     */
    long queryScore(Long userId, Long recommendUserId);
}
