package com.tanhua.dubbo.api.impl.mongo;
import com.tanhua.domain.mongo.UserLike;
import org.bson.types.ObjectId;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.domain.db.User;
import com.tanhua.domain.mongo.Cards;
import com.tanhua.dubbo.api.mongo.CardsApi;
import com.tanhua.dubbo.mapper.UserMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Service(timeout = 1000000)
public class CardsApiImpl implements CardsApi {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserMapper userMapper;

    @Override
    public void creatCardsList(Long userId) {
        //查找所有的用户，返回所有id作为集合，遍历集合，如果和userid一致，直接跳过，每一个id生成一个cards表，
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(userQueryWrapper);
        if (userList != null) {
            for (User user : userList) {
                Long userId1 = user.getId();
                if (userId1 != null&&userId1!=userId) {
                    Cards cards = new Cards();
                    cards.setUserId(userId1);
                    cards.setCreated(System.currentTimeMillis());
                    mongoTemplate.insert(cards,"cards_users_"+userId);
                }
            }
        }
    }

    @Override
    public List<Cards> findAllUserId(Long userId,Integer page,Integer pagesize) {
        Query query1 = new Query(Criteria.where("userId").is(userId));
        List<UserLike> userLikeList = mongoTemplate.find(query1, UserLike.class);


        //1.查询该用户的mongo"cards_users_"+userId表，如果查不到就新建一次；
        Query query = new Query();
        query.limit(pagesize).skip((page-1)*pagesize);
        List<Cards> cardsList = null;
               cardsList = mongoTemplate.find(query,Cards.class, "cards_users_" + userId);
        if (cardsList==null||cardsList.size()==0){
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            List<User> userList = userMapper.selectList(userQueryWrapper);

            if (userList != null) {
                OUT:
                for (User user : userList) {
                    Long userId1 = user.getId();
                    if (userLikeList!=null&&userLikeList.size()>0){
                        for (UserLike userLike : userLikeList) {
                            if (userLike.getLikeUserId()==userId1){continue OUT;}
                        }
                    }
                    if (userId1 != null&&userId1!=userId) {
                        Cards cards = new Cards();
                        cards.setUserId(userId1);
                        cards.setCreated(System.currentTimeMillis());
                        mongoTemplate.insert(cards,"cards_users_"+userId);
                    }
                }
            }
            cardsList = mongoTemplate.find(query, Cards.class, "cards_users_" + userId);
        }
        return cardsList;
    }

    @Override
    public List<Cards> findAllUserId(Long userId) {
        //1.查询该用户的mongo"cards_users_"+userId表，如果查不到就新建一次；
        Query query = new Query();
        List<Cards> cardsList = mongoTemplate.find(query,Cards.class, "cards_users_" + userId);
        if (cardsList==null||cardsList.size()==0){
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            List<User> userList = userMapper.selectList(userQueryWrapper);
            if (userList != null) {
                for (User user : userList) {
                    Long userId1 = user.getId();
                    if (userId1 != null&&userId1!=userId) {
                        Cards cards = new Cards();
                        cards.setUserId(userId1);
                        cards.setCreated(System.currentTimeMillis());
                        mongoTemplate.insert(cards,"cards_users_"+userId);
                    }
                }
            }
            cardsList = mongoTemplate.find(query, Cards.class, "cards_users_" + userId);
        }
        return cardsList;
    }

    @Override
    public void saveCards(Long userId, Long otherUserId) {
        //1.从mongo里面查询cards，没查到就插入
        Query query = new Query(Criteria.where("userId").is(userId));
        List<Cards> cardsList = mongoTemplate.find(query, Cards.class, "cards_users_" + otherUserId);
        if (cardsList==null||cardsList.size()==0){
        Cards cards = new Cards();
        cards.setId(new ObjectId());
        cards.setUserId(userId);
        cards.setCreated(System.currentTimeMillis());
        mongoTemplate.save(cards, "cards_users_" + otherUserId);
        }}
}
