package com.tanhua.dubbo.api;

import com.tanhua.domain.db.User;

public interface UserApi {
    /**
     * 保存
     * @param user 封装保存的用户信息
     * @return 返回新增的数据主键值
     */
    Long save(User user);

    /**
     * 根据手机号码查询
     */
    User findByMobile(String mobile);

    //更新用户
    void update(User user);

    void updatePhone(User user);
}
