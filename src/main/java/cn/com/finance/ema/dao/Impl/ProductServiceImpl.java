package cn.com.finance.ema.dao.Impl;

import cn.com.finance.ema.dao.IProductService;
import cn.com.finance.ema.mapper.ProductMapper;
import cn.com.finance.ema.model.entity.Product;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 产品表 服务实现类
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

}
