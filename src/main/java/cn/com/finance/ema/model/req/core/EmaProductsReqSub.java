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
 * @since 2021/11/30 11:57
 */
@Data
public class EmaProductsReqSub implements Serializable {

    /**
     * 产品金额
     **/
    private String productAmount;

    /**
     * 产品单号
     **/
    private String subProdTradeNo;

    /**
     * 产品占比
     **/
    private String productPercent;

    /**
     * 产品编号
     **/
    private String productNo;

}
