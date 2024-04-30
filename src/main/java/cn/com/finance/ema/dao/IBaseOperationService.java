package cn.com.finance.ema.dao;


import cn.com.finance.ema.model.entity.BaseOperation;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 流水记录表 服务类
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
public interface IBaseOperationService extends IService<BaseOperation> {

    BaseOperation queryLast(String acNo, String acDt);

}
