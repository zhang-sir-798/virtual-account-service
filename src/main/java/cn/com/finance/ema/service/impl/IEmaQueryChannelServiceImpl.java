package cn.com.finance.ema.service.impl;

import cn.com.finance.ema.config.EmaProperties;
import cn.com.finance.ema.constants.Constants;
import cn.com.finance.ema.dao.IAccountService;
import cn.com.finance.ema.dao.IBaseBatchOrderService;
import cn.com.finance.ema.dao.IBaseTransferOrderService;
import cn.com.finance.ema.enums.CodeEnum;
import cn.com.finance.ema.model.entity.BaseBatchOrder;
import cn.com.finance.ema.model.entity.BaseTransferOrder;
import cn.com.finance.ema.model.req.core.EmaQueryOnlineReq;
import cn.com.finance.ema.service.IAcContextService;
import cn.com.finance.ema.service.IEmaQueryChannelService;
import cn.com.finance.ema.utils.HttpUtil;
import cn.com.finance.ema.utils.SignOlUtil;
import cn.com.finance.ema.utils.channel.RsaUtils;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * <p>
 * 处理交易确认服务类
 * </p>
 *
 * @author zhangsir
 * @version v1.0.0
 * @since 2022/10/26 18:34
 */
@Slf4j
@Service
public class IEmaQueryChannelServiceImpl implements IEmaQueryChannelService {

    @Autowired
    private EmaProperties properties;

    @Autowired
    private IAccountService iAccountService;
    @Autowired
    private IAcContextService iAcContextService;
    @Autowired
    private IBaseBatchOrderService iBaseBatchOrderService;
    @Autowired
    private IBaseTransferOrderService iBaseTransferOrderService;

    @Override
    public void query(EmaQueryOnlineReq req, String reqStr) {

        BaseBatchOrder order = iBaseBatchOrderService.getBaseMapper().selectOne(new LambdaQueryWrapper<BaseBatchOrder>().eq(BaseBatchOrder::getBatchNo, req.getOrderNo()));
        if (ObjectUtil.isNull(order)) {
            log.error("[查询请求][BaseBatchOrder订单查询结果为空 , 请求参数：{}]", req);
            req.setResCode(CodeEnum.ILLEGAL_PARAM.getResCode());
            req.setResMsg(CodeEnum.ILLEGAL_PARAM.getResMsg() + " , 单号有误请核实");
            req.setOrderStatus(Constants.UNKONW);
            return;
        }
        req.setMerNo(order.getSuperMerNo());
        req.setPublicKey(order.getReqKey());

        //验签
        if (!SignOlUtil.verify(reqStr, order.getReqKey())) {
            log.error("[查询请求][前置检查未通过][签名验证失败 , 请求参数：{}]", req);
            req.setResCode(CodeEnum.SIGN_ERROR.getResCode());
            req.setResMsg(CodeEnum.SIGN_ERROR.getResMsg());
            req.setOrderStatus(Constants.FAIL);
            return;
        }

        //本地成功
        if (StrUtil.equalsIgnoreCase(Constants.SUCCESS, order.getTransStatus()) || StrUtil.equalsIgnoreCase(Constants.FAIL, order.getTransStatus()) || StrUtil.equalsIgnoreCase(Constants.ONLINEREFUNDSUCCESS, order.getTransStatus()) || StrUtil.equalsIgnoreCase(Constants.ONLINREFUNDFAILED, order.getTransStatus())) {
            //终态直接返回
            req.setResCode(order.getResCode());
            req.setResMsg(order.getResMsg());
            req.setOrderStatus(order.getTransStatus());
            req.setSerialNo(order.getSysOrderNo());
            log.info("[查询请求][本地终态直接返回 -> 状态status：{}, , 系统单号：{}]", req.getOrderStatus(), req.getSerialNo());
            return;
        }

        //查询上游
        switch (order.getTransType()) {
            case Constants.TRANS_TYPE_PAY:
                tradeQuery(req, order);
                return;
            case Constants.TRANS_TYPE_REFUND:
                refundQuery(req, order);
                return;
            default:
                return;
        }

    }

