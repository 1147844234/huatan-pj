package com.tanhua.domain.db;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测灵魂 -  问卷表（初（2星），中（3星），高（5星））
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_soulreport")
public class SoulReport extends BasePojo{
    private Long id;
    private Long userid;  //标记是哪个用户的填写的问卷所产生的报告
    private Long paperid; //标记报告对应的是初中高三种问卷中的哪种
    private Long score;  //你答完一张问卷后的总得分
}