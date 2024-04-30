package cn.com.finance.ema.controller;

import cn.com.finance.ema.model.resp.Result;
import cn.com.finance.ema.service.IEmaTradeCoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * trade线上余额类服务
 * </p>
 *
 * @author zhang_sir
 * @version v1.0.0
 * @since 2022/05/09 14:59
 */
@Slf4j
@ResponseBody
@RestController
@RequestMapping("/online")
public class EmaTradeOnlineController {

    private final IEmaTradeCoreService iEmaTradeCoreService;

    public EmaTradeOnlineController(IEmaTradeCoreService iEmaTradeCoreService) {
        this.iEmaTradeCoreService = iEmaTradeCoreService;
    }

    /**
     * 交易请求 线上余额类
     *
     * @param params
     * @return {@link Result}
     **/
    @PostMapping("/pay")
    public String onlinePay(@RequestParam("params") String params) {
        log.info("[下单请求] params请求参数：{}", params);
        return iEmaTradeCoreService.onlinePay(params);
    }

    /**
     * 退款请求 线上余额类
     *
     * @param params
     * @return {@link Result}
     **/
    @PostMapping("/refund")
    public String onlineRefund(@RequestParam("params") String params) {
        log.info("[退款请求] params请求参数：{}", params);
        return iEmaTradeCoreService.onlineRefund(params);
    }

    /**
     * 查询请求 余额类+QBS
     *
     * @param params
     * @return {@link Result}
     **/
    @PostMapping("/query")
    public String queryOrder(@RequestParam("params") String params) {
        //public String queryOrder(@RequestBody byte[] req) {
        log.info("[查询请求] params请求参数：{}", params);
        return iEmaTradeCoreService.queryOnlineOrder(params);
    }

    /**
     * 发货通知
     *
     * @param params
     * @return {@link Result}
     **/
    @PostMapping("/notice")
    public String notice(@RequestParam("params") String params) {
        log.info("[发货通知] params请求参数：{}", params);
        return iEmaTradeCoreService.notice(params);
    }
}
