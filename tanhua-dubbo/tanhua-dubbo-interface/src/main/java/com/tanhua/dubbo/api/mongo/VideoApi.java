package com.tanhua.dubbo.api.mongo;

import com.tanhua.domain.mongo.FollowUser;
import com.tanhua.domain.mongo.Video;
import com.tanhua.domain.vo.PageResult;

import java.util.List;

public interface VideoApi {
    /**
     * 分页查询小视频
     */
    PageResult findByPage(Integer page, Integer pagesize,Long...userId);

    /**
     * 保存视频
     */
    void save(Video video);

    /**
     * 视频用户关注
     */
    void followUser(FollowUser followUser);

    /**
     * 调用api，取消关注
     */
    void unfollowUser(FollowUser followUser);

    Video findById(String videoId);

    List<Video> findByVids(List<Long> vidList);
}
