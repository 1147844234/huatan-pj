package com.tanhua.domain.mongo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "cards_users")
public class Cards implements Serializable {
    private static final long serialVersionUID = 1453386494548147794L;
    private ObjectId id; //主键id
    private Long userId; //发布id
    private Long created; //发布时间
}
