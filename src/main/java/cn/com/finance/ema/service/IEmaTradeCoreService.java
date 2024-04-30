package cn.com.finance.ema.service;


import cn.com.finance.ema.model.resp.Result;

/**
 * <p>
 * trade服务核心接口
 * </p>
 *
 * @author zhangsir
 * @version v1.0.0
 * @since 2021/11/30 11:43
 */
public interface IEmaTradeCoreService {

    /**
     * 下单 线上余额类
     *
     * @param req
     * @return {@link Result}
     **/
    String onlinePay(String req);

    /**
     * 退款 线上余额类
     *
     * @param req
     * @return {@link Result}
     **/
    String onlineRefund(String req);

    /**
     * 交易/退款查询 线上余额类
     *
     * @param req
     * @return {@link Result}
     **/
    String queryOnlineOrder(String req);

    /**
     * 发货通知
     *
     * @param req
     * @return {@link Result}
     **/
    String notice(String req);

}
