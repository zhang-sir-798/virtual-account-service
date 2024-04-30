package cn.com.finance.ema.handler;

import cn.com.finance.ema.model.resp.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 校验入参统一异常处理
 *
 * @date: 2021/05/14 17:48
 * @author: zhang_sir
 * @version: 1.0
 */

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(BindException.class)
    public String validExceptionHandler(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        assert fieldError != null;
        log.error("命中字段：{} , 检出原因：{}", fieldError.getField(), fieldError.getDefaultMessage());
        return Result.error(fieldError.getDefaultMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String validExceptionHandler(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        assert fieldError != null;
        log.error("命中字段：{} , 检出原因：{}", fieldError.getField(), fieldError.getDefaultMessage());
        return Result.error(fieldError.getDefaultMessage());
    }

}
