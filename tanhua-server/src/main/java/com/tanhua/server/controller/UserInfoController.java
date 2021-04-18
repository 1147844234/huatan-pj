package com.tanhua.server.controller;

import com.tanhua.domain.db.UserInfo;
import com.tanhua.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
@Slf4j
public class UserInfoController {

    @Autowired
    private UserService userService;

    /**
     * 接口名称：用户资料 - 读取
     * 接口路径：GET/users
     * 需求描述：根据用户id，查询用户信息
     * 用户ID说明：
     *   1.根据用户id查询用户详情
     *   2.如果userID不为NULL，根据用户id查询
     *   3.如果用户id为空，huanxinID不为NULL，根据环信ID查询
     *   4.如果userID与huanxinID都为空，从token获取用户id
     */
    @GetMapping
    public ResponseEntity<Object> findById(Long userID,Long huanxinID) {
        log.info("接口名称：用户资料");
        return userService.findById(userID,huanxinID);
    }

    /**
     * 接口名称：用户资料 - 保存
     * 接口路径：PUT/users
     * 需求描述：更新用户信息
     */
    @PutMapping
    public ResponseEntity<Object> updateUserInfo(
            @RequestBody UserInfo userInfo) {
        log.info("接口名称：用户资料 - 保存");
        return userService.updateUserInfo(userInfo);
    }

    /**
     * 接口名称：互相喜欢，喜欢，粉丝
     * 接口路径：GET/users/counts
     */
    @GetMapping("counts")
    public ResponseEntity<Object> queryCounts() {
        return userService.queryCounts();
    }

    /**
     * 接口名称：互相喜欢、喜欢、粉丝、谁看过我
     * 接口路径：GET/users/friends/:type
     * 路径参数type:
     *   1 互相关注
     *   2 我关注
     *   3 粉丝
     *   4 谁看过我
     */
    @GetMapping("friends/{type}")
    public ResponseEntity<Object> queryUserLikeList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pagesize,
            @PathVariable("type") Integer type) {
        if (page<1){
            page = 1;
        }
        return userService.queryUserLikeList(page,pagesize,type);
    }

    /**
     * 接口名称：粉丝 - 喜欢
     * 接口路径：POST/users/fans/:uid
     */
    @PostMapping("fans/{uid}")
    public ResponseEntity<Object> fansLike(@PathVariable("uid") Long likeUserId){
        return userService.fansLike(likeUserId);
    }

    /**
     * 取消-喜欢
     * /users/like/:uid
     */
    @DeleteMapping("/like/{uid}")
    public  ResponseEntity<Object> UnLike(@PathVariable("uid")Long likeUserId){
        return userService.UnLike(likeUserId);
    }
}
