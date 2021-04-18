package com.tanhua.domain.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * 问题表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("testSoul_question")
public class TestSoulQuestion implements java.io.Serializable{

    /**
     * 问题主键id
     */
    private ObjectId id;
    /**
     * 问题所对应的试题id
     */
    private ObjectId paperId;
    /**
     * 问题的内容
     */
    private String text;
    /**
     * 问题的难度 初级,中级,高级
     */
    private String level;
    /**
     * 该问题所对应的全部选项
     */
    private List<TestSoulOption> options;
}
