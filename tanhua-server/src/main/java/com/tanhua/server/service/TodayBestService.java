package com.tanhua.server.service;

import com.alibaba.fastjson.JSON;
import com.tanhua.commons.template.HuanXinTemplate;
import com.tanhua.domain.db.Question;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.*;
import com.tanhua.dubbo.api.QuestionApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.mongo.RecommendUserApi;
import com.tanhua.dubbo.api.mongo.UserLocationApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TodayBestService {

    @Reference
    private RecommendUserApi recommendUserApi;
    @Reference
    private UserInfoApi userInfoApi;

    /**
     * 接口名称：今日佳人
     * 需求分析：登陆后进入首页，显示今日佳人，查询推荐用户表返回缘分值最高的用户
     */
    public ResponseEntity<Object> queryTodayBest() {
        //1. 获取登陆用户id
        Long userId = UserHolder.getUserId();

        //2. 根据登陆用户id，查询今日佳人
        //db.recommend_user.find({userId:1}).sort({score:-1}).limit(1)
        RecommendUser recommendUser = recommendUserApi.queryWithMaxScore(userId);
        //3. 如果没有推荐数据，给一个默认值（写死）
        if (recommendUser == null) {
            recommendUser = new RecommendUser();
            recommendUser.setRecommendUserId(2L);
            recommendUser.setScore(70D);
        }

        //4. 返回TodayBestVo对象，封装结果
        TodayBestVo vo = new TodayBestVo();

        //4.1 根据推荐用户id查询用户信息
        UserInfo userInfo = userInfoApi.findById(recommendUser.getRecommendUserId());
        //4.2 对象拷贝
        if (userInfo != null) {
            BeanUtils.copyProperties(userInfo,vo);
            // 设置属性tag
            if (userInfo.getTags() != null) {
                vo.setTags(userInfo.getTags().split(","));
            }
        }
        vo.setId(userInfo.getId());
        vo.setFateValue(recommendUser.getScore().longValue());

        return ResponseEntity.ok(vo);
    }

    /**
     * 接口名称：推荐朋友 (首页推荐)
     * 需求描述：登陆后进入首页，按照缘分值倒序分页显示推荐用户
     */
    public ResponseEntity<Object> queryRecommendation(RecommendQueryVo recommendQueryVo) {
        //1. 获取登陆用户id
        Long userId = UserHolder.getUserId();
        //2. 根据登陆用户id，分页查询推荐用户 recommend_user
        PageResult pageResult = recommendUserApi.queryRecommendation(
                recommendQueryVo.getPage(),
                recommendQueryVo.getPagesize(),
                userId
        );
        //3. 获取查询的推荐用户数据
        List<RecommendUser> recommendUserList =
                (List<RecommendUser>) pageResult.getItems();
        //4. 创建返回的TodayBestVo集合
        List<TodayBestVo> voList = new ArrayList<>();
        //5. 封装voList
        if (recommendUserList != null && recommendUserList.size() > 0) {
            for (RecommendUser recommendUser : recommendUserList) {
                //5.1 . 创建vo，封装结果
                TodayBestVo vo = new TodayBestVo();
                //5.1 根据推荐用户id查询用户信息
                UserInfo userInfo = userInfoApi.findById(recommendUser.getRecommendUserId());
                //5.2 对象拷贝
                if (userInfo != null) {
                    BeanUtils.copyProperties(userInfo,vo);
                    // 设置属性tag
                    if (userInfo.getTags() != null) {
                        vo.setTags(userInfo.getTags().split(","));
                    }
                }
                vo.setId(userInfo.getId());
                vo.setFateValue(recommendUser.getScore().longValue());
                //5.3 把封装好的vo添加到集合
                voList.add(vo);
            }
        }

        //6. 把封装好的集合，设置到pageResult中并返回
        pageResult.setItems(voList);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 需求描述：首页推荐用户，根据推荐用户id查询
     */
    public ResponseEntity<Object> queryPersonalInfo(Long recommendUserId) {
        //1. 根据推荐用户id查询用户信息
        UserInfo userInfo = userInfoApi.findById(recommendUserId);
        //2. 创建返回的vo对象
        TodayBestVo vo = new TodayBestVo();
        //2.1 封装用户信息
        BeanUtils.copyProperties(userInfo,vo);
        //2.2 封装tags
        if (userInfo.getTags() != null) {
            vo.setTags(userInfo.getTags().split(","));
        }
        //2.3 查询登陆用户id与推荐用户id之间的缘分值
        long score = recommendUserApi.queryScore(UserHolder.getUserId(),recommendUserId);
        vo.setFateValue(score);
        return ResponseEntity.ok(vo);
    }

    @Reference
    private QuestionApi questionApi;

    /**
     * 接口名称：查询陌生人问题
     */
    public ResponseEntity<Object> strangerQuestions(Long userId) {
        Question question = questionApi.findByUserId(userId);
        String text = question != null ? question.getTxt() : "你很像ta哦？";
        return ResponseEntity.ok(text);
    }

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    /**
     * 接口名称：回复陌生人问题
     */
    public ResponseEntity<Object> replyQuestion(Long userId, String reply) {
        // 根据用户id查询
        UserInfo userInfo = userInfoApi.findById(UserHolder.getUserId());
        // 根据用户id查询陌生人问题
        Question question = questionApi.findByUserId(userId);
        String content = question.getTxt()==null?"你是哪位？":question.getTxt();

        // 准备消息内容: 消息格式固定
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userInfo.getId());
        map.put("nickname",userInfo.getNickname());
        map.put("strangerQuestion",content);
        map.put("reply",reply);
        // 消息内容转换为json字符串
        String msgJson = JSON.toJSONString(map);

        // 回复陌生人问题：需要发送消息到环信
        huanXinTemplate.sendMsg(userId.toString(),msgJson);
        return ResponseEntity.ok(null);
    }

    @Reference
    private UserLocationApi userLocationApi;
    /**
     * 接口名称：搜附近
     */
    public ResponseEntity<Object> searchNear(String gender, Long distance) {
        // 通过Api查询搜附近，返回UserLocationVo.因为UserLocation对象中关联的对象没有实现序列化接口
        List<UserLocationVo> userLocationList =
                userLocationApi.searNear(UserHolder.getUserId(),distance);

        // 返回的结果
        List<NearUserVo> voList = new ArrayList<>();

        // 遍历查询结果
        if (userLocationList != null && userLocationList.size()>0) {
            for (UserLocationVo userLocationVo : userLocationList) {
                // 获取附近的人的用户id
                Long userId = userLocationVo.getUserId();
                // 判断： 附近的人不能包含自己
                if (userId == UserHolder.getUserId()) {
                    continue;
                }

                // 根据用户id查询
                UserInfo userInfo = userInfoApi.findById(userId);
                // 判断：性别
                if (!userInfo.getGender().equals(gender)){
                    continue;
                }

                // 创建返回的vo
                NearUserVo vo = new NearUserVo();
                BeanUtils.copyProperties(userInfo,vo);
                vo.setUserId(userInfo.getId());
                // 添加到集合
                voList.add(vo);
            }
        }
        return ResponseEntity.ok(voList);
    }
}
