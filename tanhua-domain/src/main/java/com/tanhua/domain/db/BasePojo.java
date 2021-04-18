package com.tanhua.domain.db;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class BasePojo implements Serializable {
    // 设置插入时候自动填充
    @TableField(fill = FieldFill.INSERT)
    private Date created;
    // 支持插入时候与更新时候，都可以自动填充
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updated;
}