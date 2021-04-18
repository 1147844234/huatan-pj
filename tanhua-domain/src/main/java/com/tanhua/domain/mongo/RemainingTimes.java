package com.tanhua.domain.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "remainingTimes")
public class RemainingTimes implements Serializable {
    //剩余次数表
    private ObjectId id;
    private Long userId;  //用户
    private Integer rimes; //剩余次数
    private Integer send;  //语音发送次数
}
