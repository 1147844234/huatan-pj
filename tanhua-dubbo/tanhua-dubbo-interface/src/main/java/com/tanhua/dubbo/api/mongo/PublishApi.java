package com.tanhua.dubbo.api.mongo;

import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.vo.PageResult;

import java.util.List;

public interface PublishApi {

    /**
     * 发布动态，往动态表、自己的相册表、好友的时间线表记录动态
     * @param publish
     */
    void save(Publish publish);

    /**
     * 根据登陆用户id，分页查询好友动态
     */
    PageResult queryPublishList(Integer page, Integer pagesize, Long userId);

    /**
     * 根据登陆用户id，分页查询推荐动态：recommend_quanzi
     */
    PageResult queryRecommendList(Integer page, Integer pagesize, Long userId);

    /**
     * 根据动态id查询
     */
    Publish findById(String publishId);

    /**
     * 分页查询用户的动态
     */
    PageResult findByPage(Integer page, Integer pagesize, Long userId, String state);

    void updateState(String publishId, Integer state);

    List<Publish> findByPids(List<Long> pidList);

    /**
     * 查询个人动态
     */
    PageResult findByAlbum(Long userId, int page, int pagesize);


    /**
     * 分页查询所有用户动态
     */
    PageResult findAll(Integer page, Integer pagesize);

    /**
     * 查询
     * 已审核 = 1
     * 待审核 = 0
     * 已驳回 = 2
     */
    PageResult findAuditing(Integer page, Integer pagesize, String state);

    PageResult queryUserPublishList(Long id, Integer page, Integer pageSize);
}
