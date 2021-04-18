package com.tanhua.dubbo.api;

import com.tanhua.domain.db.SoulPaperQuestion;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface SoulPaperQuestionApi {



    List<SoulPaperQuestion> queryPaperQuestionList(int paperid);

    SoulPaperQuestion selectOne(Long questionid);
}
