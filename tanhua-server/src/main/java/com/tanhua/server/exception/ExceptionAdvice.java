package com.tanhua.server.exception;

import com.tanhua.domain.vo.ErrorResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理
 * 1. @ControllerAdvice + @ExceptionHandler 实现全局异常处理
 * 2. @ExceptionHandler 指定处理的异常类型
 */
@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<Object> handlerException(Exception e){
        e.printStackTrace();
        return ResponseEntity.status(500).body(ErrorResult.error());
    }
}
