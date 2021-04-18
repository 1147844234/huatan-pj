package com.tanhua.dubbo.api.impl.mongo;

import com.tanhua.domain.mongo.*;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.mongo.PublishApi;
import com.tanhua.dubbo.utils.IdService;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;

@Service(timeout = 1000000)
public class PublishApiImpl implements PublishApi {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IdService idService;

    @Override
    public void save(Publish publish) {
        //1. 设置主键id、发布时间
        //publish.setId(ObjectId.get());
        publish.setCreated(System.currentTimeMillis());
        // 【设置自增长pid，推荐系统推荐计算使用】
        publish.setPid(idService.getNextId("quanzi_publish"));

        // 举例：登陆用户id是1，有2个好友id分别是2、3
        //2. 发布动态（1）往动态表中添加数据 quanzi_publish
        mongoTemplate.insert(publish);

        //3. 发布动态（2）往相册表中添加数据 quanzi_album_登陆用户id (存储自己的动态) quanzi_album_1
        Album album = new Album();
        album.setPublishId(publish.getId());
        album.setCreated(publish.getCreated());

        mongoTemplate.insert(album,"quanzi_album_"+publish.getUserId());

        //4. 发布动态（3）往好友的时间线表添加数据 quanzi_time_line_好友id
        //4.1 查询好友: db.tanhua_users.find({userId:1})
        Query query = new Query(Criteria.where("userId").is(publish.getUserId()));
        List<Friend> friendList = mongoTemplate.find(query, Friend.class);
        if (friendList != null && friendList.size()>0) {
            for (Friend friend : friendList) {
                // 4.2 获取好友id
                Long friendId = friend.getFriendId();
                // 4.3 往好友的时间线表插入数据：quanzi_time_line_2、quanzi_time_line_3
                TimeLine timeLine = new TimeLine();
                timeLine.setUserId(publish.getUserId());
                timeLine.setPublishId(publish.getId());
                timeLine.setCreated(publish.getCreated());

                mongoTemplate.insert(timeLine,"quanzi_time_line_"+friendId);
            }
        }
    }

    @Override
    public PageResult queryPublishList(Integer page, Integer pagesize, Long userId) {
        // 查询好友动态：查询时间线表
        Query query = new Query();
        query.with(Sort.by(Sort.Order.desc("created")));
        query.limit(pagesize).skip((page-1)*pagesize);
        List<TimeLine> timeLineList =
                mongoTemplate.find(query, TimeLine.class,"quanzi_time_line_"+userId);
        long count = mongoTemplate.count(query, "quanzi_time_line_" + userId);

        // 返回的数据：List<Publish>
        List<Publish> publishList = new ArrayList<>();
        // 遍历好友动态
        if (timeLineList != null && timeLineList.size()>0) {
            for (TimeLine timeLine : timeLineList) {
                if (timeLine.getPublishId() != null) {
                    // 根据动态id查询
                    Publish publish =
                            mongoTemplate.findById(timeLine.getPublishId(), Publish.class);
                    // 添加到集合
                    if (publish != null) {
                        publishList.add(publish);
                    }
                }
            }
        }
        return new PageResult(page,pagesize, (int) count,publishList);
    }

    @Override
    public PageResult queryRecommendList(Integer page, Integer pagesize, Long userId) {
        //1. 分页查询推荐动态:recommend_quanzi
        Query query = new Query(Criteria.where("userId").is(userId));
        query.with(Sort.by(Sort.Order.desc("created")));
        query.limit(pagesize).skip((page-1)*pagesize);
        List<RecommendQuanzi> recommendQuanziList =
                mongoTemplate.find(query, RecommendQuanzi.class);
        long count = mongoTemplate.count(query,RecommendQuanzi.class);
        //2. 返回的数据：List<Publish>
        List<Publish> publishList = new ArrayList<>();
        // 遍历好友动态
        if (recommendQuanziList != null && recommendQuanziList.size()>0) {
            for (RecommendQuanzi recommendQuanzi : recommendQuanziList) {
                if (recommendQuanzi.getPublishId() != null) {
                    // 根据动态id查询
                    Publish publish =
                            mongoTemplate.findById(recommendQuanzi.getPublishId(), Publish.class);
                    // 添加到集合
                    if (publish != null) {
                        // 【动态审核：显示state=1审核通过的动态】
                        if (publish.getState() != null && publish.getState() == 1) {
                            publishList.add(publish);
                        }
                    }
                }
            }
        }
        return new PageResult(page,pagesize, (int) count,publishList);
    }

