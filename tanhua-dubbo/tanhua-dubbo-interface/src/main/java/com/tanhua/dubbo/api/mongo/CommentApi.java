package com.tanhua.dubbo.api.mongo;

import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.vo.PageResult;

public interface CommentApi {
    /**
     * 保存点赞数据
     */
    long save(Comment comment);

    /**
     * 取消点赞，删除评论表数据
     */
    long delete(Comment comment);

    /**
     * 分页查询评论列表，查询条件：动态id、commentType
     */
    PageResult queryCommentsList(String movementId, Integer page, Integer pagesize);

    /**
     * 喜欢，评论，点赞列表
     */
    PageResult findCommentTypeList(Integer commentType, Integer page, Integer pagesize, Long userId);
}
