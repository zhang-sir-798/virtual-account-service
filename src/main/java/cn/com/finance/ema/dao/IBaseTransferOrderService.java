package cn.com.finance.ema.dao;


import cn.com.finance.ema.model.entity.BaseTransferOrder;
import cn.com.finance.ema.model.req.core.*;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 付款订单表 服务类
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
public interface IBaseTransferOrderService extends IService<BaseTransferOrder> {

    boolean saveOnlineOrder(EmaOnlinePayReq req);

    boolean saveOnlineRefundOrder(EmaOnlineRefundReq req);

    boolean updateOnlineSubOrder(EmaOnlineNoticeReq req);

}
