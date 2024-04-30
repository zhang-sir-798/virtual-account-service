package cn.com.finance.ema.dao.Impl;

import cn.com.finance.ema.config.EmaProperties;
import cn.com.finance.ema.dao.IBaseTransferOrderService;
import cn.com.finance.ema.mapper.BaseTransferOrderMapper;
import cn.com.finance.ema.model.entity.BaseTransferOrder;
import cn.com.finance.ema.model.req.core.EmaOnlineNoticeReq;
import cn.com.finance.ema.model.req.core.EmaOnlinePayReq;
import cn.com.finance.ema.model.req.core.EmaOnlineRefundReq;
import cn.com.finance.ema.service.IAcContextService;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 付款订单表 服务实现类
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
@Slf4j
@Service
public class BaseTransferOrderServiceImpl extends ServiceImpl<BaseTransferOrderMapper, BaseTransferOrder> implements IBaseTransferOrderService {

    private final EmaProperties properties;
    private final IAcContextService iAcContextService;
    private final BaseBatchOrderServiceImpl baseBatchOrderServiceImpl;

    public BaseTransferOrderServiceImpl(IAcContextService iAcContextService, BaseBatchOrderServiceImpl baseBatchOrderServiceImpl, EmaProperties properties) {
        this.properties = properties;
        this.iAcContextService = iAcContextService;
        this.baseBatchOrderServiceImpl = baseBatchOrderServiceImpl;
    }

    @Override
    public boolean saveOnlineOrder(EmaOnlinePayReq req) {

        boolean success = saveBatch(req.getBaseTransferOrders(), req.getBaseTransferOrders().size());

        //处理主订单
        if (success) {
            success = baseBatchOrderServiceImpl.save(req.getBaseBatchOrder());
        }

        return success;
    }

    @Override
    public boolean saveOnlineRefundOrder(EmaOnlineRefundReq req) {

        boolean success = false;
        //余额类可直接设置为最终态
        if (StrUtil.equalsIgnoreCase("ONLINE001", req.getTrxType())) {
            //设置原始订单退款状态为1
            if (updateBatchById(req.getSourceOrders())) {
                success = saveBatch(req.getBaseTransferOrders());
            }
            if (success) {
                success = baseBatchOrderServiceImpl.save(req.getBaseBatchOrder());
            }
        } else {
            success = saveBatch(req.getBaseTransferOrders());
            if (success) {
                success = baseBatchOrderServiceImpl.save(req.getBaseBatchOrder());
            }
        }

        return success;
    }

    @Override
    public boolean updateOnlineSubOrder(EmaOnlineNoticeReq req) {

        return updateById(req.getOrder());
    }


}
