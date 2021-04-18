package com.tanhua.domain.db;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测灵魂-提交问卷
 * 用来接受返回的参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Answers extends BasePojo{

        private String questionId;
        private String optionId;

}