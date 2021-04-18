package com.tanhua.dubbo.api.impl.mongo;

import com.tanhua.domain.mongo.PeachBlossom;
import com.tanhua.domain.mongo.RemainingTimes;
import com.tanhua.dubbo.api.mongo.PeachBlossomApi;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

@Service
public class PeachBlossomApiImpl implements PeachBlossomApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 桃花传音
     */
    @Override
    public void save(PeachBlossom peachBlossom) {
        mongoTemplate.save(peachBlossom);
    }


    /**
     * 查询未读所有语音
     */
    @Override
    public List<PeachBlossom> findAll(Long userId) {
        //ne:不等与
        Query query = Query.query(Criteria.where("state").is(0).and("userId").ne(userId));
        List<PeachBlossom> blossomList = mongoTemplate.find(query, PeachBlossom.class);
        return blossomList;
    }

    /**
     * 修改状态
     */
    @Override
    public void update(ObjectId id, Integer state) {
        Query query = Query.query(Criteria.where("id").is(id));
        Update up = new Update();
        up.set("state", state);
        mongoTemplate.updateFirst(query, up, PeachBlossom.class);
    }

    /**
     * 接收语音次数减1
     */
    @Override
    public void timeMinusOne(Long userId) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        Update up = new Update();
        up.inc("rimes", -1);
        mongoTemplate.updateFirst(query, up, RemainingTimes.class);
    }

    /**
     * 查询当前登录用户剩余次数
     */
    @Override
    public RemainingTimes findRemainingTimes(Long userId) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        return mongoTemplate.findOne(query, RemainingTimes.class);
    }


    //--------------------------拓展------------------------------

    /**
     * 如果当前登录用户发布3条语音会获得次数+1
     */
    @Override
    public void rimesPlusOne(Long userId, int i) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        Update update = new Update();
        update.inc("rimes", +1);
        mongoTemplate.updateFirst(query, update, RemainingTimes.class);
    }

    /**
     * 刷新接收次数
     */
    @Override
    public void RefreshRimes() {
        Query query = new Query();
        Update update = new Update();
        update.set("rimes", 10);
        mongoTemplate.updateFirst(query, update, RemainingTimes.class);
    }

    /**
     * 发送次数加1
     */
    @Override
    public void sendPlusOne(Long userId) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        Update update = new Update();
        update.inc("send", +1);
        mongoTemplate.updateFirst(query, update, RemainingTimes.class);
    }

    /**
     * 查询当前用户语音发送次数
     */
    @Override
    public Integer findBySend(Long userId) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        RemainingTimes one = mongoTemplate.findOne(query, RemainingTimes.class);
        return one.getSend();
    }
}
