package com.tanhua.dubbo.api.impl.mongo;

import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.mongo.CommentApi;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

@Service(timeout = 100000)
public class CommentApiImpl implements CommentApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public long save(Comment comment) {
        //1. 需要往评论表quanzi_comment插入一条点赞记录
        mongoTemplate.insert(comment);

        //2. 修改动态表点赞/喜欢数量,修改条件：动态id
        Query query = new Query(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        //update.inc("likeCount",1);
        //update.inc("loveCount",1);
        update.inc(comment.getCol(), 1);
        mongoTemplate.updateFirst(query, update, Publish.class);

        //3. 查询点赞的总数,查询条件：commentType、publishId
        Query countQuery = new Query(
                Criteria.where("commentType").is(comment.getCommentType())
                        .and("publishId").is(comment.getPublishId())
        );
        long count = mongoTemplate.count(countQuery, Comment.class);
        return count;
    }

    @Override
    public long delete(Comment comment) {
        //1. 删除评论表数据
        Query query = new Query(
                Criteria.where("publishId").is(comment.getPublishId())
                        .and("commentType").is(comment.getCommentType())
                        .and("userId").is(comment.getUserId())
        );
        mongoTemplate.remove(query, Comment.class);

        //2. 修改动态表
        Query publishQuery = new Query(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        update.inc(comment.getCol(), -1);
        mongoTemplate.updateFirst(publishQuery, update, Publish.class);

        //3. 查询总数
        Query countQuery = new Query(
                Criteria.where("commentType").is(comment.getCommentType())
                        .and("publishId").is(comment.getPublishId())
        );
        long count = mongoTemplate.count(countQuery, Comment.class);
        return count;
    }

    @Override
    public PageResult queryCommentsList(String movementId, Integer page, Integer pagesize) {
        Query query = new Query(
                Criteria.where("publishId").is(new ObjectId(movementId))
                        .and("commentType").is(2)
        );
        query.with(Sort.by(Sort.Order.desc("created")));
        query.limit(pagesize).skip((page - 1) * pagesize);
        List<Comment> commentList = mongoTemplate.find(query, Comment.class);
        long count = mongoTemplate.count(query, Comment.class);
        return new PageResult(page, pagesize, (int) count, commentList);
    }

    /**
     * 喜欢，评论，点赞列表
     */
    @Override
    public PageResult findCommentTypeList(Integer commentType, Integer page, Integer pagesize, Long userId) {
        //通过commentType类型与publishUserId查到喜欢，评论，点赞登录用户的人
        Query query = Query.query(Criteria.where("commentType").is(commentType).and("publishUserId").is(userId));
        //分页
        query.limit(pagesize).skip((page - 1) * pagesize);
        List<Comment> commentList = mongoTemplate.find(query, Comment.class);
        //统计
        long count = mongoTemplate.count(query, Comment.class);
        //返回数据
        return new PageResult(page, pagesize, (int) count, commentList);
    }
}