    private void tradeQuery(EmaQueryOnlineReq req, BaseBatchOrder order) {

        Map<String, String> paramMap = MapUtil.newHashMap();
        paramMap.put("version", "V1.0");
        paramMap.put("merchantNo", req.getMerNo());// QB分配的商户编号
        paramMap.put("orderNo", order.getSysOrderNo());
        paramMap.put("signType", "RSA2");
        paramMap.put("digest", RsaUtils.sign(paramMap, properties.getQbsPrivateKey(), "RSA2"));

        log.info("[QB微支云--统一下单查询] 请求参数：{} , 请求地址：{}", paramMap, properties.getQbsBaseUrl() + "/mas/order/query.do");
        String resp = HttpUtil.doPostRequestFrom(properties.getQbsBaseUrl() + "/mas/order/query.do", null, paramMap);
        log.info("[QB微支云--统一下单查询] 返回参数：{}", resp);
        if (StrUtil.isEmpty(resp)) {
            req.setSerialNo(order.getSysOrderNo());
            req.setResCode(CodeEnum.BUS_FAIL.getResCode());
            req.setResCode(CodeEnum.BUS_FAIL.getResMsg());
            req.setOrderStatus(Constants.FAIL);
            return;
        }
        //------mock-------
//        Map<String, String> respMap = MapUtil.newHashMap();
//        respMap.put("resultCode", "0000");
//        log.info("[QB微支云--统一下单查询--mock 响应参数==] 返回参数：{}", respMap);
        //------mock------
        //验签
        Map<String, String> respMap = JSONObject.parseObject(resp, Map.class);
        if (!RsaUtils.verify(respMap, properties.getQbsPublicKey(), "RSA2", respMap.get("digest"))) {
            req.setSerialNo(order.getSysOrderNo());
            req.setResCode(CodeEnum.BUS_FAIL.getResCode());
            req.setResCode(CodeEnum.BUS_FAIL.getResMsg());
            req.setOrderStatus(Constants.FAIL);
            return;
        }
        String resultCode = respMap.get("resultCode");// 响应码
        if ("0000".equals(resultCode)) {
            req.setOrderStatus(Constants.SUCCESS);
            order.setResultTime(LocalDateTime.now());
            order.setResCode(CodeEnum.SUCCESS.getResCode());
            order.setResMsg(CodeEnum.SUCCESS.getResMsg());
            order.setTransStatus(req.getOrderStatus());
            req.setResCode(CodeEnum.SUCCESS.getResCode());
            req.setResMsg(CodeEnum.SUCCESS.getResMsg());
            updateOrder(order);
            //1.自身账务处理 - 确认兼容HF QB
            boolean success = iAccountService.handleOnlineAccBill(order);
            log.info("[QB微支云--统一下单查询] [账务处理结果:{} , 系统单号:{}]", success, order.getSysOrderNo());
        } else if ("".equals(resultCode)) {// 支付中
            req.setOrderStatus(Constants.DOING);
            req.setResCode(CodeEnum.BUS_TRUE.getResCode());
            req.setResMsg(CodeEnum.BUS_TRUE.getResMsg());
        } else if ("9997".equals(resultCode)) {// 未确定
            req.setOrderStatus(Constants.UNKONW);
            req.setResCode(CodeEnum.BUS_NU.getResCode());
            req.setResMsg(CodeEnum.BUS_NU.getResMsg());
        } else if ("0028".equals(resultCode)) {// 找不到原笔交易(用户未扫码)还未和上游交互
            req.setOrderStatus(Constants.INIT);
            req.setResCode(CodeEnum.NONE.getResCode());
            req.setResMsg(CodeEnum.NONE.getResMsg());
        } else {// 失败
            req.setOrderStatus(Constants.FAIL);
            order.setResultTime(LocalDateTime.now());
            order.setResCode(CodeEnum.BUS_FAIL.getResCode());
            order.setResMsg(CodeEnum.BUS_FAIL.getResMsg());
            order.setTransStatus(req.getOrderStatus());
            updateOrder(order);
        }

        req.setSerialNo(order.getSysOrderNo());

    }

