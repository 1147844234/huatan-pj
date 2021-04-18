package com.tanhua.dubbo.api.mongo;

import com.tanhua.domain.vo.PageResult;

public interface FriendApi {
    /**
     * 保存好友关系到mongodb中的tanhua_users表中
     */
    void save(Long userId, Long friendId);

    /**
     * 分页查询联系人
     */
    PageResult findFriendByUserId(Long userId,Integer page, Integer pagesize);

    //fans-喜欢
    void delete(Long userId, Long likeUserId);
}
