package cn.com.finance.ema.service;

import cn.com.finance.ema.model.req.core.*;

/**
 * <p>
 *  前置检查服务
 * </p>
 *
 * @author zhangsir
 * @version v1.0.0
 * @since 2021/12/2 13:26
 */
public interface IEmaCheckService {

    /**
     * 线上余额类发起交易前置检查
     *
     * @param req
     * @return boolean
     */
    boolean checkOnlineSplitPayInfo(EmaOnlinePayReq req,String reqStr);

    /**
     * 线上余额类退款前置检查
     *
     * @param req
     * @return boolean
     */
    boolean checkOnlineRefundInfo(EmaOnlineRefundReq req,String reqStr);

    /**
     * 发货通知前置检查
     *
     * @param req
     * @return boolean
     */
    boolean checkNotice(EmaOnlineNoticeReq req, String reqStr);

}
