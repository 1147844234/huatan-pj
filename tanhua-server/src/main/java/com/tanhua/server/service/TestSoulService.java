package com.tanhua.server.service;



import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.tanhua.domain.db.*;
import com.tanhua.domain.enums.ConclusionEnum;
import com.tanhua.domain.enums.CoverEnum;
import com.tanhua.domain.vo.*;
import com.tanhua.dubbo.api.*;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


import cn.hutool.core.util.ObjectUtil;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * tb_souloptions   用来设置每个题目的选项（题库插入）
 * <p>
 * tb_soulpaper     用来设置题目的类型（固定）
 * <p>
 * tb_soulpaperquestion   组成试卷表
 * <p>
 * tb_soulquestion   题目表（题库插入）
 * <p>
 * tb_soulreport   报告表
 */
@Service
@Slf4j
@SuppressWarnings("ALL")
public class TestSoulService {

    //操作问卷
    @Reference
    private SoulPaperApi soulPaperApi;
    //操作试题
    @Reference
    private SoulQuestionApi soulQuestionApi;
    //操作试题编号表 该表上对应初中高问卷 下对应每个试题的内容和等级  做连接作用
    @Reference
    private SoulPaperQuestionApi soulPaperQuestionApi;
    //操作所有选项
    @Reference
    private SoulOptionsApi soulOptionsApi;
    //报告
    @Reference
    private SoulReportApi soulReportApi;
    //用来查询类型相关用户
    @Autowired
    private UserService userService;





    /**
     * 测灵魂-问卷列表
     */
    public List<PaperListVo> queryPaperList() {
        //1.获取登录用户
        User user = UserHolder.get();

        //构造返回的页面对象(前端返回页面为初级（2星） 中级（3星） 高级（5星)
        PaperListVo paperListVo1 = new PaperListVo();
        PaperListVo paperListVo2 = new PaperListVo();
        PaperListVo paperListVo3 = new PaperListVo();

        //查找报告（根据登录用户，查询这个用户答完问卷所产生的报告，第一次肯定是空的）
        List<SoulReport> soulReports = soulReportApi.queryReportList(user.getId());

        //该用户答问卷所产生的报告（0-3个）
        int size = soulReports.size();

        //切换到公共方法进行封装返回的vo数据 因为问卷总共就3份  所以直接用id 1 2 3 封装到vo即可
        postVo(paperListVo1, 1);
        postVo(paperListVo2, 2);
        postVo(paperListVo3, 3);

        //判断报告id是否为空,肯定是初级的
        if (size == 0) {
            //解除锁住
            paperListVo1.setIsLock(0);
            paperListVo2.setIsLock(1);
            paperListVo3.setIsLock(1);
            //此时没有报告不需要设置报告id

        } else if (soulReports.size() == 1) {
            //解除锁住
            paperListVo1.setIsLock(0);
            paperListVo2.setIsLock(0);
            paperListVo3.setIsLock(1);
            //设置报告id 此时有了初级的报告 遍历该用户的报告集
            for (SoulReport soulReport : soulReports) {
                //将该用户的初级报告拿出id给赋值给需要返回的问卷vo的reportId
                if (soulReport.getPaperid() == 1) {
                    paperListVo1.setReportId(soulReport.getId().toString());
                }
                paperListVo2.setReportId(null);
                paperListVo3.setReportId(null);

            }
            //soulReports.forEach(soulReport -> paperListVo2.setReportId(soulReport.getId().toString()));
        } else if (soulReports.size() == 2) {
            //解除锁住 因为已经有两张报告了 说明全部解锁
            paperListVo1.setIsLock(0);
            paperListVo2.setIsLock(0);
            paperListVo3.setIsLock(0);
            //设置报告id
            for (SoulReport soulReport : soulReports) {
                //将该用户的初级报告拿出id给赋值给需要返回的问卷vo的reportId 用于前端查看报告可以根据id就可以查
                if (soulReport.getPaperid() == 1) {
                    paperListVo1.setReportId(soulReport.getId().toString());
                }if (soulReport.getPaperid() == 2) { //将该用户的中级报告拿出id给赋值给需要返回的问卷vo的reportId 用于前端查看报告可以根据id就可以查
                    paperListVo2.setReportId(soulReport.getId().toString());
                }
                paperListVo3.setReportId(null);
            }
            //soulReports.forEach(soulReport -> paperListVo3.setReportId(soulReport.getId().toString()));
        } else if (soulReports.size() == 3) {
            //解除锁住
            paperListVo1.setIsLock(0);
            paperListVo2.setIsLock(0);
            paperListVo3.setIsLock(0);
            //设置报告id 全部答完了返回的初中高三张问卷都可以查看报告  需要设置好三张问卷的报告id
            for (SoulReport soulReport : soulReports) {
                if (soulReport.getPaperid() == 1) {
                    paperListVo1.setReportId(soulReport.getId().toString());
                }if (soulReport.getPaperid() == 2) {
                    paperListVo2.setReportId(soulReport.getId().toString());
                }if (soulReport.getPaperid() == 3) {
                    paperListVo3.setReportId(soulReport.getId().toString());
                }
            }
        }




        //返回集合对象
        List<PaperListVo> list = new ArrayList<>();
        list.add(paperListVo1);
        list.add(paperListVo2);
        list.add(paperListVo3);

        return list;
    }


