package cn.com.finance.ema.model.req.core;

import cn.com.finance.ema.model.entity.BaseBatchOrder;
import cn.com.finance.ema.model.entity.BaseTransferOrder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author zhang_sir
 * @version v1.0.0
 * @since 2021/11/30 11:57
 */
@Data
public class EmaSplitRefundReq implements Serializable {

    /**
     * 版本号(必填)
     **/
    @NotBlank(message = "版本号不能为空")
    private String version;

    /**
     * 付款商户编号(必填)
     **/
    @NotBlank(message = "付款商户编号不能为空")
    private String merchantNo;

    /**
     * 平台编号(必填)
     **/
    @NotBlank(message = "平台编号不能为空")
    private String platformNo;

    private String customerNo;

    /**
     * 退款流水号(必填)
     **/
    @NotBlank(message = "退款流水号不能为空")
    private String orderNo;

    /**
     * 原交易流水号(必填)
     **/
    @NotBlank(message = "原交易流水号不能为空")
    private String oriOrderNo;

    /**
     * 退款金额(必填)单位分
     **/
    @NotBlank(message = "退款金额不能为空")
    private String orderAmount;

    /**
     * 回调地址
     **/
    @NotBlank(message = "回调地址不能为空")
    private String transNotifyUrl;

    /**
     * 交易日期 YYYYMMdd
     **/
    @NotBlank(message = "交易日期不能为空")
    private String orderDate;

    /**
     * 交易时间 HHmmss
     **/
    @NotBlank(message = "交易时间不能为空")
    private String orderTime;

    /**
     * 签名
     **/
    @NotBlank(message = "签名不能为空")
    private String sign;

    /**
     *
     **/
    @NotBlank(message = "备注不能为空")
    private String remark;

    private String publicKey;

    private String sysOrderNo;

    private String goodsNo;

    private String bankRatio;

    /**
     * 付款账户
     */
    private String transAccNo;
    private String transAccName;
    private String transAccBankNo;
    private String transAccMobile;
    private String transAccIdCard;


    private String resCode;
    private String resMsg;
    private String transStatus;

    private String productNo;

    //商户账户号
    private String merchantSubNo;
    //平台账户号
    private String plateSubNo;
    //功能账户
    private String funNo;
    //子集团编号
    private String subsidiaryNo;



}
