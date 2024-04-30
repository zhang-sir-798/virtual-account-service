package cn.com.finance.ema.service;


import cn.com.finance.ema.model.req.core.EmaQueryOnlineReq;

/**
 * <p>
 * 查询服务类
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
public interface IEmaQueryChannelService {

    //同步查询
    void query(EmaQueryOnlineReq req, String reqStr);

}
