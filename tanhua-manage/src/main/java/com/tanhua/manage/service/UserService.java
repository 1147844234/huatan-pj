package com.tanhua.manage.service;

import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.mongo.Video;
import com.tanhua.domain.vo.CommentVo;
import com.tanhua.domain.vo.MovementsVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.VideoVo;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.mongo.CommentApi;
import com.tanhua.dubbo.api.mongo.PublishApi;
import com.tanhua.dubbo.api.mongo.VideoApi;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserService {

    @Reference
    private UserInfoApi userInfoApi;
    @Reference
    private VideoApi videoApi;
    @Reference
    private PublishApi publishApi;
    @Reference
    private CommentApi commentApi;


    /**
     * 接口名称：用户数据翻页
     */
    public ResponseEntity<Object> findByPage(Integer page, Integer pagesize) {
        IPage<UserInfo> iPage = userInfoApi.findByPage(page, pagesize);
        PageResult pageResult = new PageResult(page, pagesize, (int) iPage.getTotal(), iPage.getRecords());
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 需求描述：根据用户id查询
     */
    public ResponseEntity<Object> findById(Long userId) {
        UserInfo userInfo = userInfoApi.findById(userId);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 接口名称：视频记录翻页
     */
    public ResponseEntity<Object> findVideosList(Integer page, Integer pagesize, Long userId) {
        //1. 分页查询
        PageResult pageResult = videoApi.findByPage(page, pagesize, userId);
        //2. 获取分页数据
        List<Video> videoList = (List<Video>) pageResult.getItems();
        //3. 创建vo集合，把分页数据转换为vo集合
        List<VideoVo> voList = new ArrayList<>();
        //4. 分装数据
        if (videoList != null && videoList.size() > 0) {
            for (Video video : videoList) {
                // 4.1 创建vo对象
                VideoVo vo = new VideoVo();
                // 4.2 封装vo对象
                BeanUtils.copyProperties(video, vo);
                vo.setId(video.getId().toString());

                // 根据小视频的用户id查询
                UserInfo userInfo = userInfoApi.findById(video.getUserId());
                if (userInfo != null) {
                    BeanUtils.copyProperties(userInfo, vo);
                }
                // 设置返回数据中的签名：就是文本
                vo.setSignature(video.getText());
                vo.setCover(video.getPicUrl());
                vo.setHasFocus(0);
                vo.setHasLiked(0);

                voList.add(vo);
            }
        }
        //5. 设置vo集合到pageResult中
        pageResult.setItems(voList);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 接口名称：动态分页
     */
    public ResponseEntity<Object> findMovementsList(Integer page, Integer pagesize, Long userId, String state) {
        //--------------------优化查询---------------------
        PageResult pageResult;
        /*
         * ------------------------------------------------------
         * 注：估计是前端有个小bug,点击已审核、待审核、已驳回，再点击全部
         *     前端请求会传一个字符串state=''/state="",所用下面加判断
         * ------------------------------------------------------
         */
        if (userId == null && state == null || userId == null && state.equals("''") || userId == null && state.equals("")) {
            //查所有动态
            pageResult = publishApi.findAll(page, pagesize);
        } else if (userId == null) {
            //查询已审核、待审核、已驳回
            pageResult = publishApi.findAuditing(page, pagesize, state);
        } else {
            //带条件查询
            pageResult = publishApi.findByPage(page, pagesize, userId, state);
        }

        //获取查询的动态列表
        List<Publish> publishList = (List<Publish>) pageResult.getItems();
        //创建vo集合、封装返回结果
        List<MovementsVo> voList = new ArrayList<>();
        if (publishList != null && publishList.size() > 0) {
            for (Publish publish : publishList) {
                // 创建并封装vo对象
                MovementsVo vo = new MovementsVo();
                // 对象拷贝：设置publis动态数据
                BeanUtils.copyProperties(publish, vo);
                vo.setId(publish.getId().toString());
                vo.setImageContent(publish.getMedias().toArray(new String[]{}));

                // 根据用户id查询
                UserInfo userInfo = userInfoApi.findById(publish.getUserId());
                if (userInfo != null) {
                    // 对象拷贝：设置userInfo动态数据
                    BeanUtils.copyProperties(userInfo, vo);
                    if (userInfo.getTags() != null) {
                        vo.setTags(userInfo.getTags().split(","));
                    }
                }
                // 设置动态的用户id
                vo.setUserId(publish.getUserId());
                vo.setDistance("50米");
                // 设置时间
                vo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(publish.getCreated())));
                vo.setHasLiked(0);
                vo.setHasLoved(0);
                vo.setState(state);
                voList.add(vo);
            }
        }
        // 设置vo集合
        pageResult.setItems(voList);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 接口名称：动态详情
     */
    public ResponseEntity<Object> findMovementsById(String publishId) {
        Publish publish = publishApi.findById(publishId);
        // 创建并封装vo对象
        MovementsVo vo = new MovementsVo();
        // 对象拷贝：设置publis动态数据
        BeanUtils.copyProperties(publish, vo);
        vo.setId(publish.getId().toString());
        vo.setImageContent(publish.getMedias().toArray(new String[]{}));

        // 根据用户id查询
        UserInfo userInfo = userInfoApi.findById(publish.getUserId());
        if (userInfo != null) {
            // 对象拷贝：设置userInfo动态数据
            BeanUtils.copyProperties(userInfo, vo);
            if (userInfo.getTags() != null) {
                vo.setTags(userInfo.getTags().split(","));
            }
        }
        // 设置动态的用户id
        vo.setUserId(publish.getUserId());
        vo.setDistance("50米");
        // 设置时间
        vo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(publish.getCreated())));
        vo.setHasLiked(0);
        vo.setHasLoved(0);
        return ResponseEntity.ok(vo);
    }


    /**
     * 接口名称：评论列表翻页
     */
    public ResponseEntity<Object> findCommentsById(String publishId, Integer page, Integer pagesize) {
        PageResult pageResult = commentApi.queryCommentsList(publishId, page, pagesize);
        List<Comment> commentList = (List<Comment>) pageResult.getItems();
        //3. 创建返回的vo集合
        List<CommentVo> voList = new ArrayList<>();
        //4. 封装vo集合
        if (commentList != null && commentList.size() > 0) {
            for (Comment comment : commentList) {
                // 创建vo对象
                CommentVo vo = new CommentVo();
                // 根据用户id查询
                UserInfo userInfo = userInfoApi.findById(comment.getUserId());
                if (userInfo != null) {
                    BeanUtils.copyProperties(userInfo, vo);
                }
                // 设置评论id
                vo.setId(comment.getId().toString());
                // 设置评论内容
                vo.setContent(comment.getContent());
                // 设置评论时间
                vo.setCreateDate(new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date(comment.getCreated())));
                // 点赞数量、是否点赞给默认值
                vo.setLikeCount(0);
                vo.setHasLiked(0);

                // 添加到集合
                voList.add(vo);
            }
        }
        //5. 把vo集合设置到分页对象中并返回
        pageResult.setItems(voList);
        return ResponseEntity.ok(pageResult);
    }

    //------------------------------项目实战----------------------------------

    /**
     * 动态通过
     */
    public ResponseEntity<Object> PublishYer(String[] publishes) {

        //返回消息
        String message = "";
        //判断pidS
        if (publishes == null) {
            message = "审核失败，起码选择一条动态！";
            return ResponseEntity.ok(message);
        }
        //拿到每个pid,修改状态
        for (String pid : publishes) {
            publishApi.updateState(pid, 1);
            message = "通过";
        }
        return ResponseEntity.ok(message);
    }

    /**
     * 动态拒绝
     */
    public ResponseEntity<Object> PublishNo(String[] publishes) {
        //返回消息
        String message = "";
        //判断pidS
        if (publishes == null) {
            message = "审核失败，起码选择一条动态！";
            return ResponseEntity.ok(message);
        }
        //拿到每个pid,修改状态
        for (String pid : publishes) {
            publishApi.updateState(pid, 2);
            message = "拒绝";
        }
        return ResponseEntity.ok(message);
    }
}
