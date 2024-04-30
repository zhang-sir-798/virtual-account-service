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
 * 通道信息表
 * </p>
 *
 * @author zhangsir
 * @since 2022-06-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("channel_platform")
public class ChannelPlatform implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 通道编号
     */
    @TableField("channel_no")
    private String channelNo;

    /**
     * 平台商编
     */
    @TableField("platform_no")
    private String platformNo;

    /**
     * 渠道商户商编
     */
    @TableField("channel_mer_no")
    private String channelMerNo;

    /**
     * 渠道子商户商编
     */
    @TableField("channel_sub_mer_no")
    private String channelSubMerNo;

    @TableField("channel_public_key")
    private String channelPublicKey;

    @TableField("channel_private_key")
    private String channelPrivateKey;

    /**
     * 创建人
     */
    @TableField("create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    @TableField("update_by")
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 子公司轮询编号
     */
    @TableField("channel_sub_no")
    private String channelSubNo;


}
