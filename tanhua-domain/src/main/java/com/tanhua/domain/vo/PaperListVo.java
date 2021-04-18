package com.tanhua.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 测灵魂-问卷列表的返回vo
 */
public class PaperListVo {
    private String  id;//问卷id
    private String name;//问卷名称
    private String cover;//封面
    private String level;//级别
    private Integer star;//星别
    private List<?> questions= Collections.EMPTY_LIST;//试题
    private Integer isLock;//是否锁住
    private String reportId;//最新报告id
}