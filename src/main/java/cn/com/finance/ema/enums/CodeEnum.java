package cn.com.finance.ema.enums;

/**
 * 响应码枚举
 *
 * @author: zhang_sir
 * @version: 1.0
 */
public enum CodeEnum {

    /**
     * 成功
     */
    SUCCESS("0000", "成功"),
    /**
     * 检查错误
     */
    BUS_TRUE("1000", "业务处理中"),
    SIGN_ERROR("1002", "报文验签失败"),
    ILLEGAL_PARAM("2001", "参数错误"),
    ILLEGAL_PARAM_AMT("2002", "金额参数错误"),
    ILLEGAL_PARAM_ORDER("2003", "单号参数错误,流水号重复"),
    ILLEGAL_PARAM_OGR_ORDER("2004", "单号参数错误,原交易流水号有误"),
    ILLEGAL_PARAM_FUNC_AMT("2005", "功能账户额度不足"),
    ILLEGAL_DUPLICATION_PARAM_OGR_ORDER("2006", "单号参数错误,原交易流水号中有重复"),
    ILLEGAL_PARAM_ORDER_NULL("2008", "单号参数为空"),
    ILLEGAL_ORDER_STATUS("2007", "订单状态非成功,请检查"),
    ILLEGAL_ACC_FAIL("2011", "账户处理失败"),
    /**
     * 失败/异常
     */
    NONE("9995", "原交易不存在"),
    ILLEGAL_IP("9996", "非法IP"),
    BUS_NU("9997", "结果未知"),
    BUS_FAIL("9998", "处理失败"),
    SYSTEM_ERROR("9999", "系统异常");

    private String resCode;

    private String resMsg;

    CodeEnum(String resCode, String resMsg) {
        this.resMsg = resMsg;
        this.resCode = resCode;
    }

    public String getResCode() {
        return resCode;
    }

    public String getResMsg() {
        return resMsg;
    }

    /**
     * 根据code获取去value
     *
     * @param resCode
     * @return resMsg
     */
    public static String getResMsgByCode(String resCode) {
        for (CodeEnum codeEnum : CodeEnum.values()) {

            if (codeEnum.equals(codeEnum.getResCode())) {
                return codeEnum.getResMsg();
            }
        }
        return null;
    }

}

