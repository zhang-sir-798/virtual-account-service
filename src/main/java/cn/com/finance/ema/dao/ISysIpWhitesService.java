package cn.com.finance.ema.dao;


import cn.com.finance.ema.model.entity.SysIpWhites;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Set;

/**
 * <p>
 * 白名单配置表 服务类
 * </p>
 *
 * @author zhang_sir
 * @since 2022-04-19
 */
public interface ISysIpWhitesService extends IService<SysIpWhites> {


    /**
     * 查询账户
     */
    Set<String> queryAll();

}
