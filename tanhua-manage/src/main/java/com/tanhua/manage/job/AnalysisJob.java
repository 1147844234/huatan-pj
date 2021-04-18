package com.tanhua.manage.job;

import com.tanhua.manage.service.AnalysisByDayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 定时执行的任务类
 */
@Component
@Slf4j
public class AnalysisJob {

    // 注入service
    @Autowired
    private AnalysisByDayService analysisByDayService;

    /**
     * 任务调度表达式： 秒 分钟 小时 日 月 ?
     * 为了测试方便，暂时先设置为每1分钟执行一次任务调度
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void execute(){
        log.info("执行任务调度开始：");
        analysisByDayService.analysis();
        log.info("执行任务调度结束：");
    }
}
