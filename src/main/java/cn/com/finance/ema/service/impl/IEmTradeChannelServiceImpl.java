package cn.com.finance.ema.service.impl;

import cn.com.finance.ema.config.EmaProperties;
import cn.com.finance.ema.constants.Constants;
import cn.com.finance.ema.dao.*;
import cn.com.finance.ema.enums.CodeEnum;
import cn.com.finance.ema.model.req.core.EmaOnlinePayReq;
import cn.com.finance.ema.model.req.core.EmaOnlineRefundReq;
import cn.com.finance.ema.service.IAcContextService;
import cn.com.finance.ema.service.IEmaTradeChannelService;
import cn.com.finance.ema.utils.HttpUtil;
import cn.com.finance.ema.utils.channel.RsaUtils;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

/**
 * <p>
 * 前置检查服务
 * </p>
 *
 * @author zhangsir
 * @version v1.0.0
 * @since 2021/12/2 13:26
 */
@Slf4j
@Service
public class IEmTradeChannelServiceImpl implements IEmaTradeChannelService {

    private final EmaProperties properties;
    private final IGoodsService iGoodsService;
    private final IPersonService iPersonService;
    private final IAccountService iAccountService;
    private final IProductService iProductService;
    private final IPlatformService iPlatformService;
    private final IMerchantService iMerchantService;
    private final IAcContextService iAcContextService;
    private final IMerchantFeeService iMerchantFeeService;
    private final IPlatformFeeService iPlatformFeeService;
    private final IBaseBatchOrderService iBaseBatchOrderService;

    private final IBaseTransferOrderService iBaseTransferOrderService;

    public IEmTradeChannelServiceImpl(EmaProperties properties, IGoodsService iGoodsService, IPersonService iPersonService, IAccountService iAccountService, IProductService iProductService, IMerchantService iMerchantService, IAcContextService iAcContextService, IMerchantFeeService iMerchantFeeService, IPlatformFeeService iPlatformFeeService, IBaseTransferOrderService iBaseTransferOrderService, IBaseBatchOrderService iBaseBatchOrderService, IPlatformService iPlatformService) {
        this.properties = properties;
        this.iGoodsService = iGoodsService;
        this.iPersonService = iPersonService;
        this.iProductService = iProductService;
        this.iPlatformService = iPlatformService;
        this.iAccountService = iAccountService;
        this.iMerchantService = iMerchantService;
        this.iAcContextService = iAcContextService;
        this.iMerchantFeeService = iMerchantFeeService;
        this.iPlatformFeeService = iPlatformFeeService;
        this.iBaseBatchOrderService = iBaseBatchOrderService;
        this.iBaseTransferOrderService = iBaseTransferOrderService;
    }


    @Override
    public boolean channelPay(EmaOnlinePayReq req) {
        boolean success;

        String channel = req.getChannelNo();
        switch (channel) {
            case "QB":
                //余额类
                if (StrUtil.equalsIgnoreCase("ONLINE001", req.getTrxType())) {
                    req.setResCode(CodeEnum.SUCCESS.getResCode());
                    req.setResMsg(CodeEnum.SUCCESS.getResMsg());
                    req.setTransStatus(Constants.SUCCESS);
                    req.setTradeNo(req.getBaseBatchOrder().getSysOrderNo());
                    success = true;
                    req.setRecorded(true);
                    break;
                } else {
                    //微支云
                    success = payChannel(req);
                    req.setRecorded(false);
                    break;
                }
            default:
                req.setResCode(CodeEnum.BUS_FAIL.getResCode());
                req.setResMsg(CodeEnum.BUS_FAIL.getResMsg());
                req.setTransStatus(Constants.FAIL);
                success = false;
        }
        return success;

    }


