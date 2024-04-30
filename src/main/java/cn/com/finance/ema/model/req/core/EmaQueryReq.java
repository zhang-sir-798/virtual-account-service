package cn.com.finance.ema.model.req.core;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author zhang_sir
 * @version v1.0.0
 * @since 2021/12/03 11:57
 */
@Data
public class EmaQueryReq implements Serializable {

    @NotBlank(message = "版本号不能为空")
    private String version;

    @NotBlank(message = "付款商户编号不能为空")
    private String merchantNo;

    @NotBlank(message = "平台编号不能为空")
    private String platformNo;

    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @NotBlank(message = "交易日期不能为空")
    private String orderDate;

    @NotBlank(message = "签名不能为空")
    private String sign;

    private String publicKey;

    private String resCode;
    private String resMsg;
    private String transStatus;




}
