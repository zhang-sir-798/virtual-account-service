package cn.com.finance.ema.model.req.core;

import cn.com.finance.ema.model.entity.BaseBatchOrder;
import cn.com.finance.ema.model.entity.BaseTransferOrder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
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
public class EmaOnlineRefundReq implements Serializable {

    /**
     * 付款商户编号(必填)
     **/
    @NotBlank(message = "付款商户编号不能为空")
    private String merNo;

    /**
     * 退款流水号(必填)
     **/
    @NotBlank(message = "退款流水号不能为空")
    private String orderNo;

    /**
     * 平台编号(必填)
     **/
    @NotBlank(message = "平台编号不能为空")
    private String agentNo;

    /**
     * 版本号(必填)
     **/
    @NotBlank(message = "版本号不能为空")
    private String version;

    /**
     * 原交易平台订 oriOrderNo
     **/
    private String oriOrderNo;

    /**
     * 用户支付成功后微信或支付 宝的商家订单号,与 oriserialNo 字段需选择上送,都上送以 oriserialNo 为准
     **/
    private String refundNo;

    @NotBlank(message = "子产品订单号")
    private String subProdTradeNos;

    private List<String> subProdOrders;

    /**
     * 签名
     **/
    @NotBlank(message = "签名不能为空")
    private String sign;



    private String remark;

    private String publicKey;
    private String channelNo;

    private String resCode;
    private String resMsg;
    private String transStatus;

//    private String productNo;
//
//    //商户账户号
//    private String merchantSubNo;
//    //平台账户号
//    private String plateSubNo;
//    //功能账户
//    private String funNo;
//    //子集团编号
//    private String subsidiaryNo;

    //待插入订单
    private List<BaseTransferOrder> baseTransferOrders;

    //待插入批次
    private BaseBatchOrder baseBatchOrder;

    //原始订单
    private List<BaseTransferOrder> sourceOrders;

    //是否可以直接入账
    private boolean recorded = false;

    private String totalAmount;

    private String totalFee;
    private String funNo;
    private String customerNo;

    private String serialNo;
    private String refundNotifyUrl;

    private String trxType;
}
