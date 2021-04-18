package com.tanhua.dubbo.api;

import com.tanhua.domain.vo.PageResult;

public interface BlackListApi {
    /**
     * 根据用户id分页查询黑名单列表
     */
    PageResult findBlackList(Integer page, Integer pagesize, Long userId);

    /**
     * 根据登陆用户id、黑名单用户id实现移除黑名单
     * @param userId 登陆用户id
     * @param blackUserId 要移除的黑名单的用户id
     */
    void deleteBlacklist(Long userId, String blackUserId);
}