    private void refundQuery(EmaQueryOnlineReq req, BaseBatchOrder order) {

        Map<String, String> paramMap = MapUtil.newHashMap();
        paramMap.put("version", "V1.0");
        paramMap.put("merchantNo", req.getMerNo());
        paramMap.put("refundOrderNo", order.getSysOrderNo());
        paramMap.put("instRefundOrderNo", order.getChannelBatchNo());
        paramMap.put("signType", "RSA2");
        paramMap.put("digest", RsaUtils.sign(paramMap, properties.getQbsPrivateKey(), "RSA2"));

        log.info("[QB微支云--统一下单退款] 请求参数：{} , 请求地址：{}", paramMap, properties.getQbsBaseUrl() + "/mas/order/query_refund.do");
        String resp = HttpUtil.doPostRequestFrom(properties.getQbsBaseUrl() + "/mas/order/query_refund.do", null, paramMap);
        log.info("[QB微支云--统一下单退款] 返回参数：{}", resp);
        if (StrUtil.isEmpty(resp)) {
            req.setSerialNo(order.getSysOrderNo());
            req.setResCode(CodeEnum.BUS_FAIL.getResCode());
            req.setResCode(CodeEnum.BUS_FAIL.getResMsg());
            req.setOrderStatus(Constants.ONLINREFUNDFAILED);
            return;
        }
        //----mock----
//        Map<String, String> respMap = MapUtil.newHashMap();
//        respMap.put("resultCode", "0000");
//        log.info("[QB微支云--统一下单退款--mock响应参数] 返回参数：{}", respMap);
        //----mock----
        //验签
        Map<String, String> respMap = JSONObject.parseObject(resp, Map.class);
        if (!RsaUtils.verify(respMap, properties.getQbsPublicKey(), "RSA2", respMap.get("digest"))) {
            req.setSerialNo(order.getSysOrderNo());
            req.setResCode(CodeEnum.BUS_FAIL.getResCode());
            req.setResCode(CodeEnum.BUS_FAIL.getResMsg());
            req.setOrderStatus(Constants.ONLINREFUNDFAILED);
            return;
        }
        String resultCode = respMap.get("resultCode");// 响应码
        if ("0000".equals(resultCode)) {
            req.setOrderStatus(Constants.ONLINEREFUNDSUCCESS);
            order.setResultTime(LocalDateTime.now());
            order.setResCode(CodeEnum.SUCCESS.getResCode());
            order.setResMsg(CodeEnum.SUCCESS.getResMsg());
            order.setTransStatus(req.getOrderStatus());
            req.setResCode(CodeEnum.SUCCESS.getResCode());
            req.setResMsg(CodeEnum.SUCCESS.getResMsg());
            updateOrder(order, "1");
            //1.自身账务处理 - 确认兼容HF QB
            boolean success = iAccountService.handleOnlineAccBill(order);
            log.info("[QB微支云--统一下单查询] [账务处理结果:{} , 系统单号:{}]", success, order.getSysOrderNo());
            //2.设置原始订单退款状态为1
            updateSourceOrder(order.getSysOrderNo());
        } else if ("9997".equals(resultCode)) {// 未确定
            req.setOrderStatus(Constants.UNKONW);
            req.setResCode(CodeEnum.BUS_NU.getResCode());
            req.setResMsg(CodeEnum.BUS_NU.getResMsg());
        } else if ("0028".equals(resultCode)) {// 找不到原笔交易(用户未扫码)还未和上游交互
            req.setOrderStatus(Constants.INIT);
            req.setResCode(CodeEnum.NONE.getResCode());
            req.setResMsg(CodeEnum.NONE.getResMsg());
        } else {// 失败
            req.setOrderStatus(Constants.ONLINREFUNDFAILED);
            req.setResCode(CodeEnum.BUS_FAIL.getResCode());
            req.setResMsg(CodeEnum.BUS_FAIL.getResMsg());
            order.setResultTime(LocalDateTime.now());
            order.setResCode(CodeEnum.BUS_FAIL.getResCode());
            order.setResMsg(CodeEnum.BUS_FAIL.getResMsg());
            order.setTransStatus(req.getOrderStatus());
            updateOrder(order);
        }

        req.setSerialNo(order.getSysOrderNo());
    }

    private void updateOrder(BaseBatchOrder batchOrder) {

        iBaseBatchOrderService.updateById(batchOrder);
        iBaseTransferOrderService.update(new LambdaUpdateWrapper<BaseTransferOrder>()
                .set(BaseTransferOrder::getTransStatus, batchOrder.getTransStatus())
                .set(BaseTransferOrder::getResCode, batchOrder.getResCode())
                .set(BaseTransferOrder::getResMsg, batchOrder.getResMsg())
                .set(BaseTransferOrder::getResultTime, batchOrder.getResultTime())
                .eq(BaseTransferOrder::getSysOrderNo, batchOrder.getSysOrderNo()));

    }

    private boolean updateSourceOrder(String batchSysOrderNo) {

        List<BaseTransferOrder> orders = iBaseTransferOrderService.getBaseMapper().selectList(new LambdaQueryWrapper<BaseTransferOrder>().eq(BaseTransferOrder::getSysOrderNo, batchSysOrderNo));
        if (orders.isEmpty()) {
            return false;
        }

        List<String> ods = orders.stream().map(BaseTransferOrder::getSourceOrderNo).collect(toList());
        List<BaseTransferOrder> sourceOrders = iBaseTransferOrderService.getBaseMapper().selectList(new LambdaQueryWrapper<BaseTransferOrder>().in(BaseTransferOrder::getOrderNo, ods).eq(BaseTransferOrder::getTransStatus, Constants.SUCCESS).eq(BaseTransferOrder::getIsRefund, "0"));
        if (orders.isEmpty()) {
            return false;
        }

        sourceOrders.stream().forEach(e -> e.setIsRefund("1"));

        return iBaseTransferOrderService.updateBatchById(sourceOrders, sourceOrders.size());

    }

    private void updateOrder(BaseBatchOrder batchOrder, String isRefund) {

        iBaseBatchOrderService.updateById(batchOrder);
        iBaseTransferOrderService.update(new LambdaUpdateWrapper<BaseTransferOrder>()
                .set(BaseTransferOrder::getTransStatus, batchOrder.getTransStatus())
                .set(BaseTransferOrder::getResultTime, batchOrder.getResultTime())
                .set(BaseTransferOrder::getResCode, batchOrder.getResCode())
                .set(BaseTransferOrder::getResMsg, batchOrder.getResMsg())
                .set(BaseTransferOrder::getIsRefund, isRefund)
                .eq(BaseTransferOrder::getSysOrderNo, batchOrder.getSysOrderNo()));

    }
}
