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
public class EmaQueryOnlineReq implements Serializable {

    @NotBlank(message = "付款商户编号不能为空")
    private String merNo;
    //交易时上送的请求单号此字 段与 bankOrderNo 需选择上 送,都上送以此字段为准
    @NotBlank(message = "订单编号不能为空")
    private String orderNo;

    //用户支付成功后微信或支付 宝的商家订单号,与orderNo字段需选择上送,都上送以orderNo 为准
    private String bankOrderNo;
    @NotBlank(message = "签名不能为空")
    private String sign;
    @NotBlank(message = "版本号不能为空")
    private String version;

    private String agentNo;

    private String publicKey;

    private String resCode;
    private String resMsg;


    //批次订单总金额
    private String totalAmount;

    private String serialNo;

    private String orderStatus;

}
