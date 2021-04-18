package com.tanhua.domain.db;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测灵魂-试题表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_soulquestion")
public class SoulQuestion extends BasePojo{
    private Long id;
    private String question; //试题内容
    private String type; //试题类型 初 中 高
}