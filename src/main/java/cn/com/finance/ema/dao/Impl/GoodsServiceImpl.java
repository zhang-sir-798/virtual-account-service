package cn.com.finance.ema.dao.Impl;


import cn.com.finance.ema.dao.IGoodsService;
import cn.com.finance.ema.mapper.GoodsMapper;
import cn.com.finance.ema.model.entity.Goods;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 产品信息表 服务实现类
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

}
