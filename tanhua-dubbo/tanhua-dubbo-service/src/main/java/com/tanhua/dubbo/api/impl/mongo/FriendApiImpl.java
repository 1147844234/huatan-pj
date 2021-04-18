package com.tanhua.dubbo.api.impl.mongo;
import com.tanhua.domain.vo.PageResult;
import org.bson.types.ObjectId;

import com.tanhua.domain.mongo.Friend;
import com.tanhua.dubbo.api.mongo.FriendApi;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Service
public class FriendApiImpl implements FriendApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Long userId, Long friendId) {
        // 构造条件
        Query query = Query.query(
                Criteria.where("userId").is(userId)
                .and("friendId").is(friendId)
        );
        // 保存之前，先查询
        if (!mongoTemplate.exists(query,Friend.class)) {
            // 创建好友对象
            Friend friend = new Friend();
            friend.setUserId(userId);
            friend.setFriendId(friendId);
            friend.setCreated(System.currentTimeMillis());
            // 保存好友关系
            mongoTemplate.save(friend);
        }
        query = Query.query(
                Criteria.where("userId").is(friendId)
                        .and("friendId").is(userId)
        );
        // 保存之前，先查询
        if (!mongoTemplate.exists(query,Friend.class)) {
            // 创建好友对象
            Friend friend = new Friend();
            friend.setUserId(friendId);
            friend.setFriendId(userId);
            friend.setCreated(System.currentTimeMillis());
            // 保存好友关系
            mongoTemplate.save(friend);
        }
    }

    @Override
    public PageResult findFriendByUserId(Long userId,Integer page, Integer pagesize) {
        // 构造查询条件
        Query query = Query.query(
                Criteria.where("userId").is(userId)
        );
        query.limit(pagesize).skip((page-1)*pagesize);
        List<Friend> friendList = mongoTemplate.find(query, Friend.class);
        long count = mongoTemplate.count(query, Friend.class);
        return new PageResult(page,pagesize,(int) count,friendList);
    }

    @Override
    public void delete(Long userId, Long likeUserId) {
        //删除到好友关系表
        Query query = Query.query(Criteria.where("userId").is(userId).and("friendId").is(likeUserId));
        mongoTemplate.remove(query,Friend.class);

        Query querys = Query.query(Criteria.where("userId").is(likeUserId).and("friendId").is(userId));
        mongoTemplate.remove(querys,Friend.class);
    }
}
