package com.tanhua.manage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanhua.manage.domain.Admin;
import com.tanhua.manage.domain.Log;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface LogMapper extends BaseMapper<Log> {
    /**
     * 查询新增用户、登陆次数
     * type
     *    0101 登陆次数
     *    0102 新增用户
     */
    @Select("SELECT COUNT(DISTINCT user_id) FROM tb_log " +
            "WHERE log_time=#{now} AND TYPE=#{type}")
    Long queryNumsByType(@Param("now") String now, @Param("type") String type);

    /**
     * 查询活跃用户
     * @param now
     * @return
     */
    @Select("SELECT COUNT(DISTINCT user_id) FROM tb_log WHERE log_time=#{now}")
    Long queryNumsByDate(@Param("now") String now);

    /**
     * 次日留存
     */
    @Select("SELECT COUNT(DISTINCT user_id) FROM tb_log WHERE log_time=#{now} AND TYPE='0101' AND user_id IN (\n" +
            "SELECT user_id FROM tb_log WHERE log_time=#{yes} AND TYPE='0102'\n" +
            ")")
    Long queryNumsRetention1d(@Param("now") String now, @Param("yes") String yes);
}
