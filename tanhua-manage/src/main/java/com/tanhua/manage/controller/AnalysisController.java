package com.tanhua.manage.controller;

import com.tanhua.manage.service.AnalysisByDayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("dashboard")
public class AnalysisController {
    @Autowired
    private AnalysisByDayService analysisByDayService;
    /**
     * 接口名称：概要统计信息
     * 接口路径：GET/dashboard/summary
     */
    @GetMapping("summary")
    public ResponseEntity<Object> summary(){
        return analysisByDayService.summary();
    }

    /**
     * 接口名称：新增、活跃用户、次日留存率
     * 接口路径：GET/dashboard/users
     */
    @GetMapping("users")
    public ResponseEntity<Object> queryByTime(Long sd,Long ed,Integer type){
        return analysisByDayService.queryByTime(sd,ed,type);
    }
}