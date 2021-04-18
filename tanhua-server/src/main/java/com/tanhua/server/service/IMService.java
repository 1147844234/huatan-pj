package com.tanhua.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.commons.template.HuanXinTemplate;
import com.tanhua.domain.db.Announcement;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.Friend;
import com.tanhua.domain.vo.AnnouncementVo;
import com.tanhua.domain.vo.ContractVo;
import com.tanhua.domain.vo.MessageLikeVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.AnnouncementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.mongo.CommentApi;
import com.tanhua.dubbo.api.mongo.FriendApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class IMService {
    @Reference
    private FriendApi friendApi;
    @Autowired
    private HuanXinTemplate huanXinTemplate;
    @Reference
    private UserInfoApi userInfoApi;
    @Reference
    private CommentApi commentApi;
    @Reference
    AnnouncementApi announcementApi;

    /**
     * 接口名称：联系人添加
     */
    public ResponseEntity<Object> addContacts(Long friendId) {
        //1. 获取用户id
        Long userId = UserHolder.getUserId();
        //2. 保存好友关系到mongodb中的tanhua_users表中
        friendApi.save(userId, friendId);
        //3. 注册好友关系到环信
        huanXinTemplate.contactUsers(userId, friendId);
        return ResponseEntity.ok(null);
    }

    /**
     * 接口名称：联系人列表
     */
    public ResponseEntity<Object> contactsList(Integer page, Integer pagesize, String keyword) {

        Long userId = UserHolder.getUserId();
        //1. 分页查询联系人
        PageResult pageResult = friendApi.findFriendByUserId(userId, page, pagesize);
        //2. 获取分页查询的数据
        List<Friend> friendList = (List<Friend>) pageResult.getItems();

        //3. 创建并封装返回的vo集合
        List<ContractVo> voList = new ArrayList<>();
        // 遍历好友列表
        if (friendList != null && friendList.size() > 0) {
            for (Friend friend : friendList) {
                // 获取好友id
                Long friendId = friend.getFriendId();
                // 根据好友id查询
                UserInfo userInfo = userInfoApi.findById(friendId);
                // 创建返回的vo对象
                ContractVo vo = new ContractVo();
                // 对象拷贝
                BeanUtils.copyProperties(userInfo, vo);
                // 设置好友用户id
                vo.setUserId(friendId.toString());
                // 添加到集合
                voList.add(vo);
            }
        }

        // 设置到pageResult中
        pageResult.setItems(voList);
        return ResponseEntity.ok(pageResult);
    }

//-------------------------------项目实战------------------------------------------

    /**
     * 公告列表
     */
    public ResponseEntity<Object> findAnnouncements(Integer page, Integer pagesize) {
        //调用api通过分页对象page获取广播数据
        Page<Announcement> pages = announcementApi.findAll(page, pagesize);
        List<Announcement> announcementList = pages.getRecords();
        //封装数据到vo
        List<AnnouncementVo> voList = new ArrayList<>();
        if (announcementList != null && announcementList.size() > 0) {
            for (Announcement an : announcementList) {
                AnnouncementVo vo = new AnnouncementVo();
                BeanUtils.copyProperties(an, vo);
                vo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(an.getCreated()));
                voList.add(vo);
            }
        }

        //返回前端需要的分页对象
        PageResult result = new PageResult(page, pagesize, (int) pages.getTotal(), voList);
        return ResponseEntity.ok(result);
    }

    /**
     * 点赞列表
     * 评论类型，1-点赞，2-评论，3-喜欢 commentType
     */
    public ResponseEntity<Object> likesList(Integer commentType, Integer page, Integer pagesize) {
        //获取评论登录用户的数据
        PageResult pageResult = commentApi.findCommentTypeList(commentType, page, pagesize, UserHolder.getUserId());
        List<Comment> commentList = (List<Comment>) pageResult.getItems();

        //封装vo
        List<MessageLikeVo> voList = new ArrayList<>();
        if (commentList.size() > 0 && commentList != null) {
            for (Comment comment : commentList) {
                //通过评论人id得到评论人信息
                MessageLikeVo vo = new MessageLikeVo();
                UserInfo userInfo = userInfoApi.findById(comment.getUserId());
                //不为空，对象拷贝
                if(userInfo!=null){
                    BeanUtils.copyProperties(userInfo,vo);
                }
                //封装其他数据
                vo.setId(comment.getId().toString());
                vo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd").format(userInfo.getCreated()));

                voList.add(vo);
            }
        }
        pageResult.setItems(voList);
        return ResponseEntity.ok(pageResult);
    }

}
