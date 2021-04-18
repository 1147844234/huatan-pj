package com.tanhua.server.controller;

import com.tanhua.domain.mongo.Publish;
import com.tanhua.server.service.CommentService;
import com.tanhua.server.service.MovementsMQService;
import com.tanhua.server.service.MovementsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("movements")
public class MovementsController {

    @Autowired
    private MovementsService movementsService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private MovementsMQService movementsMQService;

    /**
     * 接口名称：动态-发布
     * 接口路径：POST/movements
     * 需求描述：发布动态，往动态表、自己的相册表、好友的时间线表记录动态
     */
    @PostMapping
    public ResponseEntity<Object> saveMovements(
            Publish publish, MultipartFile[] imageContent) throws IOException {
        ResponseEntity<Object> entity =
                movementsService.saveMovements(publish, imageContent);
        HttpStatus status = entity.getStatusCode();
        if(status.equals(200)){
            //【发送消息，用于计算操作评分】
            movementsMQService.publishMsg(publish.getId().toString());
        }

        return entity;
    }

    /**
     * 接口名称：好友动态
     * 接口路径：GET/movements
     * 需求分析：查询好友发布的动态（朋友圈），查询quanzi_time_line_登陆用户id
     */
    @GetMapping
    public ResponseEntity<Object> queryPublishMovementsList(
            @RequestParam(defaultValue = "1")Integer page,
            @RequestParam(defaultValue = "10")Integer pagesize) throws IOException {
        // 解决前端传递page=0的问题
        if (page < 1) page = 1;
        return movementsService.queryPublishMovementsList(page, pagesize);
    }

    /**
     * 接口名称：推荐动态
     * 接口路径：GET/movements/recommend
     * 需求分析：查询登陆用户的推荐动态，查询recommend_quanzi表
     */
    @GetMapping("recommend")
    public ResponseEntity<Object> queryRecommendPublishList(
            @RequestParam(defaultValue = "1")Integer page,
            @RequestParam(defaultValue = "10")Integer pagesize) throws IOException {
        // 解决前端传递page=0的问题
        if (page < 1) {page = 1;}
        return movementsService.queryRecommendPublishList(page,pagesize);
    }

    /**
     * 接口名称：动态-点赞
     * 接口路径：GET/movements/:id/like
     */
    @GetMapping("{id}/like")
    public ResponseEntity<Object> likeComment(@PathVariable("id") String publishId){
        //【发送消息，用于计算操作评分】
        movementsMQService.likePublishMsg(publishId);
        return commentService.likeComment(publishId);
    }

    /**
     * 接口名称：动态-取消点赞
     * 接口路径：GET/movements/:id/dislike
     */
    @GetMapping("{id}/dislike")
    public ResponseEntity<Object> dislikeComment(@PathVariable("id") String publishId){
        //【发送消息，用于计算操作评分】
        movementsMQService.disLikePublishMsg(publishId);
        return commentService.dislikeComment(publishId);
    }

    /**
     * 接口名称：动态-喜欢
     * 接口路径：GET/movements/:id/love
     */
    @GetMapping("{id}/love")
    public ResponseEntity<Object> loveComment(@PathVariable("id") String publishId){
        //【发送消息，用于计算操作评分】
        movementsMQService.lovePublishMsg(publishId);
        return commentService.loveComment(publishId);
    }

    /**
     * 接口名称：动态-取消喜欢
     * 接口路径：GET/movements/:id/unlove
     */
    @GetMapping("{id}/unlove")
    public ResponseEntity<Object> unloveComment(@PathVariable("id") String publishId){
        //【发送消息，用于计算操作评分】
        movementsMQService.disLovePublishMsg(publishId);
        return commentService.unloveComment(publishId);
    }

    /**
     * 接口名称：单条动态
     * 接口路径：GET/movements/:id
     * 需求分析：根据publishId查询单条动态，再把查询的Publish对象封装为MovementsVo对象
     */
    @GetMapping("{id}")
    public ResponseEntity<Object> queryMovementsById(@PathVariable("id") String publishId){
        return commentService.queryMovementsById(publishId);
    }

    /**
     * 接口名称：谁看过我
     * 接口路径：GET/movements/visitors
     * 需求描述：第一次显示最近5位访客；再次访问显示最近的访客
     */
    @GetMapping("visitors")
    public ResponseEntity<Object> queryVisitorList(){
        return commentService.queryVisitorList();
    }

    /**
     * 用户动态
     * 接口路径: GET/movements/all
     */
    @GetMapping("all")
    public ResponseEntity<Object> queryAlbumList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pagesize, Long userId) {
        //前端有一个bug，page = 0
        page = page < 1 ? 1 : page;
        return movementsService.queryAlbumList(page, pagesize, userId);
    }
}













