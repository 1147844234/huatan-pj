package com.tanhua.domain.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;

public enum CoverEnum implements IEnum<String> {

    MAOTTOUYING("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/owl.png"),
    BAITU("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/rabbit.png"),
    HULI("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/fox.png"),
    SHIZI("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/lion.png");

    private String desc;

    CoverEnum(String desc) {
        this.desc = desc;
    }

    @Override
    public String getValue() {
        return this.desc;
    }
}