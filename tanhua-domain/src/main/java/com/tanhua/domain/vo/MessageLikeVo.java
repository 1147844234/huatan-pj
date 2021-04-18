package com.tanhua.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageLikeVo {
    //点赞 评论 喜欢 列表
    private String id;
    private String avatar;
    private String nickname;
    private String createDate;
}