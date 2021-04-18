package com.tanhua.domain.mongo;

import com.tanhua.domain.db.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

/**
 * 灵魂测试的结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("testSoul_conclusion")
public class TestSoulConclusion implements java.io.Serializable{
    /**
     * 测试结果的主键id
     */
    private ObjectId id;
    /**
     * 测试用户的id
     */
    private Long userId;
    /**
     * 用户性别
     */
    private String gender;
    /**
     * 所做试题的等级
     */
    private String level;
    /**
     * 试题的类型
     */
    private Integer type;
    /**
     * 鉴定结果	,用户人格类型: 猫头鹰.........
     */
    private String conclusion;
    /**
     * 鉴定图片地址
     */
    private String cover;
    /**
     * 维度
     * key-->维度项（外向，判断，抽象，理性）
     * value-->维度值:80%,70%,90%,60%
     * "dimensions": [
     *         {
     *             "key": "外向",
     *             "value": "97.3%"
     *         },
     *         {
     *             "key": "判断",
     *             "value": "78.58%"
     *         },
     *         {
     *             "key": "抽象",
     *             "value": "77.81%"
     *         },
     *         {
     *             "key": "理性",
     *             "value": "79.96%"
     *         }]
     */
    private List<Map<String,String>> dimensions;
    /**
     * 与你相似
     */
    private List<UserInfo> similarYou;
    /**
     * 创建时间
     */
    private Long created;

}