    /**
     * 提交问卷
     *
     * @param map
     * @return
     */
    public String submitTestPaper(Map<String, List<Answers>> map) {
        //获取登录用户
        User user = UserHolder.get();

        //计算得分 根据返回的每道试题的用户选的选项计算分值的存储变量
        Long score = 0L;
        Long questionid = 0L;
        //遍历获得对象

        Collection<List<Answers>> AnswersList = map.values();
        //脱掉外面的Collection外衣
        for (List<Answers> answers : AnswersList) {
            //试题ID
            for (Answers answer : answers) {
                questionid = Long.valueOf(answer.getQuestionId());
                //获得选项的id
                String option = answer.getOptionId();

                //计算得分 根据题目的id和选项id找到选项 （两个id查询比较保险。。。。）
                SoulOptions soulOptions1 = soulOptionsApi.selectOne(questionid,option);
                Long score1 = soulOptions1.getScore();
                //将查询到的分值累加
                score += score1;
            }
        }

        //根据试题id就可以查到试题编号表  编号表就可以查问卷表  起到连接作用
        SoulPaperQuestion soulPaperQuestion = soulPaperQuestionApi.selectOne(questionid);
        //这个paperid就知道用户现在是测的初中高的哪一张表
        Long paperid = soulPaperQuestion.getPaperid();
        System.out.println("该用户做的试卷是:" + paperid);
        System.out.println("该用户得分为:" + score);
        System.out.println("当前用户为:" + user.getId());

        //这个用户能否查到报告ID,有ID的话说明要重新测试需要更新得分（也就是用户吃太饱要重新测一次）
        SoulReport result = soulReportApi.queryReport(user.getId(),paperid);

        if (ObjectUtil.isNotEmpty(result)) {
            //不为空就更新得分,最后将ID返回

            SoulReport soulReport = new SoulReport();
            soulReport.setScore(score);
            soulReport.setPaperid(paperid); //根据userid和paperid可以定位到一张报告
            soulReport.setUpdated(new Date(System.currentTimeMillis()));
            soulReportApi.updateReport(soulReport, UserHolder.getUserId());


            //查询到那条根据的报告 返回它的报告id
            SoulReport soulReport1 = soulReportApi.queryReport(user.getId(),paperid);
            System.out.println("当前结果的ID为:" + soulReport1.getId());
            return soulReport1.getId().toString();
        }
        //如果查不到那就插入到数据库  说明是新测试 新报告
        //封装插入的报告数据  核心是userid paperid score分值
        SoulReport soulReport = new SoulReport();
        soulReport.setUserid(UserHolder.get().getId());
        soulReport.setPaperid(paperid);
        soulReport.setScore(score);
        soulReport.setCreated(new Date(System.currentTimeMillis()));
        soulReport.setUpdated(new Date(System.currentTimeMillis()));
        soulReportApi.insert(soulReport);

        //查询到那条根据的报告 返回它的报告id
        SoulReport soulReport1 = soulReportApi.queryReport(user.getId(),paperid);
        return soulReport1.getId().toString();
    }

