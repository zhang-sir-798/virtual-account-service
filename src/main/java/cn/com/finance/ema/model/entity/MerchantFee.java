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
 * 企业产品费率表
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("merchant_fee")
public class MerchantFee implements Serializable {

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
     * 产品编号
     */
    @TableField("product_no")
    private String productNo;

    /**
     * 费率类型 0 固定费率 1 比例费率 2 固定与比例费率
     */
    @TableField("fee_type")
    private String feeType;

    /**
     * 固定费率
     */
    @TableField("fixed_fee")
    private String fixedFee;

    /**
     * 比例费率
     */
    @TableField("ratio_fee")
    private String ratioFee;

    @TableField("status")
    private String status;


}
