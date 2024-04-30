package cn.com.finance.ema.model.req.core;

import cn.com.finance.ema.model.entity.BaseTransferOrder;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>
 * 发货通知
 * </p>
 *
 * @author zhang_sir
 * @version v1.0.0
 * @since 2022/07/29 11:00
 */
@Data
public class EmaOnlineNoticeReq implements Serializable {

    /**
     * 产品子交易编号
     **/
    @NotBlank(message = "付款商户编号不能为空")
    private String subProdTradeNo;

    /**
     * 发货时间 yyyy-MM-dd HH:mm:ss
     **/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String sendTime;

    /**
     * 物流单号
     **/

    private String logisticsNo;

    /**
     * 版本号(必填)
     **/
    @NotBlank(message = "版本号不能为空")
    private String sign;

    private String resCode;
    private String resMsg;
    private String publicKey;
    private BaseTransferOrder order;

}
