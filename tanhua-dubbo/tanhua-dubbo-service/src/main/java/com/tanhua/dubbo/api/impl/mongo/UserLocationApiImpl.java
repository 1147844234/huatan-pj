package com.tanhua.dubbo.api.impl.mongo;
import com.tanhua.domain.vo.UserLocationVo;
import org.bson.types.ObjectId;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import com.tanhua.domain.mongo.UserLocation;
import com.tanhua.dubbo.api.mongo.UserLocationApi;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

@Service
public class UserLocationApiImpl implements UserLocationApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void saveLocation(Long userId, Double latitude, Double longitude, String addrStr) {
        Query query = Query.query(
                Criteria.where("userId").is(userId)
        );
        // 如果当前用户没有录入地理位置，执行插入
        if (!mongoTemplate.exists(query, UserLocation.class)) {
            UserLocation userLocation = new UserLocation();
            userLocation.setUserId(userId);
            userLocation.setLocation(new GeoJsonPoint(longitude,latitude));
            userLocation.setAddress(addrStr);
            userLocation.setCreated(System.currentTimeMillis());
            userLocation.setUpdated(System.currentTimeMillis());
            userLocation.setLastUpdated(System.currentTimeMillis());
            mongoTemplate.save(userLocation);
        } else {
            // 创建修改条件对象
            Update update = new Update();
            update.set("location",new GeoJsonPoint(longitude,latitude));
            update.set("address",addrStr);
            update.set("updated",System.currentTimeMillis());
            update.set("lastUpdated",System.currentTimeMillis());
            mongoTemplate.updateFirst(query,update,UserLocation.class);
        }
    }

    @Override
    public List<UserLocationVo> searNear(Long userId, Long distance) {
        // 根据用户id查询用户的坐标
        UserLocation userLocation = mongoTemplate.findOne(
                Query.query(Criteria.where("userId").is(userId)), UserLocation.class);
        // 获取用户坐标
        GeoJsonPoint location = userLocation.getLocation();

        // 创建半径举例对象
        Distance distanceObject = new Distance(distance/1000, Metrics.KILOMETERS);

        // 以用户坐标为圆点，按照指定的半径画圆
        Circle circle = new Circle(location,distanceObject);

        // 搜附近
        Query query = new Query(
                Criteria.where("location").withinSphere(circle)
        );
        List<UserLocation> userLocationList =
                mongoTemplate.find(query, UserLocation.class);
        return UserLocationVo.formatToList(userLocationList);
    }
}
