package cn.com.finance.ema.dao.Impl;


import cn.com.finance.ema.dao.IGroupCompanyService;
import cn.com.finance.ema.mapper.GroupCompanyMapper;
import cn.com.finance.ema.model.entity.GroupCompany;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 集团公司 服务实现类
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
@Service
public class GroupCompanyServiceImpl extends ServiceImpl<GroupCompanyMapper, GroupCompany> implements IGroupCompanyService {

}
