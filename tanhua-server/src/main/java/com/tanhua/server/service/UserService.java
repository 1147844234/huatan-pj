package com.tanhua.server.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.commons.template.AipFaceTemplate;
import com.tanhua.commons.template.HuanXinTemplate;
import com.tanhua.commons.template.OssTemplate;
import com.tanhua.commons.template.SmsTemplate;
import com.tanhua.domain.db.User;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Cards;
import com.tanhua.domain.vo.*;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.mongo.CardsApi;
import com.tanhua.dubbo.api.mongo.CardsUnloveApi;
import com.tanhua.dubbo.api.mongo.FriendApi;
import com.tanhua.dubbo.api.mongo.UserLikeApi;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.utils.ComputeUtil;
import com.tanhua.server.utils.JwtUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

/**
 * 创建UserService，处理业务
 */
@Service
public class UserService {
    /**
     * @Reference 1. 注入dubbo服务接口的代理对象，通过代理对象远程调用
     * 2. 注入导入的包:
     * org.apache.dubbo.config.annotation.Reference  正确
     * jdk.nashorn.internal.ir.annotations.Reference 错误
     */
    @Reference
    private UserLikeApi userLikeApi;
    @Reference
    private CardsUnloveApi cardsUnloveApi;
    @Reference
    private UserApi userApi;
    @Reference
    private CardsApi cardsApi;
    @Reference
    private UserInfoApi userInfoApi;
    @Autowired
    private SmsTemplate smsTemplate;
    @Autowired
    private AipFaceTemplate aipFaceTemplate;
    @Autowired
    private OssTemplate ossTemplate;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private HuanXinTemplate huanXinTemplate;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    private final String SMS_KEY = "SMS_KEY_";
    private final String TOKEN_KEY = "TOKEN_KEY_";
    // 获取配置文件值
    @Value("${tanhua.secret}")
    private String secret;

    /**
     * 测试方法1：根据手机号码查询
     */
    public ResponseEntity<Object> findByMobile(String mobile) {
        User user = userApi.findByMobile(mobile);
        return ResponseEntity.ok(user);
    }
    /**
     * 测试方法2：保存
     */
    public ResponseEntity<Object> save(User user) {
        try {
            int i = 1 / 0;
            Long id = userApi.save(user);
            return ResponseEntity.ok(id);
        } catch (Exception e) {
            e.printStackTrace();
            // 返回异常
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("errCode", "1200");
            errorMap.put("errMessage", "保存异常，请练习管理员！");
            return ResponseEntity.status(500).body(errorMap);
        }
    }


    /**
     * 接口名称：登录第一步---手机号登录
     * 需求描述：输入手机号码，发送验证码，存储到redis中,设置有效时间5分钟
     */
    public ResponseEntity<Object> login(String phone) {
        //1. 生成六位验证码
        String code = (int) ((Math.random() * 9 + 1) * 100000) + "";
        //2. 通过SmsTemplate，发送验证码
        //smsTemplate.sendSms(phone,code);
        code = "123456";
        //3. 验证码存储到redis中
        redisTemplate.opsForValue().set(SMS_KEY + phone, code, Duration.ofMinutes(10));
        return ResponseEntity.ok(null);
    }

    public static void main(String[] args) {
        String code = (int) ((Math.random() * 9 + 1) * 100000) + "";
        System.out.println("code = " + code);
    }


