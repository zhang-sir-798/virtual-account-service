package cn.com.finance.ema.service.impl;

import cn.com.finance.ema.config.EmaProperties;
import cn.com.finance.ema.constants.Constants;
import cn.com.finance.ema.dao.IAccountService;
import cn.com.finance.ema.dao.IBaseTransferOrderService;
import cn.com.finance.ema.enums.CodeEnum;
import cn.com.finance.ema.model.req.core.*;
import cn.com.finance.ema.model.resp.Result;
import cn.com.finance.ema.service.*;
import cn.com.finance.ema.utils.SignOlUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <p>
 * trade服务核心实现类
 * </p>
 *
 * @author zhangsir
 * @version v1.0.0
 * @since 2021/11/30 12:50
 */

@Slf4j
@Service
public class IEmaTradeCoreServiceImpl implements IEmaTradeCoreService {

    private final EmaProperties properties;
    private final IAccountService iAccountService;
    private final IEmaCheckService iEmaCheckService;
    private final IEmaNoticeDownService iEmaNoticeDownService;
    private final IEmaQueryChannelService iEmaQueryChannelService;
    private final IEmaTradeChannelService iEmaTradeChannelService;
    private final IBaseTransferOrderService iBaseTransferOrderService;

    public IEmaTradeCoreServiceImpl(IEmaQueryChannelService iEmaQueryChannelService, EmaProperties properties, IAccountService iAccountService, IEmaCheckService iEmaCheckService, IEmaNoticeDownService iEmaTradeNotifyService, IBaseTransferOrderService iBaseTransferOrderService, IEmaTradeChannelService iEmaTradeChannelService) {
        this.properties = properties;
        this.iAccountService = iAccountService;
        this.iEmaCheckService = iEmaCheckService;
        this.iEmaNoticeDownService = iEmaTradeNotifyService;
        this.iEmaTradeChannelService = iEmaTradeChannelService;
        this.iEmaQueryChannelService = iEmaQueryChannelService;
        this.iBaseTransferOrderService = iBaseTransferOrderService;
    }

    @Override
    public String onlinePay(String reqStr) {
        EmaOnlinePayReq req = parser(reqStr);
        //1.交易前置检查
        if (!iEmaCheckService.checkOnlineSplitPayInfo(req, reqStr)) {
            return Result.fail(req);
        }

        //2.建立交易流水
        if (!iBaseTransferOrderService.saveOnlineOrder(req)) {
            return Result.fail(req);
        }

        //3.提交支付指令
        if (!iEmaTradeChannelService.channelPay(req)) {
            return Result.fail(req);
        }

        //4.操作交易资金明细 - 渠道类产品放到异步通知里去做
        if (!iAccountService.handleOnlineAccBill(req)) {
            req.setResCode(CodeEnum.ILLEGAL_ACC_FAIL.getResCode());
            req.setResMsg(CodeEnum.ILLEGAL_ACC_FAIL.getResMsg());
            return Result.fail(req);
        }

        //6. 账单处理成功加签返回  自营 扣会员额度=更新订单为成功，走三方 不更新订单 回调时刻 更新订单
        return Result.success(req);

    }

    @Override
    public String onlineRefund(String reqStr) {
        EmaOnlineRefundReq req = JSONObject.parseObject(reqStr, EmaOnlineRefundReq.class);
        List<String> subProdOrders = JSONObject.parseArray(req.getSubProdTradeNos(), String.class);
        req.setSubProdOrders(subProdOrders);

        //1.退款前置检查
        if (!iEmaCheckService.checkOnlineRefundInfo(req, reqStr)) {
            return Result.fail(req);
        }
        //2.建立退款流水
        if (!iBaseTransferOrderService.saveOnlineRefundOrder(req)) {
            return Result.fail(req);
        }
        //3.渠道通讯 提交退款指令
        if (!iEmaTradeChannelService.channelRefund(req)) {
            return Result.fail(req);
        }
        //4.操作退款资金明细
        if (!iAccountService.handleOnlineRefundAccBill(req)) {
            req.setResCode(CodeEnum.ILLEGAL_ACC_FAIL.getResCode());
            req.setResMsg(CodeEnum.ILLEGAL_ACC_FAIL.getResMsg());
            return Result.fail(req);
        }

        //5.退款账单处理成功加签返回 自营=更新订单为成功，走三方 不更新订单 回调时刻 更新订单
        return Result.success(req);
    }

