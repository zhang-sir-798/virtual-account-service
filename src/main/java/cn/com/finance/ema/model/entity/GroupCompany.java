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
 * 集团公司
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("group_company")
public class GroupCompany implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 集团编号
     */
    @TableField("group_no")
    private String groupNo;

    /**
     * 集团名称
     */
    @TableField("group_name")
    private String groupName;

    /**
     * 手机号
     */
    @TableField("group_phone")
    private String groupPhone;

    /**
     * 状态 0正常 1停用
     */
    @TableField("status")
    private String status;

    /**
     * 联系人
     */
    @TableField("name")
    private String name;


}
