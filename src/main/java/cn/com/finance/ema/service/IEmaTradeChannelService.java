package cn.com.finance.ema.service;


import cn.com.finance.ema.model.req.core.EmaOnlinePayReq;
import cn.com.finance.ema.model.req.core.EmaOnlineRefundReq;

/**
 * <p>
 * 付款订单表 服务类
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
public interface IEmaTradeChannelService {


    //下单
    boolean channelPay(EmaOnlinePayReq req);

    //退款
    boolean channelRefund(EmaOnlineRefundReq req);


}
