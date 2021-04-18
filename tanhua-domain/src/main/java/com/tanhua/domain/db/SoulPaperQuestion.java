package com.tanhua.domain.db;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 测灵魂-试题编号表 连接试题表和问卷表  作连接作用
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_soulpaperquestion")
public class SoulPaperQuestion implements Serializable {

    private Long id;
    private Long paperid;  //问卷id  表明你这个试题是属于初中高哪一级别
    private Long questionid; //试题id  可以查询试题表
}
