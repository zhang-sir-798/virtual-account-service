package cn.com.finance.ema.service.impl;

import cn.com.finance.ema.config.EmaProperties;
import cn.com.finance.ema.constants.Constants;
import cn.com.finance.ema.dao.IAccountService;
import cn.com.finance.ema.dao.IBaseBatchOrderService;
import cn.com.finance.ema.dao.IBaseTransferOrderService;
import cn.com.finance.ema.enums.CodeEnum;
import cn.com.finance.ema.model.entity.BaseBatchOrder;
import cn.com.finance.ema.model.entity.BaseTransferOrder;
import cn.com.finance.ema.service.IAcContextService;
import cn.com.finance.ema.service.IEmaNoticeDownService;
import cn.com.finance.ema.service.IEmaNotifyCoreService;
import cn.com.finance.ema.utils.SignOlUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 异步通知核心服务实现类
 * </p>
 *
 * @author zhangsir
 * @version v1.0.0
 * @since 2022/06/21 13:45
 */
@Slf4j
@Service
public class IEmaNotifyCoreServiceImpl implements IEmaNotifyCoreService {

    private final EmaProperties properties;
    private final IAccountService iAccountService;
    private final IAcContextService iAcContextService;
    private final IEmaNoticeDownService iEmaNoticeDownService;
    private final IBaseBatchOrderService iBaseBatchOrderService;
    private final IBaseTransferOrderService iBaseTransferOrderService;


    public IEmaNotifyCoreServiceImpl(EmaProperties properties, IEmaNoticeDownService iEmaTradeNotifyService, IAccountService iAccountService, IAcContextService iAcContextService, IBaseTransferOrderService iBaseTransferOrderService, IBaseBatchOrderService iBaseBatchOrderService) {
        this.properties = properties;
        this.iAccountService = iAccountService;
        this.iAcContextService = iAcContextService;
        this.iEmaNoticeDownService = iEmaTradeNotifyService;
        this.iBaseBatchOrderService = iBaseBatchOrderService;
        this.iBaseTransferOrderService = iBaseTransferOrderService;
    }

