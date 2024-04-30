package cn.com.finance.ema.model.req.core;

import cn.com.finance.ema.model.entity.BaseBatchOrder;
import cn.com.finance.ema.model.entity.BaseTransferOrder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 线上余额类
 * </p>
 *
 * @author zhang_sir
 * @version v1.0.0
 * @since 2021/11/30 11:57
 */
@Data
public class EmaOnlinePayReq implements Serializable {
    /**
     * 商品描述(必填)
     **/
    private String body;
    /**
     * 商户编号(必填)
     **/
    private String merNo;
    /**
     * 合并交易金额(必填)
     **/
    private String amount;
    /**
     * 请求订单号(必填)
     **/
    private String orderNo;
    /**
     * 代理商编号(必填) =平台
     **/
    private String agentNo;
    /**
     * 交易类型码(必填)
     **/
    private String trxType;
    /**
     * 用户 openid
     **/
    private String openId;
    /**
     * 子单信息 json 数组
     **/
    private String subOrders;
    /**
     * 签名
     **/
    private String sign;

    private List<EmaOrderReqSub> emaOnlinePayReqSub;

    //待插入订单
    private List<BaseTransferOrder> baseTransferOrders;
    //待插入批次
    private BaseBatchOrder baseBatchOrder;

    private String publicKey;


    /**
     * 备注
     **/
    private String remark;


    private String resCode;
    private String resMsg;
    private String transStatus;

    //支付参数 W02-微信公众号支付  W06-微信小程序 返回此字段
    private String payInfo;
    //返回对应的响应参数 A03-支付窗支付 U05-银联云闪付(ApplePay) 线下余额类
    private String tradeNo;


    //0下单  1退款
    private String transType;

    private String channelNo;


    //平台账户号
    private String plateSubNo;
    //平台分账金额
    //private String platformAmount;
    //平台分账手续费
    //private String platformFee;


    //功能账户
    private String funNo;
    //子集团编号
    private String subsidiaryNo;

    //交易手续费 - 自己 -合计
    private String transFee;
    //分账手续费 - 自己 -合计
    private String splitFee;


    //是否可以直接入账
    private boolean recorded = false;


}
