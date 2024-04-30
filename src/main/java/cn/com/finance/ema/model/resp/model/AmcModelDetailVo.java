package cn.com.finance.ema.model.resp.model;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 模型实体
 * </p>
 *
 * @author zhang_sir
 * @since 2021-05-14
 */
@Data
public class AmcModelDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 单个模型id
     */
    private String oid;

    /**
     * 模型详情：模型名称
     */
    private String modelName;

    /**
     * 模型详情：模型说明
     */
    private String modelExplain;

    /**
     * 模型详情：功能介绍
     */
    private String modelFunctionIntroduce;

    /**
     * 模型详情：功能演示
     */
    private String modelFunctionDemo;

    /**
     * 技术文档链接
     */
    private String modelTechWord;

    /**
     * 模型结构json格式,每个模型都不同
     */
    private String modelStructure;

    /**
     * 模型详情图片
     */
    private String modelDetailUrl;

    /**
     * 创建时间
     */
    private String ctm;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 备用字段
     */
    private String by01;

    /**
     * 备用字段
     */
    private String by02;


}