    //接受上游交易异步通知
    @Override
    public boolean tradeNotify(Map<String, String> respMap) {
        boolean success = false;

        String orderNo = respMap.get("orderNo");//平台订单号
        String resultCode = respMap.get("resultCode");//响应码
        log.info("[QB核心系统--微支云统一下单--异步通知] [系统订单号：{} , 响应状态：{}]", orderNo, resultCode);
        BaseBatchOrder batchOrder = iBaseBatchOrderService.getBaseMapper().selectOne(new LambdaQueryWrapper<BaseBatchOrder>().eq(BaseBatchOrder::getSysOrderNo, orderNo));
        if (ObjectUtil.isNull(batchOrder)) {
            log.info("[QB核心系统--微支云统一下单--异步通知] [订单未查询到 , 订单号：{}]", orderNo);
            return false;
        }
        //处理成功
        if (StrUtil.equalsIgnoreCase(Constants.DOING, batchOrder.getTransStatus()) && StrUtil.equalsIgnoreCase("0000", resultCode)) {
            //1.账务处理
            success = iAccountService.handleOnlineAccBill(batchOrder);
            log.info("[QB核心系统--微支云统一下单--异步通知] [账务处理结果:{} , 系统单号:{}]", success, batchOrder.getSysOrderNo());
            //2.订单处理
            if (success) {
                batchOrder.setResCode(StrUtil.equalsIgnoreCase("0000", resultCode) ? CodeEnum.SUCCESS.getResCode() : CodeEnum.BUS_FAIL.getResCode());
                batchOrder.setResMsg(StrUtil.equalsIgnoreCase("0000", resultCode) ? CodeEnum.SUCCESS.getResMsg() : respMap.get("resultMsg"));
                batchOrder.setTransStatus(StrUtil.equalsIgnoreCase("0000", resultCode) ? Constants.SUCCESS : Constants.FAIL);
                batchOrder.setChannelBatchNo(respMap.get("instOrderNo"));
                batchOrder.setResultTime(LocalDateTime.now());
                success = iBaseBatchOrderService.updateById(batchOrder);
                if (success) {
                    success = iBaseTransferOrderService.update(new LambdaUpdateWrapper<BaseTransferOrder>()
                            .set(BaseTransferOrder::getTransStatus, batchOrder.getTransStatus())
                            .set(BaseTransferOrder::getResCode, batchOrder.getResCode())
                            .set(BaseTransferOrder::getResMsg, batchOrder.getResMsg())
                            .set(BaseTransferOrder::getResultTime, batchOrder.getResultTime())
                            .eq(BaseTransferOrder::getSysOrderNo, orderNo));
                }
                log.info("[QB核心系统--微支云统一下单--异步通知] [订单处理结果:{} , 系统单号:{}]", success, batchOrder.getSysOrderNo());
                if (success) {
                    asyncNotifyService(batchOrder);
                }
            }
        }
        //处理失败
        if (!StrUtil.equalsIgnoreCase(Constants.SUCCESS, batchOrder.getTransStatus()) && !StrUtil.equalsIgnoreCase(Constants.FAIL, batchOrder.getTransStatus()) && !StrUtil.equalsIgnoreCase("0000", resultCode)) {
            batchOrder.setResCode(StrUtil.equalsIgnoreCase("0000", resultCode) ? CodeEnum.SUCCESS.getResCode() : CodeEnum.BUS_FAIL.getResCode());
            batchOrder.setResMsg(StrUtil.equalsIgnoreCase("0000", resultCode) ? CodeEnum.SUCCESS.getResMsg() : respMap.get("resultMsg"));
            batchOrder.setTransStatus(StrUtil.equalsIgnoreCase("0000", resultCode) ? Constants.SUCCESS : Constants.FAIL);
            batchOrder.setChannelBatchNo(respMap.get("instOrderNo"));
            batchOrder.setResultTime(LocalDateTime.now());
            //处理订单
            success = iBaseBatchOrderService.updateById(batchOrder);
            if (success) {
                success = iBaseTransferOrderService.update(new LambdaUpdateWrapper<BaseTransferOrder>()
                        .set(BaseTransferOrder::getTransStatus, batchOrder.getTransStatus())
                        .set(BaseTransferOrder::getResCode, batchOrder.getResCode())
                        .set(BaseTransferOrder::getResMsg, batchOrder.getResMsg())
                        .set(BaseTransferOrder::getResultTime, batchOrder.getResultTime())
                        .eq(BaseTransferOrder::getSysOrderNo, orderNo));
            }
        }

        respMap.clear();
        return success;
    }

