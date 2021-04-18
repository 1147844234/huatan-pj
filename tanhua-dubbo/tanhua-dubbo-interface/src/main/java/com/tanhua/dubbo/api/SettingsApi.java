package com.tanhua.dubbo.api;

import com.tanhua.domain.db.Settings;

public interface SettingsApi {
    /**
     * 根据用户查询用户的通知设置表
     * @param userId 当前登陆用户id
     * @return
     */
    Settings findByUserId(Long userId);

    /**
     * 保存通知设置
     * @param settings
     */
    void save(Settings settings);

    /**
     * 修改通知设置
     * @param settings
     */
    void update(Settings settings);
}
