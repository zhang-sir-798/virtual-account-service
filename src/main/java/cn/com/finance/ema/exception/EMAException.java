package cn.com.finance.ema.exception;

import java.io.Serializable;

/**
 * 统一异常
 *
 * @date: 2021/05/14 17:48
 * @author: zhang_sir
 * @version: 1.0
 */
public class EMAException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 7588504848168740095L;
    private String code;
    private String msg;

    public EMAException(String message) {
        super(message);
    }

    public EMAException(String code, String msg) {
        super(code + ":" + msg);
        this.code = code;
        this.msg = msg;
    }
}