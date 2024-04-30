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
 * 白名单配置类
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_ip_whites")
public class SysIpWhites implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 参数主键
     */
    @TableId(value = "whites_id", type = IdType.AUTO)
    private Integer whitesId;

    /**
     * ip拥有者编码，例如: P001
     */
    @TableField("ip_own_code")
    private String ipOwnCode;

    /**
     * ip拥有者名称，例如: 永倍达
     */
    @TableField("ip_own_name")
    private String ipOwnName;

    /**
     * ip值
     */
    @TableField("ip_value")
    private String ipValue;

    /**
     * 启停状态 0=开启 ， 1=关闭
     */
    @TableField("ip_status")
    private String ipStatus;

    /**
     * 创建者
     */
    @TableField("create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    @TableField("update_by")
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;


}
