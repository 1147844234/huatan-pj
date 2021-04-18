package com.tanhua.domain.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * 试题表 :问卷列表返回值
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("testSoul_paper")
public class TestSoulPaper implements java.io.Serializable {
    /**
     * 试题id 主键列
     */
    @Id
    private ObjectId objectId;
    /**
     * 问卷编号
     */
    private String id;
    /**
     * 试题问卷名称
     */
    private String name;
    /**
     * 封面
     */
    private String cover;
    /**
     * 级别 :初级,中级,高级
     */
    private String level;
    /**
     * 星别（例如：2颗星，3颗星，5颗星）
     */
    private Integer star;
    /**
     * 试题
     */
    private List<TestSoulQuestion> questions;
    /**
     * 是否锁住 0:解锁  1:锁住
     */
    private Integer isLock;
    /**
     * 最新报告id
     */
    private String reportId;
}
