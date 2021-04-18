package com.tanhua.domain.db;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *测灵魂-选项表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_souloptions")
public class SoulOptions extends BasePojo {

    private Long id;  //自增
    private Long questionid; //题目id
    private String options;//选项
    private Long score; //分数

}