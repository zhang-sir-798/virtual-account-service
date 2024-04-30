package cn.com.finance.ema.dao.Impl;

import cn.com.finance.ema.dao.IBaseBatchOrderService;
import cn.com.finance.ema.mapper.BaseBatchOrderMapper;
import cn.com.finance.ema.model.entity.BaseBatchOrder;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 付款批次表 服务实现类
 * </p>
 *
 * @author zhangsir
 * @since 2022-04-29
 */
@Slf4j
@Service
public class BaseBatchOrderServiceImpl extends ServiceImpl<BaseBatchOrderMapper, BaseBatchOrder> implements IBaseBatchOrderService {

}
