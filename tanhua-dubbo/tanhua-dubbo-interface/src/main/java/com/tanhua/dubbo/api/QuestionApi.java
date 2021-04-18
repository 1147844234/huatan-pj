package com.tanhua.dubbo.api;

import com.tanhua.domain.db.Question;

public interface QuestionApi {
    /**
     * 根据用户id查询陌生人问题
     * @param userId 当前登陆用户id
     * @return
     */
    Question findByUserId(Long userId);

    /**
     * 添加陌生人问题
     */
    void save(Question question);

    /**
     * 修改陌生人问题
     */
    void update(Question question);
}
