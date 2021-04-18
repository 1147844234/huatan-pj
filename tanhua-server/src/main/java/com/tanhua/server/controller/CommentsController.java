package com.tanhua.server.controller;

import com.tanhua.server.service.CommentService;
import com.tanhua.server.service.MovementsMQService;
import com.tanhua.server.service.MovementsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("comments")
public class CommentsController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private MovementsMQService movementsMQService;

    /**
     * 接口名称：评论列表
     * 接口路径：GET/comments
     */
    @GetMapping
    public ResponseEntity<Object> queryCommentsList(String movementId,
            @RequestParam(defaultValue = "1")Integer page,
            @RequestParam(defaultValue = "10")Integer pagesize) throws IOException {
        // 解决前端传递page=0的问题
        if (page < 1) page = 1;
        return commentService.queryCommentsList(movementId,page,pagesize);
    }

    /**
     * 接口名称：评论-提交
     * 接口路径：POST/comments
     */
    @PostMapping
    public ResponseEntity<Object> saveComments(@RequestBody Map<String,String> map){
        String publishId = map.get("movementId");
        String comment = map.get("comment");
        //【发送消息，用于计算操作评分】
        movementsMQService.commentPublishMsg(publishId);
        return commentService.saveComments(publishId,comment);
    }

}
