package com.tanhua.domain.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 选项表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("testSoul_option")
public class TestSoulOption implements java.io.Serializable{
    /**
     * 选项主键id
     */
    private ObjectId id;
    /**
     * 选项对应问题的主键id
     */
    private ObjectId questionId;
    /**
     * 选项号: ABCDEFG...
     */
    private String opNumber;
    /**
     * 选项内容
     */
    private String text;
    /**
     * 该选项的分数
     */
    private Integer score;
}