    @Override
    public boolean refundNotify(Map<String, String> respMap) {
        boolean success = false;

        String refundOrderNo = respMap.get("refundOrderNo");//平台退款单号
        String resultCode = respMap.get("resultCode");//响应码

        log.info("[QB核心系统--微支云统一下单--退款--异步通知] [系统订单号：{} , 响应状态：{}]", refundOrderNo, resultCode);
        BaseBatchOrder batchOrder = iBaseBatchOrderService.getBaseMapper().selectOne(new LambdaQueryWrapper<BaseBatchOrder>().eq(BaseBatchOrder::getSysOrderNo, refundOrderNo));
        if (ObjectUtil.isNull(batchOrder)) {
            log.error("[QB核心系统--微支云统一下单--退款--异步通知] [订单未查询到 , 退款订单号：{}]", refundOrderNo);
            return false;
        }

        //处理成功
        if (StrUtil.equalsIgnoreCase(Constants.DOING, batchOrder.getTransStatus()) && StrUtil.equalsIgnoreCase("0000", resultCode)) {
            //1.账务处理
            success = iAccountService.handleOnlineAccBill(batchOrder);
            log.info("[QB核心系统--微支云统一下单--退款--异步通知] [账务处理结果:{} , 系统单号:{}]", success, batchOrder.getSysOrderNo());
            if (success) {
                batchOrder.setResCode(StrUtil.equalsIgnoreCase("0000", resultCode) ? CodeEnum.SUCCESS.getResCode() : CodeEnum.BUS_FAIL.getResCode());
                batchOrder.setResMsg(StrUtil.equalsIgnoreCase("0000", resultCode) ? CodeEnum.SUCCESS.getResMsg() : respMap.get("resultMsg"));
                batchOrder.setTransStatus(StrUtil.equalsIgnoreCase("0000", resultCode) ? Constants.ONLINEREFUNDSUCCESS : Constants.ONLINREFUNDFAILED);
                batchOrder.setChannelBatchNo(respMap.get("instRefundOrderNo"));
                batchOrder.setResultTime(LocalDateTime.now());
                success = iBaseBatchOrderService.updateById(batchOrder);
                if (success) {
                    success = iBaseTransferOrderService.update(new LambdaUpdateWrapper<BaseTransferOrder>()
                            .set(BaseTransferOrder::getTransStatus, batchOrder.getTransStatus())
                            .set(BaseTransferOrder::getIsRefund, "1")
                            .set(BaseTransferOrder::getResCode, batchOrder.getResCode())
                            .set(BaseTransferOrder::getResMsg, batchOrder.getResMsg())
                            .set(BaseTransferOrder::getResultTime, batchOrder.getResultTime())
                            .eq(BaseTransferOrder::getSysOrderNo, refundOrderNo));
                    if (success) {
                        //设置原始订单退款状态为1
                        updateSourceOrder(batchOrder.getSysOrderNo());
                    }
                }
                log.info("[QB核心系统--微支云统一下单--退款--异步通知] [订单处理结果:{} , 系统单号:{}]", success, batchOrder.getSysOrderNo());
                if (success) {
                    asyncNotifyService(batchOrder);
                }
            }
        }
        //处理失败
        if (StrUtil.equalsIgnoreCase(Constants.DOING, batchOrder.getTransStatus()) && !StrUtil.equalsIgnoreCase("0000", resultCode)) {

            batchOrder.setResCode(StrUtil.equalsIgnoreCase("0000", resultCode) ? CodeEnum.SUCCESS.getResCode() : CodeEnum.BUS_FAIL.getResCode());
            batchOrder.setResMsg(StrUtil.equalsIgnoreCase("0000", resultCode) ? CodeEnum.SUCCESS.getResMsg() : respMap.get("resultMsg"));
            batchOrder.setTransStatus(StrUtil.equalsIgnoreCase("0000", resultCode) ? Constants.ONLINEREFUNDSUCCESS : Constants.ONLINREFUNDFAILED);
            batchOrder.setChannelBatchNo(respMap.get("instRefundOrderNo"));
            batchOrder.setResultTime(LocalDateTime.now());
            //处理订单
            success = iBaseBatchOrderService.updateById(batchOrder);
            if (success) {
                success = iBaseTransferOrderService.update(new LambdaUpdateWrapper<BaseTransferOrder>()
                        .set(BaseTransferOrder::getTransStatus, batchOrder.getTransStatus())
                        .set(BaseTransferOrder::getResCode, batchOrder.getResCode())
                        .set(BaseTransferOrder::getResMsg, batchOrder.getResMsg())
                        .set(BaseTransferOrder::getResultTime, batchOrder.getResultTime())
                        .eq(BaseTransferOrder::getBatchNo, refundOrderNo));
            }
        }

        respMap.clear();
        return success;
    }

    //异步通知下游
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

        if (StrUtil.equalsIgnoreCase("0", batchOrder.getTransType())) {
            iEmaNoticeDownService.notify(respStr, properties.getZxyUrl());
        } else {
            iEmaNoticeDownService.notify(respStr, properties.getZxyRefundUrl());
        }


        batchOrder = null;

    }

    private boolean updateSourceOrder(String sourceBatchNo) {

        List<BaseTransferOrder> orders = iBaseTransferOrderService.getBaseMapper().selectList(new LambdaQueryWrapper<BaseTransferOrder>()
                .eq(BaseTransferOrder::getSysOrderNo, sourceBatchNo));
        if (orders.isEmpty()) {
            return false;
        }

        List<String> ods = orders.stream().map(BaseTransferOrder::getSourceOrderNo).collect(Collectors.toList());
        List<BaseTransferOrder> sourceOrders = iBaseTransferOrderService.getBaseMapper().selectList(new LambdaQueryWrapper<BaseTransferOrder>()
                .in(BaseTransferOrder::getOrderNo, ods)
                .eq(BaseTransferOrder::getTransStatus, Constants.SUCCESS)
                .eq(BaseTransferOrder::getIsRefund, "0"));
        if (sourceOrders.isEmpty()) {
            return false;
        }

        sourceOrders.stream().forEach(e -> e.setIsRefund("1"));

        return iBaseTransferOrderService.updateBatchById(sourceOrders, sourceOrders.size());

    }


}
