package cn.com.finance.ema.model.req.core;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

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
public class subOrders implements Serializable {


    /**
     * 子商户编号
     **/
    private String subMerNo;

    /**
     * 子交易总金额
     **/
    private String subAmount;

    /**
     * 子交易订单号
     **/
    private String subTradeNo;

    /**
     * 子单产品 json 数组
     **/
    private List<EmaProductsReqSub> products;


    //请求参数


    //下面是实体参数
    /**
     * 平台编号(必填)
     **/
    private String platformNo;

    /**
     * 付款人客户编号(必填)
     **/
    private String customerNo;

    /**
     * 回调地址
     **/
    private String transNotifyUrl;

    /**
     * 交易日期 YYYYMMdd
     **/
    private String orderDate;

    /**
     * 交易时间 HHmmss
     **/
    private String orderTime;

    private String publicKey;

    //终端号
    private String termId;

    /**
     * 备注
     **/
    private String remark;

    private MerFeeReq merFeeReq;

    private PlatformFeeReq platformFeeReq;

    private String bankRatio;

    private String resCode;
    private String resMsg;
    private String transStatus;
    private String status;

    private String productNo;

    //充值提示语
    private boolean recharge;
    //商户账户号
    private String merchantSubNo;
    //商户分账金额
    private String merchantAmount;
    //商户交易手续费
    private String merchantTransFee;


}
