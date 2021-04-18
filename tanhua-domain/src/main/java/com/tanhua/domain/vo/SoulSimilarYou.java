package com.tanhua.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测灵魂-查看结果
 * 封装相似用户id 和头像
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SoulSimilarYou {
    //用户编号
    private Integer id;
    //头像
    private String avatar;
}