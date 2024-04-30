package cn.com.finance.ema.dao.Impl;


import cn.com.finance.ema.dao.IMerchantService;
import cn.com.finance.ema.mapper.MerchantMapper;
import cn.com.finance.ema.model.entity.Merchant;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商户信息表 服务实现类
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements IMerchantService {

}
