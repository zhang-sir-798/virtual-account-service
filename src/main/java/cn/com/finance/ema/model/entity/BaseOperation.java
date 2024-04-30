package cn.com.finance.ema.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 流水记录表
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("base_operation")
public class BaseOperation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    @TableField("operation_no")
    private String operationNo;

    /**
     * 资金明细编号
     */
    @TableField("ac_bal_data_id")
    private String acBalDataId;

    /**
     * 原订单号
     */
    @TableField("source_no")
    private String sourceNo;

    /**
     * 账户号
     */
    @TableField("account_no")
    private String accountNo;

    /**
     * 平台编号
     */
    @TableField("platform_no")
    private String platformNo;

    @TableField("agent_no")
    private String agentNo;

    /**
     * 通道编号
     */
    @TableField("channel_no")
    private String channelNo;

    /**
     * 操作金额
     */
    @TableField("operation_amount")
    private String operationAmount;

    /**
     * 操作手续费
     */
    @TableField("operation_fee")
    private String operationFee;

    /**
     * 操作前余额
     */
    @TableField("before_money")
    private String beforeMoney;

    /**
     * 操作后余额
     */
    @TableField("after_money")
    private String afterMoney;

    /**
     * 功能描述 例如代付付款，提现等
     */
    @TableField("feature_type")
    private String featureType;

    /**
     * 类型：减去 MINUS 增加 PLUS
     */
    @TableField("operation_type")
    private String operationType;

    /**
     * 备注
     */
    @TableField("operation_remark")
    private String operationRemark;

    /**
     * 订单日期
     */
    @TableField("operation_date")
    private String operationDate;

    /**
     * 订单时间
     */
    @TableField("operation_time")
    private String operationTime;

    /**
     * 日期
     */
    @TableField("last_dt")
    private String lastDt;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private String createTime;


    @TableField("payment_type")
    private String paymentType;

    @TableField("voch_type")
    private String vochType;

    @TableField("account_type")
    private String accountType;


}
