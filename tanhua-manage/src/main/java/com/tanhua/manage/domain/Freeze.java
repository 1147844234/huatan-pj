package com.tanhua.manage.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Freeze {
    private Long id;
    /**
     * 被冻结的用户id
     */
    private Long userId;
    /**
     * 冻结时间 1:3天   2:7天   3:永久
     */
    private Integer freezingTime;
    /**
     * 冻结范围 1:冻结登录   2:冻结发言   3:冻结发布动态
     */
    private Integer freezingRange;
    /**
     * 冻结原因
     */
    private String reasonsForFreezing;
    /**
     * 冻结备注
     */
    private String frozenRemarks;

    /**
     * 新建冻结表
     * CREATE TABLE tb_freeze (
     * 	id BIGINT(20) NOT NULL AUTO_INCREMENT,
     * 	user_id BIGINT(20) NOT NULL,
     * 	freezing_time INT(1) NOT NULL,
     * 	freezing_range INT(1) NOT NULL,
     * 	reasons_for_freezing VARCHAR(200) NOT NULL,
     * 	frozen_remarks VARCHAR(200) NOT NULL,
     * 	PRIMARY KEY (`id`)
     * )
     */

}
