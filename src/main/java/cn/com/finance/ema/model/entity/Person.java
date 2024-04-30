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
 * 
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("person")
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 客户编号
     */
    @TableField("person_no")
    private String personNo;

    /**
     * 客户名称
     */
    @TableField("person_name")
    private String personName;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 身份证号
     */
    @TableField("id_card")
    private String idCard;

    /**
     * 银行卡号
     */
    @TableField("bank_no")
    private String bankNo;

    /**
     * 0正常 1停用
     */
    @TableField("status")
    private String status;

    /**
     * 平台编号
     */
    @TableField("platform_no")
    private String platformNo;


}
