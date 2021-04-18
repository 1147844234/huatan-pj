package com.tanhua.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测灵魂--查看解果
 * 返回的就是一些理智-%80  类似这种
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SoulDimensions {
    //维度项（外向，判断，抽象，理性）
    private String key;
    //维度值
    private String value;
}
