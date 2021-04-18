package com.tanhua.dubbo.api.impl.mongo;

import com.tanhua.domain.mongo.FollowUser;
import com.tanhua.domain.mongo.Video;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.mongo.VideoApi;
import com.tanhua.dubbo.utils.IdService;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Service
public class VideoApiImpl implements VideoApi {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IdService idService;
    @Override
    public PageResult findByPage(Integer page, Integer pagesize,Long... userId) {
        Query query = new Query();
        if (userId != null && userId.length>0) {
            // 根据用户id，分页查询小视频
            query.addCriteria(Criteria.where("userId").is(userId[0]));
        }

        query.with(Sort.by(Sort.Order.desc("created")));
        query.limit(pagesize).skip((page-1)*pagesize);

        List<Video> videoList = mongoTemplate.find(query, Video.class);
        long count = mongoTemplate.count(query, Video.class);
        return new PageResult(page,pagesize, (int) count,videoList);
    }

    @Override
    public void save(Video video) {
        // 【记录自增长vid，推荐系统推荐计算使用】
        video.setVid(idService.getNextId("video"));
        mongoTemplate.insert(video);
    }

    @Override
    public void followUser(FollowUser followUser) {
        mongoTemplate.insert(followUser);
    }

    @Override
    public void unfollowUser(FollowUser followUser) {
        Query query = Query.query(
                Criteria.where("userId").is(followUser.getUserId())
                .and("followUserId").is(followUser.getFollowUserId())
        );
        mongoTemplate.remove(query,FollowUser.class);
    }

    @Override
    public Video findById(String videoId) {
        return mongoTemplate.findById(new ObjectId(videoId),Video.class);
    }

    @Override
    public List<Video> findByVids(List<Long> vidList) {
        Query query = Query.query(Criteria.where("vid").in(vidList));
        return mongoTemplate.find(query,Video.class);
    }
}
