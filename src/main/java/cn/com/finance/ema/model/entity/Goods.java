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
 * 产品信息表
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("goods")
public class Goods implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 产品编号
     */
    @TableField("goods_no")
    private String goodsNo;

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
     * 产品名称
     */
    @TableField("goods_name")
    private String goodsName;

    /**
     * 产品介绍
     */
    @TableField("introduce")
    private String introduce;

    /**
     * 商品占比 例如0.6
     */
    @TableField("goods_rate")
    private String goodsRate;

    /**
     * 产品状态 0 正常 1 停用
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


}
