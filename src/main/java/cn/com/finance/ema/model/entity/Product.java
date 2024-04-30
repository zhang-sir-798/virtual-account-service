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
 * 产品表
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 产品编码
     */
    @TableField("product_no")
    private String productNo;

    /**
     * 产品名称
     */
    @TableField("product_name")
    private String productName;

    /**
     * 单笔最小限额
     */
    @TableField("single_min_limit")
    private String singleMinLimit;

    /**
     * 单笔最大限额
     */
    @TableField("single_max_limit")
    private String singleMaxLimit;

    /**
     * 单日限额
     */
    @TableField("day_max_limit")
    private String dayMaxLimit;

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

    @TableField("person_day_max_limit")
    private String personDayMaxLimit;





}
