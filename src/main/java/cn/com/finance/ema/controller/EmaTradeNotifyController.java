package cn.com.finance.ema.controller;

import cn.com.finance.ema.config.EmaProperties;
import cn.com.finance.ema.service.IEmaNotifyCoreService;
import cn.com.finance.ema.utils.channel.RsaUtils;
import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.Map;

/**
 * <p>
 * 异步通知
 * </p>
 *
 * @author zhang_sir
 * @version v1.0.0
 * @since 2022/06/21 10:28
 */
@Slf4j
@ResponseBody
@RestController
@RequestMapping("/notify")
public class EmaTradeNotifyController {

    private final EmaProperties properties;
    private final IEmaNotifyCoreService iEmaNotifyCoreService;

    public EmaTradeNotifyController(IEmaNotifyCoreService iEmaNotifyCoreService, EmaProperties properties) {
        this.properties = properties;
        this.iEmaNotifyCoreService = iEmaNotifyCoreService;
    }

    /**
     * 接受上游交易异步通知-QBS
     **/
    @PostMapping("/trade")
    public void onlinePay(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, String> respMap = parse(request);
        //验签
        if (!RsaUtils.verify(respMap, properties.getQbsPublicKey(), "RSA2", respMap.get("digest"))) {
            log.error("[交易-上游-异步通知] 验签失败 , 订单号:{}", respMap.get("orderNo"));
            response.getOutputStream().print("NO");
            return;
        }

        iEmaNotifyCoreService.tradeNotify(respMap);
        response.getOutputStream().print("OK");
        respMap.clear();

    }

    /**
     * 接受上游退款异步通知-QBS
     **/
    @PostMapping("/refund")
    public void onlineRefund(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, String> respMap = parse(request);
        //验签
        if (!RsaUtils.verify(respMap, properties.getQbsPublicKey(), "RSA2", respMap.get("digest"))) {
            log.error("[退款-上游-异步通知] 验签失败 , 退款订单号:{}", respMap.get("refundOrderNo"));
            response.getOutputStream().print("NO");
            return;
        }

        iEmaNotifyCoreService.refundNotify(respMap);
        response.getOutputStream().print("OK");
        respMap.clear();
        log.info("处理完成");
    }

    public Map<String, String> parse(HttpServletRequest request) {
        Map<String, String> res = MapUtil.newHashMap();
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();
                String value = request.getParameter(en);
                res.put(en, value);
                if (null == res.get(en) || "".equals(res.get(en))) {
                    res.remove(en);
                }
            }
        }
        log.info("[QB核心系统--统一下单--异步通知请求报文]:{}", res.toString());

        return res;
    }

}
