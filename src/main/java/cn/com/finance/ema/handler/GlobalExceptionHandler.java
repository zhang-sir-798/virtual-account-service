package cn.com.finance.ema.handler;

import cn.com.finance.ema.enums.CodeEnum;
import cn.com.finance.ema.exception.EMAException;
import cn.com.finance.ema.model.resp.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;

/**
 * 全局异常处理
 *
 * @date: 2021/05/14 17:48
 * @author: zhang_sir
 * @version: 1.0
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 参数解析失败
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public String handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("参数解析失败", ex);
        return Result.error("参数解析失败");
    }

    /**
     * 参数验证异常
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("参数验证异常", ex);
        BindingResult result = ex.getBindingResult();
        FieldError error = result.getFieldError();
        String field = error.getField();
        String code = error.getDefaultMessage();
        String message = String.format("%s:%s", field, code);
        return Result.error("参数验证异常:" + message);
    }

    /**
     * 参数验证异常
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ValidationException.class)
    public String handleValidationException(ValidationException ex) {
        log.error("参数验证异常", ex);
        return Result.error("参数验证异常");


    }

    /**
     * 不支持当前请求方法
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.error("不支持当前请求方法", ex);
        return Result.error("不支持当前请求方法");

    }

    /**
     * 业务处理异常
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(EMAException.class)
    public String handleEmaException(EMAException ex) {
        log.error("业务处理异常", ex);
        return Result.error("业务处理异常");
    }


    /**
     * 捕捉其他所有异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public String globalException(Throwable ex) {
        logError(ex);
        return Result.error(CodeEnum.SYSTEM_ERROR.getResMsg());
    }

    private static void logError(Throwable ex) {
        StackTraceElement stackTraceElement2 = ex.getStackTrace()[0];
        String errMsg = "服务出现以下报错信息：" + "\r\n| 报错信息：" + ex.getMessage() + "| 文件名：" + stackTraceElement2.getFileName() + "| 方法名称：" + stackTraceElement2.getMethodName() + " | 行数：" + stackTraceElement2.getLineNumber();
        log.error(errMsg);
    }

}
