package cn.com.finance.ema.dao.Impl;


import cn.com.finance.ema.dao.ISysIpWhitesService;
import cn.com.finance.ema.mapper.SysIpWhitesMapper;
import cn.com.finance.ema.model.entity.SysIpWhites;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 白名单配置表 服务实现类
 * </p>
 *
 * @author zhang_sir
 * @since 2022-04-19
 */
@Service
public class SysIpWhitesServiceImpl extends ServiceImpl<SysIpWhitesMapper, SysIpWhites> implements ISysIpWhitesService {

    @Override
    public Set<String> queryAll() {

        QueryWrapper<SysIpWhites> queryWrapper = new QueryWrapper<>();

        queryWrapper.lambda().eq(SysIpWhites::getIpStatus, "0");


        List<SysIpWhites> whites= list(queryWrapper);

        //Set<String> sysCodes = iPage.getRecords().stream().map(AuthMenu::getSysCode).collect(Collectors.toSet());

        return whites.stream().map(SysIpWhites::getIpValue).collect(Collectors.toSet());
    }
}
