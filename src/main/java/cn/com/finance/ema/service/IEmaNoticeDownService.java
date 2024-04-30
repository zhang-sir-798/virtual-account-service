package cn.com.finance.ema.service;


import org.springframework.scheduling.annotation.Async;

/**
 * <p>
 * 异步通知服务
 * </p>
 *
 * @author zhangsir
 * @version v1.0.0
 * @since 2021/12/19 13:43
 */
public interface IEmaNoticeDownService {

    /**
     * 异步通知下游
     *
     * @param params
     * @param url
     **/
    @Async("taskExecutor")
    void notify(String params, String url);

}
