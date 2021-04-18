package com.tanhua.domain.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeachBlossomVo {
    //桃花传音vo
    private Integer id; //用户id
    private String avatar; //用户头像
    private String nickname; //昵称
    private String gender; //性别
    private Integer age; //年龄
    private String soundUrl;       //语音链接
    private Integer remainingTimes; //剩余次数
}