    /**
     * 接口名称：登录第二步---验证码校验
     */
    public ResponseEntity<Object> loginVerification(String phone, String code) {
        //1. 从redis中获取验证码
        String key = SMS_KEY + phone;
        String redisCode = redisTemplate.opsForValue().get(key);
        //2. 验证码校验
        if (StringUtils.isEmpty(redisCode) || !code.equals(redisCode)) {
            // 返回异常
            return ResponseEntity.status(500).body(ErrorResult.error());
        }

        //3. 删除验证码
        redisTemplate.delete(key);

        //4. 根据手机号码查询，判断是否需要自动注册
        User user = userApi.findByMobile(phone);
        boolean isNew = false;
        // 【日志类型：默认是0101 表示登陆】
        String type = "0101";
        if (user == null) {
            // 手机号码不存在，自动注册
            user = new User();
            user.setMobile(phone);
            user.setPassword(DigestUtils.md5Hex("123456"));
            // 【保存用户，接收返回值】
            Long userId = userApi.save(user);
            user.setId(userId);
            // 新用户
            isNew = true;
            // 新用户注册到环信
            huanXinTemplate.register(user.getId());
            cardsApi.creatCardsList(userId);
            //一旦客户注册，就往所有的用户列表的"cards_users_"+userId中加入这个新用户的信息
            addAllUserCardsList(userId);
            // 【注册】
            type = "0102";
        }

        //查看用户是否被冻结了登录
        String freezeKey="Freeze_User_"+user.getId();
        if(redisTemplate.hasKey(freezeKey)){
            //判断冻结时间和冻结范围
            String freezeJson = redisTemplate.opsForValue().get(freezeKey);
            Map<String, Object> map = JSON.parseObject(freezeJson, Map.class);
            String freezingTime = (String) map.get("freezingTime");
            String freezingRange = (String) map.get("freezingRange");
            Long nowTime = (Long) map.get("nowTime");
            Date date = new Date();
            date.setTime(nowTime);
            //如果为冻结登录才进入下面的操作
            if("1".equals(freezingRange)){
                if("1".equals(freezingTime)){
                    //计算解封时间
                    String afterThree = ComputeUtil.offsetDay(date, 3);
                    //提示冻结时间
                    return ResponseEntity.status(500).body(ErrorResult.freezeTime("3",afterThree));
                }else if("2".equals(freezingTime)){
                    //计算解封时间
                    String afterThree = ComputeUtil.offsetDay(date, 7);
                    //提示冻结时间
                    return ResponseEntity.status(500).body(ErrorResult.freezeTime("7",afterThree));
                }else {
                    //提示冻结时间
                    return ResponseEntity.status(500).body(ErrorResult.freezeNever());
                }
            }
        }else {
            //如果该键已过期,则解封用户,设置userinfo的状态
            UserInfo userInfo = userInfoApi.findById(user.getId());
            if(userInfo!=null) {
                userInfo.setUserStatus("1");
                userInfoApi.updateUserStatus(userInfo);
            }
        }

        // 【构造map，封装消息内容】
        Map<String,String> logMap = new HashMap<>();
        logMap.put("userId",user.getId().toString());
        logMap.put("type",type);
        logMap.put("date",new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        // 【map转换为json】
        String logString = JSON.toJSONString(logMap);
        try {
            //【发送一条MQ消息，消息内容：userId、type、date】
            rocketMQTemplate.convertAndSend("tanhua-log2",logString);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        //【需求：生成token并返回】
        String token = JwtUtils.createToken(user.getId().toString(), user.getMobile(), secret);
        // 存储用户数据  key:token  value:用户json数据
        // user对象转换为json
        String userJsonString = JSON.toJSONString(user);
        // 存储到redis中
        redisTemplate.opsForValue().set(TOKEN_KEY+token,userJsonString,Duration.ofHours(4));

        //5. 返回
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("token",token);
        resultMap.put("isNew",isNew);
        return ResponseEntity.ok(resultMap);
    }

    /**
     * 接口名称：新用户---1填写资料
     * 需求分析：完善用户信息，保存到tb_user_info表中
     */
    public ResponseEntity<Object> saveUserInfo(UserInfo userInfo, String token) {
        //1. 根据token获取用户
        User user = findUserByToken(token);

        //2. 判断
        if (user == null) {
            return ResponseEntity.status(500).body(ErrorResult.error());
        }

        //3. 设置用户详情userInfo的id
        userInfo.setId(user.getId());
        //4. 保存
        userInfoApi.save(userInfo);
        return ResponseEntity.ok(null);
    }

    /**
     * 根据token，查找redis获取登陆用户对象json字符串
     * @param token
     * @return
     */
    public User findUserByToken(String token) {
        // 拼接redis中的key
        String key = TOKEN_KEY+token;
        // 根据key获取用户信息的json字符串
        String userJsonString = redisTemplate.opsForValue().get(key);
        // 判断
        if (StringUtils.isEmpty(userJsonString)) {
            return null;
        }
        // string--->user
        User user = JSON.parseObject(userJsonString, User.class);
        return user;
    }

    /**
     * 接口名称：新用户---2选取头像
     * 接口路径：POST/user/loginReginfo/head
     * 需求分析：修改tb_user_info表，设置头像
     */
    public ResponseEntity<Object> updateHead(MultipartFile headPhoto, String token) throws IOException {
        //1. 根据token获取用户
        User user = findUserByToken(token);
        //2. 判断
        if (user == null) {
            return ResponseEntity.status(500).body(ErrorResult.error());
        }

        //3. 先进行人脸检测
        boolean detect = aipFaceTemplate.detect(headPhoto.getBytes());
        if (!detect) {
            return ResponseEntity.status(500).body(ErrorResult.faceError());
        }

        //4. 头像上传到阿里云OSS存储，返回图片地址
        String url =
                ossTemplate.upload(headPhoto.getOriginalFilename(), headPhoto.getInputStream());

        //4. 修改UserInfo
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setAvatar(url);
        userInfoApi.update(userInfo);
        return ResponseEntity.ok(null);
    }

    /**
     * 需求描述：根据token中的用户id，查询用户信息
     * @param userID
     * @param huanxinID
     */
    public ResponseEntity<Object> findById(Long userID, Long huanxinID) {
        //1. 根据token获取用户
        Long userId = UserHolder.getUserId();

        //2. 判断 用户id
        if (userID != null) {
            userId = userID;
        }
        if (huanxinID != null) {
            userId = huanxinID;
        }

        //3. 根据用户id查询
        UserInfo userInfo = userInfoApi.findById(userId);
        //4. 创建vo、封装vo
        UserInfoVo vo = new UserInfoVo();
        // 对象拷贝
        BeanUtils.copyProperties(userInfo,vo);
        // 处理年龄, 要返回字符串
        if (userInfo.getAge() != null) {
            vo.setAge(userInfo.getAge().toString());
        }
        return ResponseEntity.ok(vo);
    }

    /**
     * 需求描述：更新用户信息
     */
    public ResponseEntity<Object> updateUserInfo(UserInfo userInfo) {
        //1. 根据token获取用户
        User user = UserHolder.get();
        //2. 判断
        if (user == null) {
            return ResponseEntity.status(500).body(ErrorResult.error());
        }
        //3. 设置用户id
        userInfo.setId(user.getId());
        //4. 修改
        userInfoApi.update(userInfo);
        return ResponseEntity.ok(null);
    }


    /**
     * 接口名称：互相喜欢，喜欢，粉丝
     * 接口路径：GET/users/counts
     */
    public ResponseEntity<Object> queryCounts() {
        //1. 获取登陆用户
        Long userId = UserHolder.getUserId();
        //2. 调用api服务查询
        //2.1 统计互相关注
        Long eachLoveCount = userLikeApi.queryEachLoveCount(userId);
        //2.2 喜欢
        Long loveCount = userLikeApi.queryLoveCount(userId);
        //2.3 粉丝
        Long fanCount = userLikeApi.queryFanCount(userId);

        //3. 返回数据
        Map<String,Integer> resultMap = new HashMap<>();
        resultMap.put("eachLoveCount",eachLoveCount.intValue());
        resultMap.put("loveCount",loveCount.intValue());
        resultMap.put("fanCount",fanCount.intValue());
        return ResponseEntity.ok(resultMap);
    }

    /**
     * 接口名称：互相喜欢、喜欢、粉丝、谁看过我
     * 路径参数type:
     *   1 互相关注: tanhua_users
     *   2 我关注: user_like
     *   3 粉丝: user_like
     *   4 谁看过我: visitors
     */
    public ResponseEntity<Object> queryUserLikeList(
            Integer page, Integer pagesize, Integer type) {
        // 1. 获取登陆用户id
        Long userId = UserHolder.getUserId();
        // 2. 调用Api服务查询，返回pageResult分页对象，内容放的数据 Map
        PageResult pageResult = null;
        switch (type){
            case 1: // 互相关注: tanhua_users
                pageResult = userLikeApi.queryEachLoveList(userId,page,pagesize);
                break;
            case 2: // 我关注: user_like
                pageResult = userLikeApi.queryUserLikeList(userId,page,pagesize);
                break;
            case 3: // 粉丝: user_like
                pageResult = userLikeApi.queryFansList(userId,page,pagesize);
                break;
            case 4: // 谁看过我: visitors
                pageResult = userLikeApi.queryVisitorsList(userId,page,pagesize);
                break;
        }
        //3. 获取查询的数据
        List<Map<String,Object>> list = (List<Map<String, Object>>) pageResult.getItems();
        // 返回的集合
        List<UserLikeVo> voList = new ArrayList<>();

        //4. 遍历查询的数据，封装返回结果UserLikeVo
        if (list != null && list.size() >0) {
            for (Map<String, Object> map : list) {
                // 获取用户id
                Long uid = (Long) map.get("uid");
                // 获取缘分值
                Long score = (Long) map.get("score");

                // 创建vo对象
                UserLikeVo vo = new UserLikeVo();
                // 根据用户id查询
                UserInfo userInfo = userInfoApi.findById(uid);
                if (userInfo != null) {
                    BeanUtils.copyProperties(userInfo,vo);
                }
                // 设置缘分值
                vo.setMatchRate(score.intValue());
                // vo添加到集合
                voList.add(vo);
            }
        }
        //5. vo集合设置到分页对象中
        pageResult.setItems(voList);
        return ResponseEntity.ok(pageResult);
    }

    @Reference
    private FriendApi friendApi;

    /**
     * 接口名称：粉丝 - 喜欢
     * 接口路径：POST/users/fans/:uid
     */
    public ResponseEntity<Object> fansLike(Long likeUserId) {
        //1. 删除粉丝中的喜欢数据
        userLikeApi.delete(likeUserId,UserHolder.getUserId());
        //2. 记录双向好友关系
        friendApi.save(UserHolder.getUserId(),likeUserId);
        //3. 记录好友关系到环信
        huanXinTemplate.contactUsers(UserHolder.getUserId(),likeUserId);
        return ResponseEntity.ok(null);
    }

    /**
     * 实现流程；
     * 1.通过userApi去搜索所有用户的id，除去自己的id，其余的放入一个中进行返回
     */
    public ResponseEntity<Object> findCardsList(Long userId, Integer page, Integer pagesize) {

        List<Cards> allUserId = cardsApi.findAllUserId(userId, page, pagesize);
        List<CardsUserVo> cardsUserVoList = new ArrayList<>();
        if (allUserId != null && allUserId.size() > 0) {
            for (Cards cards : allUserId) {
                Long userId1 = cards.getUserId();
                UserInfo userInfo = userInfoApi.findById(userId1);
                CardsUserVo cardsUserVo = new CardsUserVo();
                if (userInfo != null) {
                    BeanUtils.copyProperties(userInfo, cardsUserVo);
                    cardsUserVo.setId(userInfo.getId().intValue());
                    if (userInfo.getTags() != null) {
                        String[] strings = userInfo.getTags().split(",");
                        cardsUserVo.setTags(strings);
                    }
                    cardsUserVoList.add(cardsUserVo);
                }
            }
        }

        return ResponseEntity.ok(cardsUserVoList);
    }

    /**
     * 新用户注册后，往所有其他客户的卡片用户表中进行添加一条数据
     */
    public void addAllUserCardsList(Long userId) {
        List<Cards> allUserId = cardsApi.findAllUserId(userId);
        if (allUserId != null) {
            for (Cards cards : allUserId) {
                Long otherUserId = cards.getUserId();
                cardsApi.saveCards(userId, otherUserId);
            }
        }
    }

    /**
     * 接口名称：探花-不喜欢
     * 接口路径：GET/tanhua/:id/unlove
     */
    public ResponseEntity<Object> cardsUnlove(Integer unloveId) {
        Long userId = UserHolder.getUserId();
        Long unloveUserId = unloveId.longValue();
        //1.从当前用户的划卡列表中将这位不喜欢的用户删除；cards_users_userId
        //2.将这个用户加入到本地用户unlove表中；cards_unlove_users_userId
        cardsUnloveApi.cardsUnlove(userId, unloveUserId);
        System.out.println("进入了cardsUnlove method");
        return ResponseEntity.ok(null);
    }

    public ResponseEntity<Object> saveLove(Integer loveUserId) {
        Long userId = UserHolder.getUserId();
        //1.从当前用户的划卡列表中将这位喜欢的用户删除；cards_users_userId
        //2.将这个用户和本地用户一起加入到本地用户喜欢关系表中；user_like
        //3.如何两个人互相喜欢则添加好友关系，
        userLikeApi.saveLove(userId.longValue(), loveUserId);
        //判断是否是好友关系,如果是就注册环信为好友可以聊天
        boolean flag = userLikeApi.isFriend(userId.longValue(),loveUserId.longValue());
        if (flag){
           // huanXinTemplate.contactUsers(userId.longValue(),loveUserId.longValue());
            friendApi.save(userId,loveUserId.longValue());
        }
        return ResponseEntity.ok(null);
    }

    /**
     * 修改手机号 - 2 校验验证码
     */
    public ResponseEntity<Object> checkVerificationCode(String code) {
        //1、获取用户信息
        User user = UserHolder.get();

        //2、从redis中获取验证码
        String key = SMS_KEY + user.getMobile();
        String redisCode = redisTemplate.opsForValue().get(key);

        //3、判断
        Boolean verification = true;
        if (code == null || redisCode == null || !code.equals(redisCode)) {
            //3.1 校验失败
            verification = false;
        } else {
            //3.2 校验成功，从redis中删除验证码
            redisTemplate.delete(key);
        }

        //4、构造返回结果:{"verification":false/true}
        Map<String, Boolean> resultMap = new HashMap<>();
        resultMap.put("verification",verification);
        return ResponseEntity.ok(resultMap);
    }

    /**
     * 修改手机号 - 3 保存
     * @param phone 修改后的手机号
     * @return
     */
    public ResponseEntity<Object> updateUserPhone(String phone) {
        //1、根据修改后的手机号码查询，如果手机号码已存在返回错误信息
        User user = userApi.findByMobile(phone);
        if (user != null) {
            return ResponseEntity.status(500).body(ErrorResult.mobileError());
        }

        //2、获取用户信息、设置修改手机号
        User updateUser = UserHolder.get();
        updateUser.setMobile(phone);

        //3、修改用户
        userApi.update(updateUser);
        return ResponseEntity.ok(null);
    }


    /**
     * 取消-喜欢
     * /users/like/:uid
     */
    public ResponseEntity<Object> UnLike(Long likeUserId) {
        Long userId = UserHolder.getUserId();
        //删除userike表的数据
        userLikeApi.delete(userId,likeUserId);
        //往（Friend）互相喜欢表删除数据
        friendApi.delete(userId,likeUserId);

        return ResponseEntity.ok(null);
    }





    //根据用户的id集合查询用户信息
    public List<UserInfo> queryUserInfoByUserIdList(List<Object> userIds) {

        return userInfoApi.selectList(userIds);
    }
}
