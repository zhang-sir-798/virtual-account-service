package cn.com.finance.ema.model.req.core;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author zhang_sir
 * @version v1.0.0
 * @since 2021/12/2 11:03
 */
@Data
public class PlatformFeeReq implements Serializable {

    /**
     * 平台编号(必填)
     **/
    private String platformNo;

    private String orderAmount;

    private String feeType;

    private String fixedFee;

    private String ratioFee;


}
