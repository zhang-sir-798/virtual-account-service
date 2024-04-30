package cn.com.finance.ema.model.resp.core;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 管理
 * </p>
 *
 * @author zhang_sir
 * @since 2021-05-14
 */
@Data

public class AmcCoreVo implements Serializable {

    /**
     * 主键
     */
    private String oid;

    /**
     * 单个算法oid
     */
    private String algorithmOid;

    /**
     * 算法名称
     */
    private String algorithmName;

    /**
     * 算法类型
     */
    private String algorithmType;

    /**
     * 算法图片
     */
    private String algorithmImage;

    /**
     * 算法描述
     */
    private String algorithmDescribe;

    /**
     * 类型:01查询全部算法，02查询全部模型，00不区分进行知识查询
     */
    private String type;

    /**
     * 单个模型oid
     */
    private String modelOid;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 模型类别：数据算法模型库-0001,行业机理模型库-0002,业务流程模型库-0003,研发仿真模型库-0004
     */
    private String modelType;

    /**
     * 模型图片
     */
    private String modelImage;

    /**
     * 模型描述
     */
    private String modelDescribe;

    /**
     * 创建时间
     */
    private String ctm;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 备用字段01
     */
    private String by01;

    /**
     * 备用字段02
     */
    private String by02;


}
