package com.tanhua.server.service;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.commons.template.OssTemplate;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.PeachBlossom;
import com.tanhua.domain.mongo.RemainingTimes;
import com.tanhua.domain.vo.PeachBlossomVo;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.mongo.PeachBlossomApi;
import com.tanhua.server.interceptor.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class PeachBlossomService {


    @Reference
    private PeachBlossomApi peachBlossomApi;
    @Reference
    private UserInfoApi userInfoApi;
    @Autowired
    private FastFileStorageClient fileStorageClient;
    @Autowired
    private FdfsWebServer fdfsWebServer;
    @Autowired
    private OssTemplate ossTemplate;


    /**
     * 每天晚上12点(24点),刷新接收次数
     * cron:秒、分、时、日、月、周
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void Refresh() {
        log.info("刷新接收次数-------------------------->");
        peachBlossomApi.RefreshRimes();
    }


    /**
     * 桃花传音-发送语音
     */
    public ResponseEntity<Object> upload(MultipartFile soundFile) throws IOException {
        //获取用户id
        Long userId = UserHolder.getUserId();
        //获取语音名字
        String soundName = soundFile.getOriginalFilename();
        //上传语音到Fdfs
        StorePath storePath = fileStorageClient.uploadFile(soundFile.getInputStream(),
                soundFile.getSize(),
                soundName.substring(soundName.lastIndexOf(".") + 1), null);
        //拼接视频完整地址
        String soundUrl = fdfsWebServer.getWebServerUrl() + storePath.getFullPath();

        PeachBlossom peachBlossom = new PeachBlossom();
        peachBlossom.setUserId(userId);
        peachBlossom.setSoundUrl(soundUrl);
        peachBlossom.setState(0);

        //保存数据
        peachBlossomApi.save(peachBlossom);

        //语音上传成功
        if (storePath.getFullPath() != null) {
            //当前登录用户发送次数加1
            peachBlossomApi.sendPlusOne(userId);
        }
        //如果当前登录用户发布3条语音会获得接收次数+1
        Integer count = peachBlossomApi.findBySend(userId);
        if (count != 0 && count % 3 == 0) {
            peachBlossomApi.rimesPlusOne(userId, 1);
        }

        return ResponseEntity.ok(null);
     /*   String Url = ossTemplate.upload(
                soundFile.getOriginalFilename(), soundFile.getInputStream());
        System.out.println("地址："+Url);
        return ResponseEntity.ok(null);*/
    }


    /**
     * 桃花传音-接收语音
     */
    public ResponseEntity<Object> download() {
        //获取当前登录用户id
        Long userId = UserHolder.getUserId();
        //查询所有状态未读的语音,除了当前用户
        List<PeachBlossom> all = peachBlossomApi.findAll(userId);
        //判断语音不为空
        PeachBlossom peachBlossom = null;
        //定义vo
        PeachBlossomVo vo = new PeachBlossomVo();
        if (all != null && all.size() > 0) {
            //随机一个语音给当前用户
            int index = new Random().nextInt(all.size());
            System.out.println("随机：" + index);
            peachBlossom = all.get(index);
            if (peachBlossom != null) {
                //封装SoundUrl
                vo.setSoundUrl(peachBlossom.getSoundUrl());
                //查询用户剩余次数
                RemainingTimes times = peachBlossomApi.findRemainingTimes(userId);
                if (times != null) {
                    vo.setRemainingTimes(times.getRimes());
                }
            }
            UserInfo userInfo = userInfoApi.findById(peachBlossom.getUserId());
            if (userInfo != null) {
                BeanUtils.copyProperties(userInfo, vo);
            }
            vo.setId(userInfo.getId().intValue());
        }
        //修改state
        peachBlossomApi.update(peachBlossom.getId(), 1);
        //当前用户次数减1
        peachBlossomApi.timeMinusOne(userId);
        //返回数据
        return ResponseEntity.ok(vo);
    }


}