    private boolean payChannel(EmaOnlinePayReq req) {
        try {
            Map<String, String> paramMap = MapUtil.newHashMap();
            String productId = "";//上送QB产品码
            // 支付宝
            if (Constants.ProdCode.Z001.equals(req.getTrxType())) {
                productId = "A03";
            } else if (Constants.ProdCode.OFFICIAL.equals(req.getTrxType())) {
                productId = "W02";
            } else if (Constants.ProdCode.APPLET.equals(req.getTrxType())) {
                productId = "W06";
            } else if (Constants.ProdCode.U002.equals(req.getTrxType())) {
                productId = "U05";
            }
            paramMap.put("version", "V1.0");
            paramMap.put("merchantNo", req.getMerNo());// QB分配的商户编号 大商户编号 下游直接上送
            paramMap.put("orderNo", req.getBaseBatchOrder().getSysOrderNo());//批次表  批次号 = 上送QB订单
            paramMap.put("productId", productId);// -微信公众号
            paramMap.put("orderDate", new SimpleDateFormat("yyyyMMdd").format(new Date()));
            paramMap.put("notifyUrl", properties.getQbsTradeNotifyUrl());
            paramMap.put("orderAmount", req.getBaseBatchOrder().getTotalAmount());// 交易金额：以分为单位
            paramMap.put("goodsName", StrUtil.isEmpty(req.getBody()) ? "下单" : req.getBody());
            paramMap.put("userOpenid", req.getOpenId());
            paramMap.put("signType", "RSA2");
            paramMap.put("digest", RsaUtils.sign(paramMap, properties.getQbsPrivateKey(), "RSA2"));

            log.info("[QB微支云--统一下单] 请求参数：{} , 请求地址：{}", paramMap, properties.getQbsBaseUrl() + "/mas/unitorder/pay.do");
            String resp = HttpUtil.doPostRequestFrom(properties.getQbsBaseUrl() + "/mas/unitorder/pay.do", null, paramMap);
            log.info("[QB微支云--统一下单] 响应参数：{}", resp);

            //-------------mock---------------
//            JSONObject js = new JSONObject();
//            js.put("merchantNo", "666110073720004");
//            js.put("resultCode", "0000");
//            js.put("resultMsg", "交易成功");
//            js.put("digest", "1");
//            js.put("instOrderNo", "QBS" + IdGenerator.nextId());
//            js.put("tradeNo", "TNO" + IdGenerator.nextId());
//            js.put("payInfo", js.getString("tradeNo"));
//            js.put("goodsName", "交易");
//            js.put("productId", "U05 ");
//            js.put("orderNo", paramMap.get("orderNo"));
//            js.put("orderAmount", paramMap.get("orderAmount"));
//            String resp = js.toJSONString();
            //-------------mock---------------

            if (StrUtil.isEmpty(resp)) {
                req.setResCode(CodeEnum.BUS_FAIL.getResCode());
                req.setResMsg(CodeEnum.BUS_FAIL.getResMsg());
                req.setTransStatus(Constants.FAIL);

                req.getBaseBatchOrder().setTransStatus(req.getTransStatus());
                req.getBaseBatchOrder().setResCode(req.getResCode());
                req.getBaseBatchOrder().setResMsg(req.getResMsg());
                iBaseBatchOrderService.updateById(req.getBaseBatchOrder());
                req.getBaseTransferOrders().stream().forEach(e -> {
                    e.setTransStatus(req.getTransStatus());
                    e.setResCode(req.getResCode());
                    e.setResMsg(req.getResMsg());
                    e.setResultTime(LocalDateTime.now());
                });
                iBaseTransferOrderService.updateBatchById((req.getBaseTransferOrders()));
                log.info("[QB微支云--统一下单] 响应参数为空");
                return false;
            }
            Map<String, String> respMap = JSONObject.parseObject(resp, Map.class);
            //验签
            if (!RsaUtils.verify(respMap, properties.getQbsPublicKey(), "RSA2", respMap.get("digest"))) {
                req.setResCode(CodeEnum.BUS_FAIL.getResCode());
                req.setResMsg(CodeEnum.BUS_FAIL.getResMsg());
                req.setTransStatus(Constants.FAIL);
                return false;
            }
            String resultCode = respMap.get("resultCode") + "";// 响应码
            if (!StrUtil.equalsIgnoreCase("0000", resultCode)) {
                req.setResCode(respMap.get("resultCode"));
                req.setResMsg(respMap.get("resultMsg"));
                req.setTransStatus(Constants.FAIL);
                req.getBaseBatchOrder().setTransStatus(req.getTransStatus());
                req.getBaseBatchOrder().setResCode(req.getResCode());
                req.getBaseBatchOrder().setResMsg(req.getResMsg());
                req.getBaseBatchOrder().setChannelBatchNo(respMap.get("instRefundOrderNo"));
                iBaseBatchOrderService.updateById(req.getBaseBatchOrder());
                req.getBaseTransferOrders().stream().forEach(e -> {
                    e.setTransStatus(req.getTransStatus());
                    e.setResCode(req.getResCode());
                    e.setResMsg(req.getResMsg());
                    e.setResultTime(LocalDateTime.now());
                });
                iBaseTransferOrderService.updateBatchById((req.getBaseTransferOrders()));
                log.info("[QB微支云--统一下单 , 返回失败 -> 响应信息:{} 系统单号:{}", req.getResMsg(), req.getBaseBatchOrder().getSysOrderNo());
                paramMap.clear();
                return false;
            }

            // 判读是什么类型支付 返回对应的响应参数 A03-支付窗支付 W02-微信公众号支付 U05-银联云闪付(ApplePay) W06-微信小程序
            if (Constants.channelProd.A03.equals(productId)) {
                req.setTradeNo(respMap.get("tradeNo"));
            } else if (Constants.channelProd.W02.equals(productId)
                    || Constants.channelProd.W06.equals(productId)) {
                req.setPayInfo(respMap.get("payInfo"));
            } else if (Constants.channelProd.U05.equals(productId)) {
                req.setTradeNo(respMap.get("tradeNo"));
                req.setPayInfo(req.getTradeNo());
            }
            req.setResCode(CodeEnum.SUCCESS.getResCode());
            req.setResMsg(CodeEnum.SUCCESS.getResCode());
            log.info("[QB微支云--统一下单 , 成功 -> TradeNo:{} 系统单号:{}", req.getTradeNo(), req.getBaseBatchOrder().getSysOrderNo());
            paramMap.clear();
        } catch (Exception e) {
            log.error(e.getMessage());
            req.setResCode(CodeEnum.SYSTEM_ERROR.getResCode());
            req.setResMsg(CodeEnum.SYSTEM_ERROR.getResMsg());
            req.setTransStatus(Constants.ERROR);
            return false;
        }

        req.setResCode(CodeEnum.SUCCESS.getResCode());
        req.setResMsg(CodeEnum.SUCCESS.getResMsg());
        req.setTransStatus(Constants.DOING);
        return true;
    }


