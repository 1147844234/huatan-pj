package com.tanhua.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 测灵魂 -  问卷列表
 * 之返回的问卷vo中每个问卷含有十个题 这十个题用QuestionsVo存储（跟接口文档要求返回的字段一致）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionsVo {
    private String id;//试题编号
    private String question;//题目
    private List<?> options= Collections.EMPTY_LIST;//选项

}