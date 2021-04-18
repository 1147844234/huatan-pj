package com.tanhua.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * vo : value object (数据传递)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResult {

    private String errCode;
    private String errMessage;


    public static ErrorResult error() {
        ErrorResult errorResult = new ErrorResult();
        errorResult.setErrCode("999999");
        errorResult.setErrMessage("系统异常稍后再试!!!");
        return errorResult;

        //return ErrorResult.builder().errCode("999999").errMessage("系统异常稍后再试").build();
    }

    public static ErrorResult fail() {
        return ErrorResult.builder().errCode("000000").errMessage("发送验证码失败").build();
    }

    public static ErrorResult duplicate() {
        return ErrorResult.builder().errCode("000001").errMessage("上一次发送的验证码还未失效").build();
    }

    public static ErrorResult loginError() {
        return ErrorResult.builder().errCode("000002").errMessage("验证码失效").build();
    }

    public static ErrorResult faceError() {
        return ErrorResult.builder().errCode("000003").errMessage("图片非人像，请重新上传!").build();
    }

    public static ErrorResult mobileError() {
        return ErrorResult.builder().errCode("000004").errMessage("更换手机号码失败,手机号已经注册!").build();
    }

    public static ErrorResult freezeTime(String day,String time) {
        return ErrorResult.builder().errCode("000005").errMessage("您的账户已被冻结,冻结时间为为"+day+"天,你将被冻结到"+time).build();
    }
    public static ErrorResult freezeNever() {
        return ErrorResult.builder().errCode("000006").errMessage("您的账户已被冻结,冻结时间为永久").build();
    }
    public static ErrorResult freezeComment(String day,String time) {
        return ErrorResult.builder().errCode("000007").errMessage("您的账户已被冻结发言,冻结时间为为"+day+"天,你将被冻结到"+time).build();
    }
    public static ErrorResult freezeCommentNever() {
        return ErrorResult.builder().errCode("000008").errMessage("您的账户已被冻结发言,冻结时间为永久").build();
    }
    public static ErrorResult freezePublish(String day,String time) {
        return ErrorResult.builder().errCode("000009").errMessage("您的账户已被冻结发布动态,冻结时间为为"+day+"天,你将被冻结到"+time).build();
    }
    public static ErrorResult freezePublishNever() {
        return ErrorResult.builder().errCode("000010").errMessage("您的账户已被冻结发布动态,冻结时间为永久").build();
    }
}