    @Override
    public boolean channelRefund(EmaOnlineRefundReq req) {
        boolean success;
        String channel = req.getChannelNo();

        switch (channel) {
            case "QB":
                if (StrUtil.equalsIgnoreCase("ONLINE001", req.getTrxType())) {
                    req.setResCode(CodeEnum.SUCCESS.getResCode());
                    req.setResMsg(CodeEnum.SUCCESS.getResMsg());
                    req.setTransStatus(Constants.ONLINEREFUNDSUCCESS);
                    success = true;
                    req.setRecorded(true);
                    break;
                } else {
                    success = refundChannel(req);
                    req.setRecorded(false);
                    break;
                }

            default:
                req.setResCode(CodeEnum.BUS_FAIL.getResCode());
                req.setResMsg(CodeEnum.BUS_FAIL.getResMsg());
                req.setTransStatus(Constants.ONLINREFUNDFAILED);
                success = false;
        }
        return success;
    }

    private boolean refundChannel(EmaOnlineRefundReq req) {
        try {

            Map<String, String> map = MapUtil.newHashMap();
            map.put("version", "V1.0");
            map.put("merchantNo", req.getMerNo());
            map.put("paymentOrderNo", req.getBaseBatchOrder().getSourceBatchNo());
            map.put("paymentInstOrderNo", req.getBaseBatchOrder().getChannelBatchNo());
            map.put("refundOrderNo", req.getBaseBatchOrder().getSysOrderNo());
            map.put("currency", "CNY");
            map.put("refundAmount", req.getBaseBatchOrder().getTotalAmount());
            map.put("notifyUrl", properties.getQbsRefundNotifyUrl());
            map.put("signType", "RSA2");
            map.put("digest", RsaUtils.sign(map, properties.getQbsPrivateKey(), "RSA2"));
            log.info("[QB微支云--统一下单--退款] 请求参数：{} , 请求地址：{}", map, properties.getQbsBaseUrl() + "/mas/order/refund.do");
            String resp = HttpUtil.doPostRequestFrom(properties.getQbsBaseUrl() + "/mas/order/refund.do", null, map);
            log.info("[QB微支云--统一下单--退款] 返回参数：{}", resp);

            //----------mock--------------
//            JSONObject js = new JSONObject();
//            js.put("merchantNo", "666110073720004");
//            js.put("resultCode", "0000");
//            js.put("resultMsg", "交易成功");
//            js.put("digest", "1");
//            js.put("paymentOrderNo", "paymentOrderNo" + IdGenerator.nextId());
//            js.put("paymentInstOrderNo", "paymentOrderNo" + IdGenerator.nextId());
//            js.put("instRefundOrderNo", "instRefundOrderNo" + IdGenerator.nextId());
//            js.put("refundOrderNo", map.get("refundOrderNo"));
//            String resp = js.toJSONString();
//            log.info("[QB微支云--统一下单--退款--mock响应参数==] 返回参数：{}", resp);
            //----------mock--------------
            if (StrUtil.isEmpty(resp)) {
                req.setResCode(CodeEnum.BUS_FAIL.getResCode());
                req.setResMsg(CodeEnum.BUS_FAIL.getResMsg());
                req.setTransStatus(Constants.FAIL);
                return false;
            }
            Map<String, String> respMap = JSONObject.parseObject(resp, Map.class);
            //验签
            if (!RsaUtils.verify(respMap, properties.getQbsPublicKey(), "RSA2", respMap.get("digest"))) {
                req.setResCode(CodeEnum.BUS_FAIL.getResCode());
                req.setResMsg(CodeEnum.BUS_FAIL.getResMsg());
                req.setTransStatus(Constants.FAIL);
                return false;
            }
            String resultCode = respMap.get("resultCode");// 响应码
            switch (resultCode) {
                case "0000":
                    req.setResCode(CodeEnum.SUCCESS.getResCode());
                    req.setResMsg(CodeEnum.SUCCESS.getResMsg());
                    req.setTransStatus(Constants.DOING);
                    log.info("[QB微支云--统一下单--退款 , 成功 -> 系统单号:{}", req.getBaseBatchOrder().getSysOrderNo());
                    map.clear();
                    return true;
                case "9997":
                    req.setResCode(CodeEnum.BUS_TRUE.getResCode());
                    req.setResMsg(CodeEnum.BUS_TRUE.getResMsg());
                    req.setTransStatus(Constants.DOING);
                    map.clear();
                    return true;
                default:
                    req.setResCode(CodeEnum.BUS_FAIL.getResCode());
                    req.setResMsg(CodeEnum.BUS_FAIL.getResMsg());
                    req.setTransStatus(Constants.ONLINREFUNDFAILED);

                    req.getBaseBatchOrder().setTransStatus(req.getTransStatus());
                    req.getBaseBatchOrder().setResCode(req.getResCode());
                    req.getBaseBatchOrder().setResMsg(req.getResMsg());
                    req.setRemark("QB错误信息:" + respMap.get("resultMsg"));
                    req.getBaseBatchOrder().setChannelBatchNo(respMap.get("instRefundOrderNo"));
                    iBaseBatchOrderService.updateById(req.getBaseBatchOrder());
                    req.getBaseTransferOrders().stream().forEach(e -> {
                        e.setTransStatus(req.getTransStatus());
                        e.setResCode(req.getResCode());
                        e.setResMsg(req.getResMsg());
                        e.setResultTime(LocalDateTime.now());
                    });
                    iBaseTransferOrderService.updateBatchById((req.getBaseTransferOrders()));
                    log.info("[QB微支云--统一下单--退款 , 处理失败 -> 响应信息:{} 系统单号:{}", req.getResMsg(), req.getBaseBatchOrder().getSysOrderNo());
                    return false;
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            req.setResCode(CodeEnum.SYSTEM_ERROR.getResCode());
            req.setResMsg(CodeEnum.SYSTEM_ERROR.getResMsg());
            req.setTransStatus(Constants.ERROR);
            return false;
        }

    }

}
