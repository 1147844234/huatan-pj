package com.tanhua.server.controller;


import com.tanhua.server.service.PeachBlossomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 桃花传音
 */
@RestController
@RequestMapping
public class PeachBlossomController {

    @Autowired
    private PeachBlossomService peachBlossomService;

    /**
     * 桃花传音-发送语音（学生实战）
     * 接口路径：POST/peachblossom
     */
    @PostMapping("peachblossom")
    public ResponseEntity<Object> send(MultipartFile soundFile) throws IOException {
        return peachBlossomService.upload(soundFile);
    }

    /**
     * 桃花传音-接收语音（学生实战）下载
     * 接口路径：/peachblossom
     */
    @GetMapping("peachblossom")
    public ResponseEntity<Object> receive() {
        return peachBlossomService.download();
    }
}

