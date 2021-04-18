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
@Document(collection = "cards_unlove_users")
public class CardsUnlove implements Serializable {
    private static final long serialVersionUID = -3367143741511960869L;
    private ObjectId id; //主键id
    private Long userId; //发布id
    private Long created; //发布时间

}