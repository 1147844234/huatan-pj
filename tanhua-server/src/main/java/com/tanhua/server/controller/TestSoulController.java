package com.tanhua.server.controller;

import com.tanhua.domain.db.Answers;
import com.tanhua.domain.vo.ConclusionVo;
import com.tanhua.domain.vo.PaperListVo;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.TestSoulService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/testSoul")
@Slf4j
public class TestSoulController {

    @Autowired
    private TestSoulService testSoulService;


    /**
     * 接口名称：测灵魂-问卷列表
     * 接口路径：GET/testSoul
     * 返回一个PaperListVo的list集合
     */
    @GetMapping
    public ResponseEntity<Object> paperList() {
        try {
            List<PaperListVo> list = testSoulService.queryPaperList();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //如果以上出现异常则返回服务器异常错误---500错误
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /*
     *提交问卷
     * 前端传来试题的id（可知是初中高） 和用户对这道题的选项id（确定分值）
     * 接收返回值有讲究根据使用String接受得知json格式为key为answers  value为一个对象数组  使用如下类型接受
     */
    @PostMapping
    public ResponseEntity<Object> submitTestPaper(@RequestBody Map<String, List<Answers>> map){

        try {
            //返回报告ID 获取所有键也就是一个list集合 Answers是一个接受的vo只有quetionId和optionId两个属性
            Collection<List<Answers>> values = map.values();

            for (List<Answers> value : values) {
                System.out.println(value);
            }
            String reportId = testSoulService.submitTestPaper(map);
            //如果不为空就成功
            if(StringUtils.isNotEmpty(reportId)){
                return ResponseEntity.ok(reportId);
            }
        } catch (Exception e) {
            //输出错误日志
            log.error("提交问卷失败~ userId = " + UserHolder.get().getId(), e);
        }
        //返回服务器端错误
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }



    /**
     * @Description: 测灵魂-查看结果
     * id 为reportid 就是报告id
     */
    @GetMapping("/report/{id}")
    public ResponseEntity<Object> getReport(@PathVariable("id") String id) {
        log.info("-----id------:"+id);
        //调业务层
        try {
            ConclusionVo conclusionVo = testSoulService.getReport(id);
            if (null != conclusionVo) {
                return ResponseEntity.ok(conclusionVo);
            }
        } catch (Exception e) {
            log.error("获取测试结果失败......" + e);
        }
        //返回服务器端错误
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


}
