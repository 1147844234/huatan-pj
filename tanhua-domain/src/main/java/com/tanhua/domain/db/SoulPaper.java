package com.tanhua.domain.db;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测灵魂-问卷表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_soulpaper")
public class SoulPaper extends BasePojo{
    private Long id; //问卷编号 1 ， 2， 3
    private String name; //问卷名称
    private String cover;//封面
    private String level;//级别 初 ， 中， 高
    private Long star;//星别 2， 3， 5
}