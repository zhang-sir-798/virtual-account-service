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
 * 账户表
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("account")
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 账户编号
     */
    @TableField("account_no")
    private String accountNo;

    /**
     * 子公司编号
     */
    @TableField("subsidiary_no")
    private String subsidiaryNo;

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
     * 商户编号
     */
    @TableField("merchant_no")
    private String merchantNo;

    /**
     * 客户账户
     */
    @TableField("person_no")
    private String personNo;

    /**
     * 余额
     */
    @TableField("balance")
    private String balance;

    /**
     * 冻结金额
     */
    @TableField("freeze_money")
    private String freezeMoney;

    /**
     * 0正常 1停用
     */
    @TableField("status")
    private String status;

    /**
     * 账户类型 0 集团账户 1 子公司账户 2功能账户 3商户账户 4平台账户 5客户账户 6交易手续费账户 7代付手续费账户  8分账手续费账户
     */
    @TableField("type")
    private String type;

    /**
     * 流水金额(子公司实时金额)
     */
    @TableField("now_money")
    private String nowMoney;

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
     * 最后记账日期：yyyyMMdd
     */
    @TableField("last_dt")
    private String lastDt;

    /**
     * 最后一笔资金明细编号
     */
    @TableField("last_ac_bal_data_id")
    private String lastAcBalDataId;


}
