package com.tanhua.dubbo.api.impl.mongo;
import org.bson.types.ObjectId;
import com.tanhua.domain.mongo.CardsUnlove;
import com.tanhua.dubbo.api.mongo.CardsUnloveApi;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Service(timeout = 1000000)
public class CardsUnloveApiImpl implements CardsUnloveApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void cardsUnlove(Long userId, Long unloveUserId) {
        //1.从当前用户的划卡列表中将这位不喜欢的用户删除；cards_users_userId
        System.out.println("进入了cardsUnlove方法");
        Query query = new Query(Criteria.where("userId").is(unloveUserId));
        mongoTemplate.remove(query,"cards_users_"+userId);

        //2.将这个用户加入到本地用户unlove表中；cards_unlove_users_userId
        CardsUnlove cardsUnlove = new CardsUnlove();
        cardsUnlove.setId(new ObjectId());
        cardsUnlove.setUserId(unloveUserId);
        cardsUnlove.setCreated(System.currentTimeMillis());

        CardsUnlove unlove = mongoTemplate.insert(cardsUnlove, "cards_unlove_users_" + userId);
        System.out.println("保存了cards_unlove_users_"+userId+"表格");
    }
}
