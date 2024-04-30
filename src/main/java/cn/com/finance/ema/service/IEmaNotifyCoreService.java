package cn.com.finance.ema.service;


import java.util.Map;

/**
 * <p>
 * 异步通知核心服务
 * </p>
 *
 * @author zhangsir
 * @version v1.0.0
 * @since 2022/06/21 13:54
 */
public interface IEmaNotifyCoreService {


    /**
     * 接受上游交易异步通知-QBS
     *
     * @param respMap
     **/

    boolean tradeNotify(Map<String, String> respMap);

    /**
     * 接受上游退款异步通知-QBS
     *
     * @param respMap
     **/

    boolean refundNotify(Map<String, String> respMap);


}
