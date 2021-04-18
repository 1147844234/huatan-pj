package com.tanhua.dubbo.api.mongo;
import com.tanhua.domain.mongo.Cards;

import java.util.List;

public interface CardsApi {

    void creatCardsList(Long userId);

    List<Cards> findAllUserId(Long userId,Integer page,Integer pagesize);

    List<Cards> findAllUserId(Long userId);

    void saveCards(Long userId, Long otherUserId);
}
