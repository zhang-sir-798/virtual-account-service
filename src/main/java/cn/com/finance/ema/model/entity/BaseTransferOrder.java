package cn.com.finance.ema.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 付款订单表
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("base_transfer_order")
public class BaseTransferOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 商户编号
     */
    @TableField("merchant_no")
    private String merchantNo;

    /**
     * 通道编号
     */
    @TableField("channel_no")
    private String channelNo;

    /**
     * 平台编号
     */
    @TableField("platform_no")
    private String platformNo;

    /**
     * 集团编号
     */
    @TableField("group_no")
    private String groupNo;

    /**
     * 付款人客户编号(必填)
     **/
    @TableField("customer_no")
    private String customerNo;

    /**
     * 交易产品
     */
    @TableField("product_no")
    private String productNo;

    /**
     * 产品占比编码
     */
    @TableField("goods_no")
    private String goodsNo;

    /**
     * 付款金额
     */
    @TableField("trans_amount")
    private String transAmount;

    /**
     * 子商户订单金额
     */
    @TableField("sub_mer_trans_amount")
    private String subMerTransAmount;


    /**
     * 交易手续费
     */
    @TableField("trans_fee")
    private String transFee;

    /**
     * 平台分账手续费
     */
    @TableField("platform_fee")
    private String platformFee;

    /**
     * 通道交易手续费
     */
    @TableField("channel_fee")
    private String channelFee;

    /**
     * 平台金额
     */
    @TableField("platform_amount")
    private String platformAmount;

    /**
     * 商户金额
     */
    @TableField("merchant_amount")
    private String merchantAmount;

    /**
     * 付款人名称
     */
    @TableField("trans_acc_name")
    private String transAccName;

    /**
     * 付款账户
     */
    @TableField("trans_acc_no")
    private String transAccNo;

    @TableField("trans_acc_bank_no")
    private String transAccBankNo;

    /**
     * 付款银行名称
     */
    @TableField("trans_acc_bank_name")
    private String transAccBankName;

    /**
     * 联行号
     */
    @TableField("trans_acc_settle_code")
    private String transAccSettleCode;

    /**
     * 开户行支行名称
     */
    @TableField("trans_acc_branch_name")
    private String transAccBranchName;

    /**
     * 付款人手机号
     */
    @TableField("trans_acc_mobile")
    private String transAccMobile;

    /**
     * 付款人身份证号
     */
    @TableField("trans_acc_id_card")
    private String transAccIdCard;

    /**
     * 返回编码
     */
    @TableField("res_code")
    private String resCode;

    /**
     * 返回信息
     */
    @TableField("res_msg")
    private String resMsg;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 异步通知地址
     */
    @TableField("trans_notify_url")
    private String transNotifyUrl;

    /**
     * 通道订单号
     */
    @TableField("sys_order_no")
    private String sysOrderNo;

    /**
     * 原订单号（trans_no）
     */
    @TableField("source_order_no")
    private String sourceOrderNo;

    /**
     * 订单状态: 处理中 DOING 成功 SUCCESS 失败 FAIL 
     */
    @TableField("trans_status")
    private String transStatus;

    /**
     * 结算状态 0 未结算 1 结算中 2 已结算
     */
    @TableField("payment_status")
    private String paymentStatus;

    /**
     * 对账结果信息
     */
    @TableField("check_result_msg")
    private String checkResultMsg;

//    /**
//     * 扫码时间
//     */
//    @TableField("scanned_time")
//    private LocalDateTime scannedTime;

    /**
     * 支付结果时间
     */
    @TableField("result_time")
    private LocalDateTime resultTime;

    @TableField("bank_ratio")
    private String bankRatio;

    /**
     * 0 交易  1退款 
     */
    @TableField("trans_type")
    private String transType;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    //商户账户号
    @TableField("merchant_sub_no")
    private String merchantSubNo;
    //平台账户号
    @TableField("plate_sub_no")
    private String plateSubNo;
    //功能账户
    @TableField("fun_no")
    private String funNo;

    //子公司账户
    @TableField("subsidiary_no")
    private String subsidiaryNo;

    @TableField("term_id")
    private String termId;

    /**
     * 本系统订单号
     */
    @TableField("self_order_no")
    private String selfOrderNo;

    /**
     * 子商户订单号
     */
    @TableField("sub_mer_order_no")
    private String subMerOrderNo;

    /**
     * 批次号
     */
    @TableField("batch_no")
    private String batchNo;

    /**
     * 交易类型
     */
    @TableField("trx_type")
    private String trxType;

    @TableField("req_key")
    private String reqKey;

    @TableField("is_refund")
    private String isRefund;

    @TableField("delivery_time")
    private LocalDateTime deliveryTime;

    @TableField("logistics_no")
    private String logisticsNo;



}
