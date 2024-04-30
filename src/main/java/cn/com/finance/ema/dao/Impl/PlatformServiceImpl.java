package cn.com.finance.ema.dao.Impl;


import cn.com.finance.ema.dao.IPlatformService;
import cn.com.finance.ema.mapper.PlatformMapper;
import cn.com.finance.ema.model.entity.Platform;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 平台信息表 服务实现类
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
@Service
public class PlatformServiceImpl extends ServiceImpl<PlatformMapper, Platform> implements IPlatformService {

}
