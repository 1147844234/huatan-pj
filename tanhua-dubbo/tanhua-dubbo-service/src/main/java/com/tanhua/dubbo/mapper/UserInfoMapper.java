package com.tanhua.dubbo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserInfoMapper extends BaseMapper<UserInfo> {
    /**
     * 分页查询用户（黑名单）
     * @Param 注解
     *   1. 当方法有多个参数使用
     *   2. 建立方法形参与占位符参数的对应关系
     */
    @Select("SELECT info.* FROM tb_black_list b,tb_user_info info " +
            "WHERE b.black_user_id=info.id AND b.user_id=#{userId}")
    IPage<UserInfo> findBlackList(Page<UserInfo> iPage, @Param("userId") Long userId);
}