    /**
     * 用于封装问卷列表的数据  测灵魂-问卷列表的抽取方法
     */
    private void postVo(PaperListVo paperListVo, int i) {
        //设置问卷编号
        paperListVo.setId(String.valueOf(i));
        //设置问卷名称  根据问卷id查找问卷获取问卷名字 如：初级灵魂题...
        paperListVo.setName(soulPaperApi.selectById(i).getName());
        //设置封面  根据问卷id查找问卷获取封面 是一个url
        paperListVo.setCover(soulPaperApi.selectById(i).getCover());
        //设置级别  level 初级 中级  高级
        paperListVo.setLevel(soulPaperApi.selectById(i).getLevel());
        //设置星别  2星 3星 5星
        paperListVo.setStar(Convert.toInt(soulPaperApi.selectById(i).getStar()));


        //根据paperid查找试题  每一个问卷表有十道题 根据paperid就可以查到对应的十个题目也就是SoulPaperQuestion
        List<SoulPaperQuestion> soulPaperQuestions = soulPaperQuestionApi.queryPaperQuestionList(i);

        //返回的vo中包含着quetionVo就是十道题 每道题含有optionVo每道题的选项
        List<QuestionsVo> questionsVoList = new ArrayList<>();

        //遍历10道题目
        for (SoulPaperQuestion soulPaperQuestion : soulPaperQuestions) {
            //把题目加入QuestionsVo对象
            QuestionsVo questionsVo = new QuestionsVo();
            //questionVo的id其实就是试题的题目id
            questionsVo.setId(soulPaperQuestion.getQuestionid().toString());
            //根据试题编号表的试题编号就可以查询试题表中的试题
            questionsVo.setQuestion(soulQuestionApi.selectById(soulPaperQuestion.getQuestionid()).getQuestion());


            //得到选项的集合 根据是试题编号找到这道题对应的选项
            List<SoulOptions> soulOptionsList = soulOptionsApi.queryOptions(soulPaperQuestion.getQuestionid());


            //创建要返回的问卷vo中的试题vo中的选项vo
            List<OptionsVo> optionsVolist = new ArrayList<>();

            //得到optionsVolist集合
            for (SoulOptions soulOptions : soulOptionsList) {
                OptionsVo optionsVo = new OptionsVo();
                //选项的id
                optionsVo.setId(soulOptions.getId().toString());
                //选项的内容
                optionsVo.setOption(soulOptions.getOptions());
                //选项添加进一个题目的选项集合中
                optionsVolist.add(optionsVo);
            }
            //选项集合放进题目中
            questionsVo.setOptions(optionsVolist);
            //题目集合收录每一道题
            questionsVoList.add(questionsVo);
        }
        //再把收录好选项的十道题的集合放进要返回的问卷中
        paperListVo.setQuestions(questionsVoList);
    }

