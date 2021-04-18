package com.tanhua.dubbo.api.impl.mongo;

import com.tanhua.domain.mongo.Visitors;
import com.tanhua.dubbo.api.mongo.RecommendUserApi;
import com.tanhua.dubbo.api.mongo.VisitorsApi;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Service(timeout = 100000)
public class VisitorsApiImpl implements VisitorsApi {

    @Autowired
    private MongoTemplate mongoTemplate;
    // 注入服务对象（因为当前是在dubbo工程中注入当前工程中的对象，不需要远程调用，所以不用@Reference注解）
    @Autowired
    private RecommendUserApi recommendUserApi;

    @Override
    public List<Visitors> queryVisitorList(Long userId, int top) {
        // db.visitors.find({userId:1}).sort({date:-1}).limit(5)
        Query query = Query.query(
                Criteria.where("userId").is(userId)
        );
        query.with(Sort.by(Sort.Order.desc("date"))).limit(top);
        return getVisitorsList(query);
    }

    @Override
    public List<Visitors> queryVisitorList(Long userId, Long time) {
        //db.visitors.find({userId:1,date:{$gt:100}}).sort({date:-1})
        Query query = Query.query(
                Criteria.where("userId").is(userId)
                .and("date").gt(time)
        );
        query.with(Sort.by(Sort.Order.desc("date")));
        return getVisitorsList(query);
    }

    // 抽取的方法：设置缘分值
    private List<Visitors> getVisitorsList(Query query){
        List<Visitors> visitorsList = mongoTemplate.find(query, Visitors.class);
        // 查询缘分值，设置到集合中的每一个访客中
        if (visitorsList != null && visitorsList.size()>0) {
            for (Visitors visitors : visitorsList) {
                // 查询当前用户与访客用户的缘分值
                Long score = recommendUserApi.queryScore(visitors.getUserId(), visitors.getVisitorUserId());
                visitors.setScore(score.doubleValue());
            }
        }
        return visitorsList;
    }
}
