package com.tanhua.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 测灵魂-查看结果
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ConclusionVo {
    //鉴定结果
    private String conclusion; //结论 后面用枚举注入值
    //鉴定图片
    private String cover; //封面  也用枚举注入值
    //维度
    //就是理智对应%80  要返回几个对应的键值
    private List<?> dimensions= Collections.EMPTY_LIST;
    //与你相似 分值差5分左右的用户
    private List<?> similarYou= Collections.EMPTY_LIST;
}