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
 * 平台信息表
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("platform")
public class Platform implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 平台编码
     */
    @TableField("platform_no")
    private String platformNo;

    /**
     * 平台名称
     */
    @TableField("platform_name")
    private String platformName;

    /**
     * 状态：0 正常 1 停用
     */
    @TableField("status")
    private String status;

    /**
     * 创建人
     */
    @TableField("create_by")
    private String createBy;

    /**
     * 更新人
     */
    @TableField("update_by")
    private String updateBy;

    /**
     * 结算支行名称
     */
    @TableField("settle_branch_name")
    private String settleBranchName;

    /**
     * 结算联行号
     */
    @TableField("settle_branch_no")
    private String settleBranchNo;

    /**
     * 结算持卡人名称
     */
    @TableField("settle_bank_card_name")
    private String settleBankCardName;

    /**
     * 结算银行名称
     */
    @TableField("settle_bank_name")
    private String settleBankName;

    /**
     * 结算卡银行预留手机
     */
    @TableField("settle_phone")
    private String settlePhone;

    /**
     * 结算卡市
     */
    @TableField("settle_city")
    private String settleCity;

    /**
     * 结算卡省
     */
    @TableField("settle_province")
    private String settleProvince;

    /**
     * 结算银行卡号
     */
    @TableField("settle_bank_no")
    private String settleBankNo;

    /**
     * 证件类型
     */
    @TableField("legal_card_type")
    private String legalCardType;

    /**
     * 证件号码
     */
    @TableField("legal_id_card")
    private String legalIdCard;

    /**
     * 证件号码
     */
    @TableField("req_key")
    private String reqKey;

    /**
     * 证件号码
     */
    @TableField("allow_refund_day")
    private String allowRefundDay;

    @TableField("trans_notify_url")
    private String transNotifyUrl;

    @TableField("refund_notify_url")
    private String refundNotifyUrl;


}
