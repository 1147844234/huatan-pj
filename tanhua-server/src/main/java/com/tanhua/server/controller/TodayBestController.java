package com.tanhua.server.controller;

import com.tanhua.domain.vo.CardsUserVo;
import com.tanhua.domain.vo.RecommendQueryVo;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.TodayBestService;
import com.tanhua.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("tanhua")
public class TodayBestController {
    @Autowired
    private TodayBestService todayBestService;
    @Autowired
    private UserService userService;

    /**
     * 接口名称：今日佳人
     * 接口路径：GET/tanhua/todayBest
     * 需求分析：登陆后进入首页，显示今日佳人，查询推荐用户表返回缘分值最高的用户
     */
    @GetMapping("todayBest")
    public ResponseEntity<Object> queryTodayBest() {
        return todayBestService.queryTodayBest();
    }

    /**
     * 接口名称：推荐朋友 (首页推荐)
     * 接口路径：GET/tanhua/recommendation
     * 需求描述：登陆后进入首页，按照缘分值倒序分页显示推荐用户
     */
    @GetMapping("recommendation")
    public ResponseEntity<Object> queryRecommendation(RecommendQueryVo recommendQueryVo) {
        return todayBestService.queryRecommendation(recommendQueryVo);
    }

    /**
     * 接口名称：佳人信息
     * 接口路径：GET/tanhua/:id/personalInfo
     * 需求描述：首页推荐用户，根据推荐用户id查询
     */
    @GetMapping("{id}/personalInfo")
    public ResponseEntity<Object> queryPersonalInfo(
            @PathVariable("id") Long recommendUserId) {
        return todayBestService.queryPersonalInfo(recommendUserId);
    }

    /**
     * 接口名称：查询陌生人问题
     * 接口路径：GET/tanhua/strangerQuestions
     */
    @GetMapping("strangerQuestions")
    public ResponseEntity<Object> strangerQuestions(Long userId) {
        return todayBestService.strangerQuestions(userId);
    }

    /**
     * 接口名称：回复陌生人问题
     * 接口路径：POST/tanhua/strangerQuestions
     */
    @PostMapping("strangerQuestions")
    public ResponseEntity<Object> replyQuestion(@RequestBody Map<String, Object> map) {
        // 获取请求参数
        Integer userId = (Integer) map.get("userId");
        String reply = (String) map.get("reply");
        // 调用api，完成回复陌生人问题
        return todayBestService.replyQuestion(userId.longValue(), reply);
    }

    /**
     * 接口名称：搜附近
     * 接口路径：GET/tanhua/search
     */
    @GetMapping("search")
    public ResponseEntity<Object> searchNear(String gender, Long distance) {
        return todayBestService.searchNear(gender, distance);
    }

    /**
     * 接口名称：探花-左滑右滑
     * 接口路径：GET/tanhua/cards
     */
    @GetMapping("cards")
    public ResponseEntity<Object> findCardsList(){
        Integer page = 1;
        Integer pagesize = 10;
        Long userId = UserHolder.getUserId();
        System.out.println("访问了探花划卡功能");
        return userService.findCardsList(userId,page,pagesize);
    }

    /**
     * 接口名称：探花-不喜欢
     * 接口路径：GET/tanhua/:id/unlove
     */
    @GetMapping("{id}/unlove")
    public ResponseEntity<Object> cardsUnlove(@PathVariable("id")String id){
        System.out.println("用户进行了一次左滑");
        Integer uid = Integer.parseInt(id);
        return userService.cardsUnlove(uid);
    }

    /**
     * 接口名称：探花-喜欢
     * 接口路径：GET/tanhua/:id/love
     */
    @GetMapping("{id}/love")
    public ResponseEntity<Object> love(@PathVariable("id")String id){
        System.out.println("用户进行了一次右滑");
        Integer uid = Integer.parseInt(id);
        return userService.saveLove(uid);
    }

}