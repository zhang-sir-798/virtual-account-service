package cn.com.finance.ema.dao.Impl;


import cn.com.finance.ema.dao.IBaseOperationService;
import cn.com.finance.ema.mapper.BaseOperationMapper;
import cn.com.finance.ema.model.entity.BaseOperation;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 流水记录表 服务实现类
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
@Service
public class BaseOperationServiceImpl extends ServiceImpl<BaseOperationMapper, BaseOperation> implements IBaseOperationService {

    @Override
    public BaseOperation queryLast(String acNo, String acDt) {

        QueryWrapper<BaseOperation> queryWrapper = new QueryWrapper();

        queryWrapper.lambda().eq(BaseOperation::getAccountNo,acNo);
        queryWrapper.lambda().eq(BaseOperation::getLastDt,acDt);
        queryWrapper.lambda().orderByDesc(BaseOperation::getCreateTime);
        queryWrapper.lambda().last("limit 0,1");

        return getOne(queryWrapper);
    }
}
