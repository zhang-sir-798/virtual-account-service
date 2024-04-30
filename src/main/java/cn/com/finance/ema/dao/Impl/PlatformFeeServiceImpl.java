package cn.com.finance.ema.dao.Impl;

import cn.com.finance.ema.dao.IPlatformFeeService;
import cn.com.finance.ema.mapper.PlatformFeeMapper;
import cn.com.finance.ema.model.entity.PlatformFee;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 企业产品费率表 服务实现类
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
@Service
public class PlatformFeeServiceImpl extends ServiceImpl<PlatformFeeMapper, PlatformFee> implements IPlatformFeeService {

}
