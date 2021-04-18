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
@Document(collection = "tanhua_peachblossom")
public class PeachBlossom implements Serializable {
        //桃花传音
        private ObjectId id;           //mongo主键
        private Long userId;           //发语音人
        private String soundUrl;       //语音链接
        private Integer state = 0;     //语音状态 0：未读，1：已读
}
