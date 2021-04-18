package com.tanhua.server.controller;

import com.tanhua.domain.vo.PageResult;
import com.tanhua.server.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("smallVideos")
public class VideoController {

    @Autowired
    private VideoService videoService;


    /**
     * 接口名称：小视频列表
     * 接口路径：GET/smallVideos
     */
    @GetMapping
    public ResponseEntity<Object> queryVideoList(
            @RequestParam(defaultValue = "1")Integer page,
            @RequestParam(defaultValue = "10")Integer pagesize)  {
        // 解决前端传递page=0的问题
        if (page < 1) page = 1;
        PageResult pageResult = videoService.queryVideoList(page, pagesize);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 接口名称：视频上传
     * 接口路径：POST/smallVideos
     */
    @PostMapping
    public ResponseEntity<Object> uploadVideos(String text,
            MultipartFile videoThumbnail,MultipartFile videoFile) throws IOException {
        return videoService.uploadVideos(text,videoThumbnail,videoFile);
    }

    /**
     * 接口名称：视频用户关注
     * 接口路径：POST/smallVideos/:uid/userFocus
     */
    @PostMapping("{uid}/userFocus")
    public ResponseEntity<Object> followUser(@PathVariable("uid") Long followUserId)  {
        return videoService.followUser(followUserId);
    }

    /**
     * 接口名称：视频用户关注 - 取消
     * 接口路径：POST/smallVideos/:uid/userUnFocus
     */
    @PostMapping("{uid}/userUnFocus")
    public ResponseEntity<Object> unfollowUser(@PathVariable("uid") Long followUserId)  {
        return videoService.unfollowUser(followUserId);
    }

}
