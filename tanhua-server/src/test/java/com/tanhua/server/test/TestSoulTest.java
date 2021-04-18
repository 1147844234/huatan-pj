package com.tanhua.server.test;

import com.tanhua.domain.mongo.TestSoulOption;
import com.tanhua.domain.mongo.TestSoulPaper;
import com.tanhua.domain.mongo.TestSoulQuestion;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSoulTest {
    @Autowired
    private MongoTemplate mongoTemplate;


    @Test
    public void addTest(){
        //新增试卷表
        TestSoulPaper paper = new TestSoulPaper();
        paper.setObjectId(ObjectId.get());
        paper.setId(String.valueOf(paper.getObjectId()));
        paper.setName("初级灵魂题");
        paper.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/qn_cover_01.png");
        paper.setLevel("初级");
        paper.setStar(2);
        paper.setQuestions(null);
        paper.setIsLock(0);
        paper.setReportId(null);
        mongoTemplate.save(paper);
    }
    @Test
    public void addTest2(){
        //新增问题表
        TestSoulQuestion question = new TestSoulQuestion();
        question.setId(ObjectId.get());
        question.setPaperId(new ObjectId("60755aec8b2755421469cc8b"));
        question.setText("你何时感觉最好？");
        question.setLevel("初级");
        question.setOptions(null);
        mongoTemplate.save(question);
    }

    @Test
    public void addTest3(){
        //新增问题选项表
        TestSoulOption option = new TestSoulOption();
        option.setId(ObjectId.get());
        option.setQuestionId(new ObjectId("60755d128b2755562011b505"));
        option.setOpNumber("A");
        option.setText("早晨");
        option.setScore(2);
        mongoTemplate.save(option);

    }
}