    @Override
    public String queryOnlineOrder(String reqStr) {
        EmaQueryOnlineReq req = JSONObject.parseObject(reqStr, EmaQueryOnlineReq.class);

        iEmaQueryChannelService.query(req, reqStr);

        return Result.success(req);
    }

    @Override
    public String notice(String reqStr) {
        EmaOnlineNoticeReq req = JSONObject.parseObject(reqStr, EmaOnlineNoticeReq.class);
        //1.交易前置检查
        if (!iEmaCheckService.checkNotice(req, reqStr)) {
            return Result.fail(req);
        }

        //2.发货时间为空 直接返回成功
        if (req.getOrder().getDeliveryTime() != null) {
            log.info("[发货通知][发货时间已经存在不再重复接受并返回成功 , 请求子订单号：{}]", req.getSubProdTradeNo());
            req.setResCode(CodeEnum.SUCCESS.getResCode());
            req.setResMsg(CodeEnum.SUCCESS.getResMsg());
            return Result.success(req);
        }

        //组装参数
        req.getOrder().setDeliveryTime(LocalDateTimeUtil.parse(req.getSendTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        //3.更新交易流水
        if (!iBaseTransferOrderService.updateOnlineSubOrder(req)) {
            return Result.fail(req);
        }

        return Result.success(req);
    }


    private EmaOnlinePayReq parser(String reqStr) {

        EmaOnlinePayReq req = new EmaOnlinePayReq();
        JSONObject reqBody = JSONObject.parseObject(reqStr);
        req.setBody(reqBody.getString("body"));
        req.setMerNo(reqBody.getString("merNo"));
        req.setAmount(reqBody.getString("amount"));
        req.setOrderNo(reqBody.getString("orderNo"));
        req.setAgentNo(reqBody.getString("agentNo"));
        req.setTrxType(reqBody.getString("trxType"));
        req.setOpenId(reqBody.getString("openId"));
        req.setSubOrders(reqBody.getString("subOrders"));
        req.setSign(reqBody.getString("sign"));

        List<EmaOrderReqSub> emaOnlinePayReqSub = JSONObject.parseArray(req.getSubOrders(), EmaOrderReqSub.class);

        for (EmaOrderReqSub product : emaOnlinePayReqSub) {
            List<EmaProductsReqSub> productList = JSONObject.parseArray(product.getProducts(), EmaProductsReqSub.class);
            product.setProductList(productList);
        }
        req.setEmaOnlinePayReqSub(emaOnlinePayReqSub);

        return req;
    }

    private void asyncNotifyService(EmaOnlinePayReq req) {

        if (StrUtil.equalsIgnoreCase("ONLINE001", req.getTrxType())) {

            JSONObject success = new JSONObject();
            success.put(Constants.CODE, CodeEnum.SUCCESS.getResCode());
            success.put(Constants.MSG, CodeEnum.SUCCESS.getResMsg());
            success.put("amount", req.getAmount());
            success.put("merNo", req.getMerNo());
            success.put("orderNo", req.getOrderNo());
            success.put("serialNo", req.getBaseBatchOrder().getSysOrderNo());
            success.put("payType", req.getTrxType());
            success.put(Constants.SIGN, SignOlUtil.encrypt(success.toJSONString(), req.getPublicKey()));
            String respStr = success.toJSONString();

            iEmaNoticeDownService.notify(respStr, "");

            success = null;
        }


    }

    private void asyncNotifyService(EmaOnlineRefundReq req) {

        if (StrUtil.equalsIgnoreCase("ONLINE001", req.getTrxType())) {

            JSONObject success = new JSONObject();
            success.put(Constants.CODE, CodeEnum.SUCCESS.getResCode());
            success.put(Constants.MSG, CodeEnum.SUCCESS.getResMsg());
            success.put("amount", req.getTotalAmount());
            success.put("merNo", req.getMerNo());
            success.put("orderNo", req.getOrderNo());
            success.put("serialNo", req.getSerialNo());
            success.put(Constants.SIGN, SignOlUtil.encrypt(success.toJSONString(), req.getPublicKey()));
            String respStr = success.toJSONString();

            iEmaNoticeDownService.notify(respStr, req.getRefundNotifyUrl());

            success = null;
        }


    }

}
