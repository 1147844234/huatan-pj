package com.tanhua.dubbo.api.mongo;

import com.tanhua.domain.vo.PageResult;

public interface UserLikeApi {
    /**
     * 我的喜欢：查询统计数据（互相喜欢、喜欢、粉丝）
     */
    Long queryEachLoveCount(Long userId);
    Long queryLoveCount(Long userId);
    Long queryFanCount(Long userId);

    /**
     * 互相喜欢、喜欢、粉丝、谁看过我
     */
    PageResult queryEachLoveList(Long userId, Integer page, Integer pagesize);
    PageResult queryUserLikeList(Long userId, Integer page, Integer pagesize);
    PageResult queryFansList(Long userId, Integer page, Integer pagesize);
    PageResult queryVisitorsList(Long userId, Integer page, Integer pagesize);

    /**
     * 删除粉丝中的喜欢数据
     */
    void delete(Long likeUserId,Long userId);

    void saveLove(long userId, Integer loveUserId);

    boolean isFriend(long userId, long loveUserId);

    void save(Long userId, Long likeUserId);
}
