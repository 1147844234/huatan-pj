package com.tanhua.dubbo.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.Announcement;
import com.tanhua.dubbo.api.AnnouncementApi;
import com.tanhua.dubbo.mapper.AnnouncementMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service(timeout = 100000)
public class AnnouncementApiImpl implements AnnouncementApi {

    @Autowired
    private AnnouncementMapper announcementMapper;

    /**
     * 公告列表
     */
    @Override
    public Page<Announcement> findAll(Integer page, Integer pagesize) {
        Page<Announcement> pages = new Page<>(page,pagesize);
        QueryWrapper<Announcement> qw = new QueryWrapper<>();
        return (Page<Announcement>) announcementMapper.selectPage(pages,qw);
    }
}
