package cn.com.finance.ema.service.impl;

import cn.com.finance.ema.constants.Constants;
import cn.com.finance.ema.enums.CodeEnum;
import cn.com.finance.ema.model.entity.BaseBatchOrder;
import cn.com.finance.ema.model.entity.BaseTransferOrder;
import cn.com.finance.ema.service.IEmaNoticeDownService;
import cn.com.finance.ema.utils.HttpUtil;
import cn.com.finance.ema.utils.SignOlUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 异步通知下游服务实现类
 * </p>
 *
 * @author zhangsir
 * @version v1.0.0
 * @since 2021/12/19 13:45
 */
@Slf4j
@Service
public class IEmaNoticeDownServiceImpl implements IEmaNoticeDownService {

    @Async("taskExecutor")
    public void asyncNotifyService(BaseBatchOrder batchOrder) {


        JSONObject success = new JSONObject();
        success.put(Constants.CODE, CodeEnum.SUCCESS.getResCode());
        success.put(Constants.MSG, CodeEnum.SUCCESS.getResMsg());
        success.put("amount", batchOrder.getTotalAmount());
        success.put("merNo", batchOrder.getSuperMerNo());
        success.put("orderNo", batchOrder.getBatchNo());
        success.put("serialNo", batchOrder.getSysOrderNo());
        success.put("payType", batchOrder.getTrxType());
        success.put(Constants.SIGN, SignOlUtil.encrypt(success.toJSONString(), batchOrder.getReqKey()));
        String respStr = success.toJSONString();

        notify(respStr, batchOrder.getTransNotifyUrl());

        batchOrder = null;


    }

    @Override
    public void notify(String params, String url) {

        try {
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Accept", "*/*");
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("params", params);
            //延迟500毫秒 防止通知提前到达
            Thread.sleep(500);
            Long start = System.currentTimeMillis();
            String resp = HttpUtil.doPostRequestFrom(url, headerMap, paramsMap);
            Long end = System.currentTimeMillis();
            log.info("[异步通知][请求地址:{} , 响应结果:{}  , 耗时:{} , 请求参数:{}]", url, resp, (end - start), params);

            //备用多次异步通知2022 04 25
            //notifyJob(baseTransferOrder, headerMap, params);
            headerMap.clear();
            headerMap = null;

        } catch (Exception e) {
            log.error("[异步通知] , 异常信息：{}]", e.toString());
        }


    }

    public void notifyJob(BaseTransferOrder baseTransferOrder, Map<String, String> headerMap, String params) {
        String resp = "";
        int index = 1;
        int retries = 5;
        long delay = 1;
        boolean hasNext = false;

        do {
            resp = HttpUtil.doPostRequestStr(baseTransferOrder.getTransNotifyUrl(), headerMap, params);
            if (StrUtil.isNotEmpty(resp) && StrUtil.equalsIgnoreCase("success", resp)) {
                index = 5;
            } else {
                if (delay < retries) {
                    delay = delay * delay * 1000 + 2000;
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            log.info("[异步通知进行中] 当前订单:{} , 已经通知次数第：{} 次 , 延迟时间：{} 秒 , 响应结果：{}", baseTransferOrder.getOrderNo(), index, delay / 1000, resp);
            hasNext = index < retries;
            if (hasNext) {
                index++;
                delay = index;
            }
        } while (hasNext);

        log.info("[异步通知结束][订单号:{} , 请求地址:{} , 响应结果:{}]", baseTransferOrder.getOrderNo(), baseTransferOrder.getTransNotifyUrl(), resp);
    }

}
