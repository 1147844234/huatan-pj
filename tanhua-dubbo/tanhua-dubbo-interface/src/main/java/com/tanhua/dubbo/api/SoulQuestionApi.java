package com.tanhua.dubbo.api;

import com.tanhua.domain.db.SoulQuestion;

public interface SoulQuestionApi {


    SoulQuestion selectById(Long questionid);
}
