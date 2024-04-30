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
 * 付款批次表
 * </p>
 *
 * @author zhangsir
 * @since 2022-04-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("base_batch_order")
public class BaseBatchOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 批次号
     */
    @TableField("batch_no")
    private String batchNo;

    /**
     * 原订批次号
     */
    @TableField("source_batch_no")
    private String sourceBatchNo;

    /**
     * 渠道订单号
     */
    @TableField("channel_batch_no")
    private String channelBatchNo;

    //本系统订单号 交易
    @TableField("sys_order_no")
    private String sysOrderNo;


    /**
     * 通道编号
     */
    @TableField("channel_no")
    private String channelNo;

    /**
     * 功能账户编号
     */
    @TableField("fun_no")
    private String funNo;

    /**
     * 大商户编号
     */
    @TableField("super_mer_no")
    private String superMerNo;

    /**
     * 平台编号
     */
    @TableField("platform_no")
    private String platformNo;

    /**
     * 具体的平台账户编号
     */
    @TableField("plate_sub_no")
    private String plateSubNo;

    /**
     * 子公司编号
     */
    @TableField("subsidiary_no")
    private String subsidiaryNo;

    /**
     * 集团编号
     */
    @TableField("group_no")
    private String groupNo;

    /**
     * 交易产品
     */
    @TableField("product_no")
    private String productNo;

    /**
     * 付款金额（总订单金额）
     */
    @TableField("total_amount")
    private String totalAmount;

    /**
     * 交易手续费（总手续费）
     */
    @TableField("total_fee")
    private String totalFee;

    /**
     * 总分账手续费
     */
    @TableField("total_split_fee")
    private String totalSplitFee;

    /**
     * 通道交易手续费
     */
    @TableField("channel_fee")
    private String channelFee;

    /**
     * 平台总金额(分账金额)
     */
    @TableField("total_platform_amount")
    private String totalPlatformAmount;

    /**
     * 商户总金额(分账金额)
     */
    @TableField("total_mer_amount")
    private String totalMerAmount;

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

    /**
     * 0 交易  1退款
     */
    @TableField("trans_type")
    private String transType;

    /**
     * 为空=线下余额类，线上余额类=1，微信公众号=2，支付宝=3
     */
    @TableField("trx_type")
    private String trxType;

    /**
     * 支付结果时间
     */
    @TableField("result_time")
    private LocalDateTime resultTime;

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

    @TableField("req_key")
    private String reqKey;

}
