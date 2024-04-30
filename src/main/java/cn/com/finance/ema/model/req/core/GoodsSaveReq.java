package cn.com.finance.ema.model.req.core;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>
 * 产品信息
 * </p>
 */
@Data
public class GoodsSaveReq implements Serializable {

    @NotBlank(message = "商品占比编号不能为空")
    private String goodsNo;

    /**
     * 商户编号
     */
    @NotBlank(message = "商户编号不能为空")
    private String merchantNo;

    /**
     * 平台编号
     */
    @NotBlank(message = " 平台编号不能为空")
    private String platformNo;

    /**
     * 产品名称
     */
    @NotBlank(message = " 产品名称不能为空")
    private String goodsName;

    /**
     * 商品占比 例如0.6
     */
    @NotBlank(message = " 商品占比不能为空")
    private String goodsRate;

    /**
     * 产品状态 0 正常 1 停用
     */
    @NotBlank(message = " 产品状态不能为空")
    private String goodsStatus;

    /**
     * 签名
     **/
    @NotBlank(message = "签名不能为空")
    private String sign;

    private String resCode;
    private String resMsg;
    private String transStatus;

    private String publicKey;


}