    /**
     * 查看结果
     */
    public ConclusionVo getReport(String id) {
        //创建ConclusionVo对象
        ConclusionVo conclusionVo = new ConclusionVo();

        //先根据报告id获取report表中的数据
        SoulReport soulReport = soulReportApi.queryReportById(id);

        //如果report表中没有数据,返回null
        if (null == soulReport) {
            return null;
        }

        //从report表中获取用户的得分
        Long score = soulReport.getScore();

        //再到report表中查询分数接近的用户  下面这个方法就是根据分值少五分多分查找问卷 但是不包括自己的问卷 而且是相同级别的问卷
        //级别通过paperid初中高三种问卷类型
        List<SoulReport> soulReportList = soulReportApi.querySimiliar(score,soulReport.getUserid(),soulReport.getPaperid());

        System.out.println(soulReportList+"=================================");

        //获取这些分数接近的用户的id  这个方法就是获取问卷中所有的userid集合
        List<Object> userIdList = CollUtil.getFieldValues(soulReportList, "userid");

        //获取userinfo对象的集合
        List<UserInfo> userInfoList = new ArrayList<>();
        if (CollUtil.isNotEmpty(userIdList)) {
            //下面这个方式其实就是根据相似用户id集合查询所有的用户信息
            userInfoList = userService.queryUserInfoByUserIdList(userIdList);
        }
        //遍历userinfo集合,创建soulsimilar对象,并创建集合
        List<SoulSimilarYou> similarYouList = new ArrayList<>();
        if (null != userInfoList) {
            for (UserInfo userInfo : userInfoList) {
                //遍历这些相同的用户  获取他们的id和头像封装到要返回的soulSimilarYou集合
                SoulSimilarYou soulSimilarYou = new SoulSimilarYou();
                soulSimilarYou.setAvatar(userInfo.getAvatar());
                soulSimilarYou.setId(Convert.toInt(userInfo.getId()));
                similarYouList.add(soulSimilarYou);
            }
        }
        if (21 > score) {
            //小于21分,鉴定结果为理性
            //ConclusionEnum结论枚举类  有四种结论比较固定所以作为枚举
            //CoverEnum每种类型有对应的封面  固定所以作为枚举
            conclusionVo.setConclusion(ConclusionEnum.MAOTOUTING.getValue());
            conclusionVo.setCover(CoverEnum.MAOTTOUYING.getValue());
            List<SoulDimensions> dimensionsList = new ArrayList<>();
            //我们不是大数据团队更不是心理学家 做不了这些分析  写死返回
            SoulDimensions s1 = new SoulDimensions("外向", "60%");
            SoulDimensions s2 = new SoulDimensions("判断", "80%");
            SoulDimensions s3 = new SoulDimensions("抽象", "70%");
            SoulDimensions s4 = new SoulDimensions("理性", "90%");
            dimensionsList.add(s1);
            dimensionsList.add(s2);
            dimensionsList.add(s3);
            dimensionsList.add(s4);
            conclusionVo.setDimensions(dimensionsList);
            conclusionVo.setSimilarYou(similarYouList);
        } else if (21 <= score && score <= 40) {
            //21-40分,鉴定结果为判断类型
            conclusionVo.setConclusion(ConclusionEnum.BAITU.getValue());
            conclusionVo.setCover(CoverEnum.BAITU.getValue());

            List<SoulDimensions> dimensionsList = new ArrayList<>();
            SoulDimensions s1 = new SoulDimensions("外向", "80%");
            SoulDimensions s2 = new SoulDimensions("判断", "60%");
            SoulDimensions s3 = new SoulDimensions("抽象", "70%");
            SoulDimensions s4 = new SoulDimensions("理性", "90%");
            dimensionsList.add(s1);
            dimensionsList.add(s2);
            dimensionsList.add(s3);
            dimensionsList.add(s4);
            conclusionVo.setDimensions(dimensionsList);

            //conclusionVo.setDimensions(Convert.toList(new SoulDimensions("判断", "70%")));
            conclusionVo.setSimilarYou(similarYouList);
        } else if (41 <= score && score <= 55) {
            //41-55分,鉴定结果为抽象类型
            conclusionVo.setConclusion(ConclusionEnum.HULI.getValue());
            conclusionVo.setCover(CoverEnum.HULI.getValue());
            List<SoulDimensions> dimensionsList = new ArrayList<>();
            SoulDimensions s1 = new SoulDimensions("外向", "80%");
            SoulDimensions s2 = new SoulDimensions("判断", "70%");
            SoulDimensions s3 = new SoulDimensions("抽象", "60%");
            SoulDimensions s4 = new SoulDimensions("理性", "90%");
            dimensionsList.add(s1);
            dimensionsList.add(s2);
            dimensionsList.add(s3);
            dimensionsList.add(s4);
            conclusionVo.setDimensions(dimensionsList);
            //conclusionVo.setDimensions(Convert.toList(new SoulDimensions("抽象", "90%")));
            conclusionVo.setSimilarYou(similarYouList);
        } else if (score > 56) {
            //41-55分,鉴定结果为外向类型
            conclusionVo.setConclusion(ConclusionEnum.SHIZI.getValue());
            conclusionVo.setCover(CoverEnum.SHIZI.getValue());
            List<SoulDimensions> dimensionsList = new ArrayList<>();
            SoulDimensions s1 = new SoulDimensions("外向", "90%");
            SoulDimensions s2 = new SoulDimensions("判断", "70%");
            SoulDimensions s3 = new SoulDimensions("抽象", "80%");
            SoulDimensions s4 = new SoulDimensions("理性", "60%");
            dimensionsList.add(s1);
            dimensionsList.add(s2);
            dimensionsList.add(s3);
            dimensionsList.add(s4);
            conclusionVo.setDimensions(dimensionsList);
            //conclusionVo.setDimensions(Convert.toList(new SoulDimensions("理性", "60%")));
            conclusionVo.setSimilarYou(similarYouList);
        }
        //ConclusionVo填充完毕,进行返回
        return conclusionVo;
    }




}
