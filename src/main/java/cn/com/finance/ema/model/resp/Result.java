package cn.com.finance.ema.model.resp;


import cn.com.finance.ema.constants.Constants;
import cn.com.finance.ema.enums.CodeEnum;
import cn.com.finance.ema.model.req.core.EmaOnlineNoticeReq;
import cn.com.finance.ema.model.req.core.EmaOnlinePayReq;
import cn.com.finance.ema.model.req.core.EmaOnlineRefundReq;
import cn.com.finance.ema.model.req.core.EmaQueryOnlineReq;
import cn.com.finance.ema.utils.SignOlUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;


@Slf4j
public class Result implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功
     *
     * @param req
     * @return String
     */
    public static String success(EmaOnlinePayReq req) {

        JSONObject success = new JSONObject();
        success.put(Constants.CODE, CodeEnum.SUCCESS.getResCode());
        success.put(Constants.MSG, CodeEnum.SUCCESS.getResMsg());
        if (StrUtil.isNotEmpty(req.getPayInfo())) {
            success.put("payInfo", req.getPayInfo());
        }

        if (StrUtil.isNotEmpty(req.getTradeNo())) {
            success.put("tradeNo", req.getTradeNo());
        }
        success.put(Constants.SIGN, SignOlUtil.encrypt(success.toJSONString(), req.getPublicKey()));

        String respStr = success.toJSONString();
        log.info("[下单请求接口] resp响应参数：{}", respStr);

        req = null;
        return respStr;
    }

    public static String success(EmaOnlineRefundReq req) {

        JSONObject success = new JSONObject();
        success.put(Constants.CODE, CodeEnum.SUCCESS.getResCode());
        success.put(Constants.MSG, CodeEnum.SUCCESS.getResMsg());
        success.put("serialNo", req.getSerialNo());
        success.put("orderNo", req.getOrderNo());
        success.put("merNo", req.getMerNo());
        success.put("agentNo",req.getAgentNo());
        success.put(Constants.SIGN, SignOlUtil.encrypt(success.toJSONString(), req.getPublicKey()));

        String respStr = success.toJSONString();
        log.info("[退款请求接口] resp响应参数：{}", respStr);
        req = null;
        return respStr;
    }

    public static String success(EmaQueryOnlineReq req) {

        JSONObject success = new JSONObject();
        success.put(Constants.CODE, req.getResCode());
        success.put(Constants.MSG, req.getResMsg());
        success.put("merNo", req.getMerNo());
        success.put("orderNo", req.getOrderNo());
        success.put("serialNo", req.getSerialNo());
        success.put("orderStatus", req.getOrderStatus());
        success.put(Constants.SIGN, SignOlUtil.encrypt(success.toJSONString(), req.getPublicKey()));

        String respStr = success.toJSONString();
        log.info("[查询请求接口] resp响应参数：{}", respStr);
        req = null;
        return respStr;
    }

    public static String success(EmaOnlineNoticeReq req) {

        JSONObject success = new JSONObject();
        success.put(Constants.CODE, req.getResCode());
        success.put(Constants.MSG, req.getResMsg());
        success.put("subProdTradeNo", req.getSubProdTradeNo());
        success.put(Constants.SIGN, SignOlUtil.encrypt(success.toJSONString(), req.getPublicKey()));

        String respStr = success.toJSONString();
        log.info("[发货时间接口] resp响应参数：{}", respStr);
        req = null;
        return respStr;
    }

    public static String fail(EmaOnlinePayReq req) {
        JSONObject fail = new JSONObject();
        fail.put(Constants.CODE, req.getResCode());
        fail.put(Constants.MSG, req.getResMsg());
        fail.put(Constants.SIGN, SignOlUtil.encrypt(fail.toJSONString(), req.getPublicKey()));
        return fail.toJSONString();
    }

    public static String fail(EmaOnlineRefundReq req) {
        JSONObject fail = new JSONObject();
        fail.put(Constants.CODE, req.getResCode());
        fail.put(Constants.MSG, req.getResMsg());
        fail.put(Constants.SIGN, SignOlUtil.encrypt(fail.toJSONString(), req.getPublicKey()));
        return fail.toJSONString();
    }

    public static String fail(EmaOnlineNoticeReq req) {
        JSONObject fail = new JSONObject();
        fail.put(Constants.CODE, req.getResCode());
        fail.put(Constants.MSG, req.getResMsg());
        fail.put(Constants.SIGN, SignOlUtil.encrypt(fail.toJSONString(), req.getPublicKey()));
        return fail.toJSONString();
    }

    public static String error(String errMsg) {
        JSONObject error = new JSONObject();
        error.put(Constants.CODE, CodeEnum.BUS_FAIL.getResCode());
        error.put(Constants.MSG, StrUtil.isBlank(errMsg) ? CodeEnum.BUS_FAIL.getResMsg() : errMsg);
        return error.toJSONString();
    }


}