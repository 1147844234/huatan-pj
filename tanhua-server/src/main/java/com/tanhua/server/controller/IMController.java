package com.tanhua.server.controller;

import com.tanhua.server.service.IMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("messages")
public class IMController {

    @Autowired
    private IMService imService;

    /**
     * 接口名称：联系人添加
     * 接口路径：POST/messages/contacts
     */
    @PostMapping("contacts")
    public ResponseEntity<Object> addContacts(@RequestBody Map<String, Object> map) {
        Integer friendId = (Integer) map.get("userId");
        return imService.addContacts(friendId.longValue());
    }

    /**
     * 接口名称：联系人列表
     * 接口路径：GET/messages/contacts
     */
    @GetMapping("contacts")
    public ResponseEntity<Object> contactsList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pagesize, String keyword) {
        return imService.contactsList(page, pagesize, keyword);
    }

//-------------------------------项目实战------------------------------------------

    /**
     * 公告列表
     * GET/messages/announcements
     */
    @GetMapping("/announcements")
    public ResponseEntity<Object> announcement(@RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer pagesize){
        return imService.findAnnouncements(page,pagesize);
    }

    /**
     * 点赞列表
     * GET/messages/likes
     * 评论类型，1-点赞，2-评论，3-喜欢
     */
    @GetMapping("likes")
    public  ResponseEntity<Object> likesList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pagesize){
       return imService.likesList(1,page,pagesize);
    }

    /**
     * 喜欢列表
     * GET/messages/loves
     * 评论类型，1-点赞，2-评论，3-喜欢
     */
    @GetMapping("loves")
    public  ResponseEntity<Object> loves(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pagesize){
        return imService.likesList(3,page,pagesize);
    }

    /**
     * 评论列表
     * GET/messages/comments
     * 评论类型，1-点赞，2-评论，3-喜欢
     */
    @GetMapping("comments")
    public  ResponseEntity<Object> comments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pagesize){
        return imService.likesList(2,page,pagesize);
    }
}
