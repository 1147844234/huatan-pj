package com.tanhua.dubbo.api.impl.mongo;

import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.mongo.RecommendUserApi;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * 推荐用户模块，dubbo服务
 * timeout
 *    设置超时时间为1000秒
 */
@Service(timeout = 1000000)
public class RecommendUserApiImpl implements RecommendUserApi {

    // 注入操作mongo数据库的工具类对象
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public RecommendUser queryWithMaxScore(Long userId) {
        // db.recommend_user.find({userId:1}).sort({score:-1}).limit(1)
        // 构造查询条件对象
        Query query = new Query(Criteria.where("userId").is(userId));
        query.with(Sort.by(Sort.Order.desc("score")));
        query.limit(1);
        return mongoTemplate.findOne(query,RecommendUser.class);
    }

    @Override
    public PageResult queryRecommendation(Integer page, Integer pagesize, Long userId) {
        // 构造查询条件
        Query query = new Query(Criteria.where("userId").is(userId));
        // 排序
        query.with(Sort.by(Sort.Order.desc("score")));
        // 分页
        query.limit(pagesize).skip((page-1)*pagesize);
        // 查询当前页数据
        List<RecommendUser> recommendUserList =
                mongoTemplate.find(query, RecommendUser.class);
        // 统计总记录数
        long count = mongoTemplate.count(query, RecommendUser.class);
        return new PageResult(page,pagesize, (int) count,recommendUserList);
    }

    @Override
    public long queryScore(Long userId, Long recommendUserId) {
        // 查询条件
        Query query = Query.query(
            Criteria.where("userId").is(userId)
                .and("recommendUserId").is(recommendUserId)
        );
        // 查询推荐用户
        RecommendUser recommendUser = mongoTemplate.findOne(query, RecommendUser.class);
        // 判断：如果没有查询到推荐用户，返回一个默认缘分值
        if (recommendUser == null) {
            return 70L;
        }
        return recommendUser.getScore().longValue();
    }
}
