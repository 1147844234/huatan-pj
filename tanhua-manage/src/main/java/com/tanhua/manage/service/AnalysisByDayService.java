package com.tanhua.manage.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tanhua.manage.domain.AnalysisByDay;
import com.tanhua.manage.mapper.AnalysisByDayMapper;
import com.tanhua.manage.mapper.LogMapper;
import com.tanhua.manage.utils.ComputeUtil;
import com.tanhua.manage.vo.AnalysisSummaryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AnalysisByDayService extends ServiceImpl<AnalysisByDayMapper,AnalysisByDay> {

    @Autowired
    private AnalysisByDayMapper analysisByDayMapper;
    @Autowired
    private LogMapper logMapper;

    /**
     * 接口名称：概要统计信息
     */
    public ResponseEntity<Object> summary() {
        // 1. 查询累计用户数：SELECT SUM(num_registered) num_registered FROM tb_analysis_by_day
        AnalysisByDay analysisByDay = query().select("SUM(num_registered) num_registered").one();
        // 1.1 获取统计的累计用户
        Integer totalRegistered = analysisByDay.getNumRegistered();

        // 2. 查询今日新增、登陆次数、活跃用户
        // SELECT num_registered,num_active,num_login FROM tb_analysis_by_day WHERE record_date = '2021-04-08'
        Date now = new Date();
        String today = new SimpleDateFormat("yyyy-MM-dd").format(now);
        // 2.1 根据日期查询今日新增、登陆次数、活跃用户
        AnalysisByDay todayAnalysis = query().eq("record_date", today).one();
        // 2.2 获取今日新增、登陆次数、活跃用户
        Integer todayRegistered = todayAnalysis.getNumRegistered();
        Integer todayActive = todayAnalysis.getNumActive();
        Integer todayNumLogin = todayAnalysis.getNumLogin();

        // 3. 查询昨日新增、登陆次数、活跃用户； 主要是了计算环比
        // 3.1 获取昨天的日期字符串
        String yes = ComputeUtil.offsetDay(now, -1);
        // 3.2 查询昨日新增、登陆次数、活跃用户；
        AnalysisByDay yesAnalysis = query().eq("record_date", yes).one();
        Integer yesRegistered = yesAnalysis.getNumRegistered();
        Integer yesActive = yesAnalysis.getNumActive();
        Integer yesNumLogin = yesAnalysis.getNumLogin();

        // 4. 查询过去7天活跃以及过去30天活跃
        // SELECT SUM(num_active) num_active FROM tb_analysis_by_day WHERE record_date BETWEEN '2021-04-01' AND '2021-04-08'
        Long day7 = analysisByDayMapper.findNumActiveByDate(ComputeUtil.offsetDay(now,-7),today);
        Long day30 = analysisByDayMapper.findNumActiveByDate(ComputeUtil.offsetDay(now,-30),today);

        // 创建并封装返回的vo对象
        AnalysisSummaryVo vo = new AnalysisSummaryVo();
        // 累计用户数
        vo.setCumulativeUsers(totalRegistered.longValue());
        vo.setActivePassWeek(day7);
        vo.setActivePassMonth(day30);
        // 今日新增
        vo.setNewUsersToday(todayRegistered.longValue());
        vo.setNewUsersTodayRate(ComputeUtil.computeRate(todayRegistered,yesRegistered));
        // 登陆次数
        vo.setLoginTimesToday(todayNumLogin.longValue());
        vo.setLoginTimesTodayRate(ComputeUtil.computeRate(todayNumLogin,yesNumLogin));
        // 活跃用户
        vo.setActiveUsersToday(todayActive.longValue());
        vo.setActiveUsersTodayRate(ComputeUtil.computeRate(todayActive,yesActive));

        return ResponseEntity.ok(vo);
    }

    /**
     * 读取统计日志表数据更新到日表中 (每天的第一次执行是插入)
     */
    public void analysis() {

        //1. 创建当前日期对象
        Date date = new Date();
        // 获取当前日期字符串： 默认格式yyyy-MM-dd
        String now = DateUtil.formatDate(date);

        //2. 根据当前日期，先查询日表tb_analysis_by_day
        QueryWrapper<AnalysisByDay> wrapper = new QueryWrapper<>();
        wrapper.eq("record_date",now);
        AnalysisByDay analysisByDay = analysisByDayMapper.selectOne(wrapper);

        //3. 判断：如果没有日表记录，则执行插入
        if (analysisByDay == null) {
            analysisByDay = new AnalysisByDay();
            analysisByDay.setRecordDate(date);
            analysisByDay.setCreated(new Date());
            analysisByDay.setUpdated(new Date());
            analysisByDayMapper.insert(analysisByDay);
        }

        //4. 统计日志表中数据：新增用户、登陆次数、活跃用户、次数留存
        Long numRegistered = logMapper.queryNumsByType(now,"0102");
        Long numLogin = logMapper.queryNumsByType(now,"0101");
        Long numActive = logMapper.queryNumsByDate(now);
        // 获取昨天的日期
        String yes = ComputeUtil.offsetDay(date, -1);
        Long numRetention1d = logMapper.queryNumsRetention1d(now,yes);


        //5. 根据查询统计的结果，更新日表数据
        analysisByDay.setNumRegistered(numRegistered.intValue());//新增用户
        analysisByDay.setNumLogin(numLogin.intValue());//登陆次数
        analysisByDay.setNumActive(numActive.intValue());//活跃用户
        analysisByDay.setNumRetention1d(numRetention1d.intValue());//次数留存
        analysisByDayMapper.updateById(analysisByDay);
    }

    /**
     * 接口名称：新增、活跃用户、次日留存率
     */
    public ResponseEntity<Object> queryByTime(Long sd,Long ed,Integer type) {
        //将sd,ed转化日期对象
        String start = new SimpleDateFormat("yyyy-MM-dd").format(sd);
        String end = new SimpleDateFormat("yyyy-MM-dd").format(ed);
        //查询日表得到该时间段所有的对象集合--今年
        List<AnalysisByDay> dayList = analysisByDayMapper.queryByTime(start, end);

        //根据tpye返回需要的对象  101 新增 102 活跃用户 103 次日留存率
        //得到今年的时间段结果对象
        List<Map<String, Object>> thisYearList = findYear(dayList, type);
        //得到去年今日的时间
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(sd);
        c.add(Calendar.YEAR,-1);
        String lastStart = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        c.setTimeInMillis(ed);
        c.add(Calendar.YEAR,-1);
        String lastEnd = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        //查询日表得到该时间段所有的对象集合--去年
        List<AnalysisByDay> lastDayList = analysisByDayMapper.queryByTime(lastStart, lastEnd);
        //得到去年的时间段结果对象
        List<Map<String, Object>> lastYearList = findYear(lastDayList, type);

        //将结果放入一个map中
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        result.put("thisYear",thisYearList);
        result.put("lastYear",lastYearList);
        return ResponseEntity.ok(result);
    }


    /**
     * 抽取获得时间段对象公共方法
     */
    public List<Map<String, Object>> findYear(List<AnalysisByDay> dayList ,Integer type){
        List<Map<String, Object>> yearList = new ArrayList<>();
        //得到今年的所有对应日表
        if(dayList!=null&&dayList.size()>0) {
            for (AnalysisByDay analysis : dayList) {
                Map<String, Object> thisYear = new HashMap<>();
                switch (type) {
                    case 101:
                        thisYear.put("title", analysis.getRecordDate());
                        thisYear.put("amount", analysis.getNumRegistered());
                        yearList.add(thisYear);
                        break;
                    case 102:
                        thisYear.put("title", analysis.getRecordDate());
                        thisYear.put("amount", analysis.getNumActive());
                        yearList.add(thisYear);
                        break;
                    case 103:
                        thisYear.put("title", analysis.getRecordDate());
                        thisYear.put("amount", analysis.getNumRetention1d());
                        yearList.add(thisYear);
                        break;
                }
            }
        }
        return yearList;
    }
}
