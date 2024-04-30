package cn.com.finance.ema.model.req.core;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

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
public class EmaOrderReqSub implements Serializable {

    /**
     * 子商户编号
     **/
    private String subMerNo;
    /**
     * 子交易总金额
     **/
    private String subAmount;
    /**
     * 子交易订单号
     **/
    private String subTradeNo;
    /**
     * 子单产品 json string
     **/
    private String products;

    /**
     * 子单产品 json array
     **/
    private List<EmaProductsReqSub> productList;

}
