package com.tanhua.manage.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("tb_unfreeze")
public class UnFreeze {
    private Long id;
    /**
     * 被冻结的用户id
     */
    private Long userId;

    /**
     * 解冻原因
     */
    private String reasonsForThawing;
    /**
     * 解冻备注
     */
    private String frozenRemarks;

    /**
     * 新建解冻表
     * CREATE TABLE tb_unfreeze (
     * 	id BIGINT(20) NOT NULL AUTO_INCREMENT,
     * 	user_id BIGINT(20) NOT NULL,
     * 	reasons_for_thawing VARCHAR(200) NOT NULL,
     * 	frozen_remarks VARCHAR(200) NOT NULL,
     * 	PRIMARY KEY (`id`)
     * )
     */
}
