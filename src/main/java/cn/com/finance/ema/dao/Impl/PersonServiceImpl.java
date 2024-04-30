package cn.com.finance.ema.dao.Impl;


import cn.com.finance.ema.dao.IPersonService;
import cn.com.finance.ema.mapper.PersonMapper;
import cn.com.finance.ema.model.entity.Person;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
@Service
public class PersonServiceImpl extends ServiceImpl<PersonMapper, Person> implements IPersonService {

}
