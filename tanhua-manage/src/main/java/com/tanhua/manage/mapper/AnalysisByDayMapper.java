package com.tanhua.manage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanhua.manage.domain.AnalysisByDay;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AnalysisByDayMapper extends BaseMapper<AnalysisByDay> {
    /**
     * 查询过去7天活跃以及过去30天活跃
     */
    @Select("SELECT SUM(num_active) num_active FROM tb_analysis_by_day " +
            "WHERE record_date BETWEEN #{start} AND #{end}")
    Long findNumActiveByDate(@Param("start") String start, @Param("end") String end);

    /**
     * 根据指定时间查询日表
     */
    @Select("SELECT  * FROM tb_analysis_by_day WHERE record_date BETWEEN #{start} AND #{end}")
    List<AnalysisByDay> queryByTime(@Param("start") String start , @Param("end")  String end);
}
