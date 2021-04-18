package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.Announcement;

public interface AnnouncementApi {
    /**
     * 公告列表
     */
    Page<Announcement> findAll(Integer page, Integer pagesize);
}
