package com.tanhua.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测灵魂 封装一道题对应的选项集合
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionsVo {
    private String id;
    private String option;
}