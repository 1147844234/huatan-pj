package com.tanhua.dubbo.api.impl.mongo;
import org.bson.types.ObjectId;

import com.tanhua.domain.mongo.Cards;
import com.tanhua.domain.mongo.Friend;
import com.tanhua.domain.mongo.UserLike;
import com.tanhua.domain.mongo.Visitors;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.mongo.RecommendUserApi;
import com.tanhua.dubbo.api.mongo.UserLikeApi;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserLikeApiImpl implements UserLikeApi {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RecommendUserApi recommendUserApi;


    @Override
    public Long queryEachLoveCount(Long userId) {
        // 互相关注： db.tanhua_users.find({userId:1})
        Query query = Query.query(Criteria.where("userId").is(userId));
        return mongoTemplate.count(query, Friend.class);
    }

    @Override
    public Long queryLoveCount(Long userId) {
        // 喜欢： db.user_like.find({userId:1})
        Query query = Query.query(Criteria.where("userId").is(userId));
        return mongoTemplate.count(query, UserLike.class);
    }

    @Override
    public Long queryFanCount(Long userId) {
        // 粉丝：db.user_like.find({likeUserId:1})
        Query query = Query.query(Criteria.where("likeUserId").is(userId));
        return mongoTemplate.count(query, UserLike.class);
    }


    @Override
    public PageResult queryEachLoveList(Long userId, Integer page, Integer pagesize) {
        // 互相关注： db.tanhua_users.find({userId:1})
        Query query = Query.query(
                Criteria.where("userId").is(userId)
        );
        query.with(Sort.by(Sort.Order.desc("created")));
        query.limit(pagesize).skip((page-1)*pagesize);
        List<Friend> friendList = mongoTemplate.find(query, Friend.class);
        long count = mongoTemplate.count(query, Friend.class);
        // 通过集合中的map统计封装数据：List<Map<String,Object>> list;
        List<Map<String,Object>> list = new ArrayList<>();
        if (friendList != null && friendList.size()>0) {
            for (Friend friend : friendList) {
                // 构造map
                Map<String,Object> map = new HashMap<>();
                map.put("uid",friend.getFriendId());
                map.put("score", recommendUserApi.queryScore(userId,friend.getFriendId()));
                // 添加到集合
                list.add(map);
            }
        }
        return new PageResult(page,pagesize, (int) count,list);
    }

    @Override
    public PageResult queryUserLikeList(Long userId, Integer page, Integer pagesize) {
         // 喜欢： db.user_like.find({userId:1})
        Query query = Query.query(
                Criteria.where("userId").is(userId)
        );
        query.with(Sort.by(Sort.Order.desc("created")));
        query.limit(pagesize).skip((page-1)*pagesize);

        List<UserLike> userLikeList = mongoTemplate.find(query, UserLike.class);
        long count = mongoTemplate.count(query, UserLike.class);
        // 通过集合中的map统计封装数据
        List<Map<String,Object>> list = new ArrayList<>();
        if (userLikeList != null && userLikeList.size()>0) {
            for (UserLike userLike : userLikeList) {
                // 构造map
                Map<String,Object> map = new HashMap<>();
                map.put("uid",userLike.getLikeUserId());
                map.put("score", recommendUserApi.queryScore(userId,userLike.getLikeUserId()));
                // 添加到集合
                list.add(map);
            }
        }
        return new PageResult(page,pagesize, (int) count,list);
    }

    @Override
    public PageResult queryFansList(Long userId, Integer page, Integer pagesize) {
        // 粉丝：db.user_like.find({likeUserId:1})
        Query query = Query.query(
                Criteria.where("likeUserId").is(userId)
        );
        query.with(Sort.by(Sort.Order.desc("created")));
        query.limit(pagesize).skip((page-1)*pagesize);

        List<UserLike> userLikeList = mongoTemplate.find(query, UserLike.class);
        long count = mongoTemplate.count(query, UserLike.class);
        // 通过集合中的map统计封装数据
        List<Map<String,Object>> list = new ArrayList<>();
        if (userLikeList != null && userLikeList.size()>0) {
            for (UserLike userLike : userLikeList) {
                // 构造map
                Map<String,Object> map = new HashMap<>();
                map.put("uid",userLike.getUserId());
                map.put("score", recommendUserApi.queryScore(userId,userLike.getUserId()));
                // 添加到集合
                list.add(map);
            }
        }
        return new PageResult(page,pagesize, (int) count,list);
    }

    @Override
    public PageResult queryVisitorsList(Long userId, Integer page, Integer pagesize) {
        // 谁看过我：db.visitors.find({userId:1})
        Query query = Query.query(
                Criteria.where("userId").is(userId)
        );
        query.with(Sort.by(Sort.Order.desc("date")));
        query.limit(pagesize).skip((page-1)*pagesize);

        List<Visitors> visitorsList = mongoTemplate.find(query, Visitors.class);
        long count = mongoTemplate.count(query, Visitors.class);
        // 通过集合中的map统计封装数据
        List<Map<String,Object>> list = new ArrayList<>();
        if (visitorsList != null && visitorsList.size()>0) {
            for (Visitors visitors : visitorsList) {
                // 构造map
                Map<String,Object> map = new HashMap<>();
                map.put("uid",visitors.getVisitorUserId());
                map.put("score", recommendUserApi.queryScore(userId,visitors.getVisitorUserId()));
                // 添加到集合
                list.add(map);
            }
        }
        return new PageResult(page,pagesize, (int) count,list);
    }

    @Override
    public void delete(Long likeUserId,Long userId) {
        Query query = Query.query(
            Criteria.where("userId").is(likeUserId)
                .and("likeUserId").is(userId)
        );
        mongoTemplate.remove(query,UserLike.class);
    }

    @Override
    public void saveLove(long userId, Integer loveUserId) {
        //1.从当前用户的划卡列表中将这位喜欢的用户删除；cards_users_userId
        Query query = new Query(Criteria.where("userId").is(loveUserId));
        mongoTemplate.remove(query,"cards_users_"+userId);
        //2.将这个用户和本地用户一起加入到本地用户喜欢关系表中；user_like
        UserLike userLike = new UserLike();
        userLike.setId(new ObjectId());
        userLike.setUserId(userId);
        userLike.setLikeUserId(loveUserId.longValue());
        userLike.setCreated(System.currentTimeMillis());
        mongoTemplate.save(userLike);
    }

    @Override
    public boolean isFriend(long userId, long loveUserId) {
        boolean flag = false;
        Query query1 = new Query(Criteria.where("userId").is(userId).and("likeUserId").is(loveUserId));
        Query query2 = new Query(Criteria.where("userId").is(loveUserId).and("likeUserId").is(userId));
        UserLike userLike1 = mongoTemplate.findOne(query1, UserLike.class);
        UserLike userLike2 = mongoTemplate.findOne(query2, UserLike.class);
        if (userLike1!=null&&userLike2!=null){
            flag = true;
        }
        return flag;
    }

    //添加数据
    @Override
    public void save(Long userId, Long likeUserId) {
        UserLike userLike = new UserLike();
        userLike.setCreated(System.currentTimeMillis());
        userLike.setUserId(userId);
        userLike.setLikeUserId(likeUserId);
        mongoTemplate.save(userLike);
    }

}
