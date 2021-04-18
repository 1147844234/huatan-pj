package com.tanhua.manage.controller;


import com.tanhua.manage.service.FreezeService;
import com.tanhua.manage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("manage")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private FreezeService freezeService;

    /**
     * 接口名称：用户数据翻页
     * 接口路径：GET/manage/users
     */
    @GetMapping("users")
    public ResponseEntity<Object> findByPage(Integer page, Integer pagesize) {
        return userService.findByPage(page, pagesize);
    }

    /**
     * 接口名称：用户基本资料
     * 接口路径：GET/manage/users/:userID
     * 需求描述：根据用户id查询
     */
    @GetMapping("users/{userID}")
    public ResponseEntity<Object> findById(@PathVariable("userID") Long userId) {
        return userService.findById(userId);
    }

    /**
     * 接口名称：视频记录翻页
     * 接口路径：GET/manage/videos
     */
    @GetMapping("videos")
    public ResponseEntity<Object> findVideosList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pagesize, Long uid) {
        return userService.findVideosList(page, pagesize, uid);
    }

    //-----------------------------优化查询-------------------------

    /**
     * 接口名称：动态分页
     * 接口路径：GET/manage/messages
     */
    @GetMapping("messages")
    public ResponseEntity<Object> findMovementsList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pagesize, Long uid, String state) {
        return userService.findMovementsList(page, pagesize, uid, state);
    }

    /**
     * 接口名称：动态详情
     * 接口路径：GET/manage/messages/:id
     */
    @GetMapping("messages/{id}")
    public ResponseEntity<Object> findMovementsById(@PathVariable("id") String publishId) {
        return userService.findMovementsById(publishId);
    }

    /**
     * 接口名称：评论列表翻页
     * 接口路径：GET/manage/messages/comments
     */
    @GetMapping("/messages/comments")
    public ResponseEntity<Object> findCommentsById(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pagesize,
            @RequestParam(name = "messageID") String publishId) {
        return userService.findCommentsById(publishId, page, pagesize);
    }

    //-------------------------------------实战----------------------------------

    /**
     * 动态通过
     * 接口路径：POST/manage/messages/pass
     */
    @PostMapping("/messages/pass")
    public ResponseEntity<Object> pass(@RequestBody String[] publishes) {
        return userService.PublishYer(publishes);
    }

    /**
     * 动态拒绝
     * 接口路径：POST/manage/messages/reject
     */
    @PostMapping("/messages/reject")
    public ResponseEntity<Object> reject(@RequestBody String[] publishes) {
        return userService.PublishNo(publishes);
    }

    /**
     * 接口名称：用户冻结操作
     * 接口路径：POST/manage/users/freeze
     */
    @PostMapping("users/freeze")
    public ResponseEntity<Object> freezeUser(@RequestBody Map<String, Object> map) {
        return freezeService.freezeUser(map);
    }

    /**
     * 接口名称：用户解冻操作
     * 接口路径：POST/manage/users/unfreeze
     */
    @PostMapping("users/unfreeze")
    public ResponseEntity<Object> unFreezeUser(@RequestBody Map<String, Object> map) {
        return freezeService.unFreezeUser(map);
    }

}
