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
 * 商户信息表
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("merchant")
public class Merchant implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商户编号
     */
    @TableField("merchant_no")
    private String merchantNo;

    /**
     * 平台编号
     */
    @TableField("platform_no")
    private String platformNo;

    /**
     * 商户名称
     */
    @TableField("merchant_name")
    private String merchantName;

    /**
     * 注册地址
     */
    @TableField("register_addr")
    private String registerAddr;

    /**
     * 注册资本
     */
    @TableField("register_capital")
    private String registerCapital;

    /**
     * 成立日期
     */
    @TableField("register_date")
    private String registerDate;

    /**
     * 实收资本
     */
    @TableField("reality_capital")
    private String realityCapital;

    /**
     * 主营业务
     */
    @TableField("primary_business")
    private String primaryBusiness;

    /**
     * 是否统一产品/费率：0按平台，1按商户(默认按平台)
     */
    @TableField("platform_fee_type")
    private String platformFeeType;

    /**
     * MCC码
     */
    @TableField("mcc")
    private String mcc;

    /**
     * 商户类型
     */
    @TableField("merchant_type")
    private String merchantType;

    /**
     * 商户所属省份
     */
    @TableField("province")
    private String province;

    /**
     * 商户所属城市
     */
    @TableField("city")
    private String city;

    /**
     * 商户所属区县
     */
    @TableField("area")
    private String area;

    /**
     * 协议起始日期
     */
    @TableField("begin_date")
    private String beginDate;

    /**
     * 协议结束日期
     */
    @TableField("end_date")
    private String endDate;

    /**
     * 经营范围
     */
    @TableField("business_scope")
    private String businessScope;

    /**
     * 商户简称
     */
    @TableField("introduce")
    private String introduce;

    /**
     * 法人姓名
     */
    @TableField("legal_name")
    private String legalName;

    /**
     * 法人证件类型
     */
    @TableField("legal_card_type")
    private String legalCardType;

    /**
     * 法人证件号码
     */
    @TableField("legal_id_card")
    private String legalIdCard;

    /**
     * 法人证件有效期
     */
    @TableField("legal_card_validity")
    private String legalCardValidity;

    /**
     * 法人电话
     */
    @TableField("legal_phone")
    private String legalPhone;

    /**
     * 法人职业
     */
    @TableField("legal_work")
    private String legalWork;

    /**
     * 身份证正面照片
     */
    @TableField("legal_card_front_photo")
    private Long legalCardFrontPhoto;

    /**
     * 身份证反面照片
     */
    @TableField("legal_card_reverse_photo")
    private Long legalCardReversePhoto;

    /**
     * 是否三证合一   0否  1是
     */
    @TableField("is_integrate")
    private String isIntegrate;

    /**
     * 社会统一信用代码
     */
    @TableField("license_no")
    private String licenseNo;

    /**
     * 营业执照号码
     */
    @TableField("business_no")
    private String businessNo;

    /**
     * 营业执照有效日期
     */
    @TableField("license_validity")
    private String licenseValidity;

    /**
     * 组织机构代码
     */
    @TableField("organization_no")
    private String organizationNo;

    /**
     * 组织结构代码有效期
     */
    @TableField("organization_validity")
    private String organizationValidity;

    /**
     * 税证号码
     */
    @TableField("tallage_no")
    private String tallageNo;

    /**
     * 税证有效期
     */
    @TableField("tallage_validity")
    private String tallageValidity;

    /**
     * 办公地址
     */
    @TableField("business_address")
    private String businessAddress;

    @TableField("req_key")
    private String reqKey;

    /**
     * 开户许可证照片
     */
    @TableField("license_photo")
    private Long licensePhoto;

    /**
     * 税务登记证照片
     */
    @TableField("tallage_photo")
    private Long tallagePhoto;

    /**
     * 组织机构代码证照片
     */
    @TableField("organization_photo")
    private Long organizationPhoto;

    /**
     * 营业执照照片
     */
    @TableField("business_photo")
    private Long businessPhoto;

    /**
     * 门头照片
     */
    @TableField("door_photo")
    private Long doorPhoto;

    /**
     * 店内照片
     */
    @TableField("store_photo")
    private Long storePhoto;

    /**
     * 收银台照片
     */
    @TableField("cashier_desk_photo")
    private Long cashierDeskPhoto;

    /**
     * 特约用户协议照片
     */
    @TableField("agreement_photo")
    private Long agreementPhoto;

    /**
     * 支付开通照片
     */
    @TableField("payment_open_photo")
    private Long paymentOpenPhoto;

    /**
     * 审核信息
     */
    @TableField("audit_Lines")
    private String auditLines;

    /**
     * 商户后台通知地址
     */
    @TableField("notify_url")
    private String notifyUrl;

    /**
     * 结算类型 1.对私 2.对公 3.对公+对私
     */
    @TableField("settle_type")
    private String settleType;

    /**
     * 请求流水号（QB线上使用）
     */
    @TableField("outreqid")
    private String outreqid;

    /**
     * 商户审核结果通知地址
     */
    @TableField("merchant_notify_url")
    private String merchantNotifyUrl;

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
     * 银行卡照片（QB线上使用）
     */
    @TableField("bank_photo")
    private Long bankPhoto;

    /**
     * 手持身份证照片（QB线上使用）
     */
    @TableField("bank_legal_hold_card_photo")
    private Long bankLegalHoldCardPhoto;

    /**
     * 授权书照片（QB线上使用）
     */
    @TableField("auth_photo")
    private Long authPhoto;

    /**
     * QB省编码
     */
    @TableField("qb_province")
    private String qbProvince;

    /**
     * QB市编码
     */
    @TableField("qb_city")
    private String qbCity;

    /**
     * QB区域编码
     */
    @TableField("qb_area")
    private String qbArea;

    /**
     * 状态：0 正常 1 停用  
     */
    @TableField("status")
    private String status;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

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
     * 0门店 1线上
     */
    @TableField("merchant_goods_type")
    private String merchantGoodsType;


}