    @Override
    public Publish findById(String publishId) {
        return mongoTemplate.findById(new ObjectId(publishId),Publish.class);
    }

    /**
     * 分页查询用户的动态
     *
     * @param page
     * @param pagesize
     * @param userId
     * @param state
     */


    @Override
    public PageResult findByPage(Integer page, Integer pagesize, Long userId, String state) {
        // 查询条件：userId
        Query query = new Query(
                Criteria.where("userId").is(userId)
        );
        query.with(Sort.by(Sort.Order.desc("created")));
        query.limit(pagesize).skip((page-1)*pagesize);
        // 查询用户的动态
        List<Publish> publishList = mongoTemplate.find(query, Publish.class);
        long count = mongoTemplate.count(query, Publish.class);
        return new PageResult(page,pagesize, (int) count,publishList);
    }

    @Override
    public void updateState(String publishId, Integer state) {
        Query query = Query.query(Criteria.where("id").is(new ObjectId(publishId)));
        Update update = new Update();
        update.set("state",state);
        mongoTemplate.updateFirst(query,update,Publish.class);
    }

    @Override
    public List<Publish> findByPids(List<Long> pidList) {
        Query query = Query.query(Criteria.where("pid").in(pidList));
        return mongoTemplate.find(query,Publish.class);
    }

    /**
     * 分页查询所有用户动态
     */
    @Override
    public PageResult findAll(Integer page, Integer pagesize) {
        Query query = new Query();
        query.limit(pagesize).skip((page - 1) * pagesize);
        List<Publish> publishList = mongoTemplate.find(query, Publish.class);
        long count = mongoTemplate.count(query, Publish.class);
        return new PageResult(page, pagesize, (int) count, publishList);
    }

    /**
     * 查询
     * 已审核 = 1
     * 待审核 = 0
     * 已驳回 = 2
     */
    @Override
    public PageResult findAuditing(Integer page, Integer pagesize, String state) {
        Query query = Query.query(Criteria.where("state").is(Integer.valueOf(state)));
        query.with(Sort.by(Sort.Order.desc("created")));
        query.limit(pagesize).skip((page - 1) * pagesize);
        List<Publish> publishList = mongoTemplate.find(query, Publish.class);
        long count = mongoTemplate.count(query, Publish.class);
        return new PageResult(page, pagesize, (int) count, publishList);
    }

    @Override
    public PageResult queryUserPublishList(Long id, Integer page, Integer pageSize) {
        Query query = new Query(Criteria.where("userId").is(id).and("state").is(1));
        query.with(Sort.by(Sort.Order.desc("created")));
        //db.quanzi_publish.find({userId:1}).sort({created:-1})
        List<Publish> publishes = mongoTemplate.find(query, Publish.class);
        long count = mongoTemplate.count(query, Publish.class);
        return new PageResult(page,pageSize, (int) count,publishes);
    }


    /**
     * 查询个人动态
     */
    @Override
    public PageResult findByAlbum(Long userId, int page, int pagesize) {
        Query query = new Query()
                .limit(pagesize).skip((page - 1) * pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        List<Album> albumList = mongoTemplate.find(query, Album.class, "quanzi_album_" + userId);

       // long count = mongoTemplate.count(query, Album.class, "quanzi_album_" + userId);

        List<Publish> result = new ArrayList<>();
        if (albumList != null && albumList.size() > 0) {
            albumList.forEach(album -> {
                Publish publish = mongoTemplate.findById(album.getPublishId(), Publish.class);
                if (publish != null) {
                    result.add(publish);
                }
            });
        }
        return new PageResult(page, pagesize, result.size(), result);
    }


}














