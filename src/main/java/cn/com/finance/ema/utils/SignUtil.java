package cn.com.finance.ema.utils;

import cn.com.finance.ema.model.req.core.*;
import cn.hutool.core.util.StrUtil;

/**
 * <p>
 *  签名工具类
 * </p>
 *
 * @author zhang_sir
 * @version v1.0.0
 * @since 2021/12/3 15:35
 */
public class SignUtil {

    public static boolean check(EmaSplitPayReq req) {

        StringBuffer source = new StringBuffer();
        source.append("version=V1.0").append("&merchantNo=").append(req.getMerchantNo()).append("&platformNo=").append(req.getPlatformNo())
                .append("&customerNo=").append(req.getCustomerNo()).append("&goodsNo=").append(req.getGoodsNo()).append("&orderNo=").append(req.getOrderNo())
                .append("&orderAmount=").append(req.getOrderAmount()).append("&orderDate=").append(req.getOrderDate()).append("&orderTime=").append(req.getOrderTime())
                .append("&remark=").append(req.getRemark())
                .append("&transNotifyUrl=").append(req.getTransNotifyUrl())
                .append(req.getPublicKey());

        return StrUtil.equalsIgnoreCase(req.getSign(), Md5Util.MD5(source.toString()));

    }

    public static boolean check(EmaSplitRefundReq req) {

        StringBuffer source = new StringBuffer();
        source.append("version=V1.0").append("&merchantNo=").append(req.getMerchantNo()).append("&platformNo=").append(req.getPlatformNo())
                .append("&orderNo=").append(req.getOrderNo()).append("&oriOrderNo=").append(req.getOriOrderNo()).append("&orderAmount=").append(req.getOrderAmount())
                .append("&orderDate=").append(req.getOrderDate()).append("&orderTime=").append(req.getOrderTime()).append("&remark=").append(req.getRemark())
                .append("&transNotifyUrl=").append(req.getTransNotifyUrl())
                .append(req.getPublicKey());

        return StrUtil.equalsIgnoreCase(req.getSign(), Md5Util.MD5(source.toString()));

    }

    public static boolean check(EmaQueryReq req) {

        StringBuffer source = new StringBuffer();
        source.append("version=V1.0").append("&merchantNo=").append(req.getMerchantNo()).append("&platformNo=").append(req.getPlatformNo())
                .append("&orderNo=").append(req.getOrderNo()).append("&orderDate=").append(req.getOrderDate())
                .append(req.getPublicKey());

        return StrUtil.equalsIgnoreCase(req.getSign(), Md5Util.MD5(source.toString()));

    }

    public static boolean check(GoodsSaveReq req) {

        StringBuffer source = new StringBuffer();
        source.append("version=V1.0").append("&merchantNo=").append(req.getMerchantNo()).append("&platformNo=").append(req.getPlatformNo())
                .append("&goodsName=").append(req.getGoodsName()).append("&goodsRate=").append(req.getGoodsRate())
                .append("&goodsNo=").append(req.getGoodsNo())
                .append("&goodsStatus=").append(req.getGoodsStatus())
                .append(req.getPublicKey());

        return StrUtil.equalsIgnoreCase(req.getSign(), Md5Util.MD5(source.toString()));

    }

    public static boolean check(GoodsUpdateReq req) {

        StringBuffer source = new StringBuffer();
        source.append("version=V1.0").append("&merchantNo=").append(req.getMerchantNo()).append("&platformNo=").append(req.getPlatformNo())
                .append("&goodsName=").append(req.getGoodsName()).append("&goodsRate=").append(req.getGoodsRate())
                .append("&goodsNo=").append(req.getGoodsNo()).append("&goodsStatus=").append(req.getGoodsStatus())
                .append(req.getPublicKey());

        return StrUtil.equalsIgnoreCase(req.getSign(), Md5Util.MD5(source.toString()));

    }

    public static String sign(String resCode, String resMsg, String status, String publicKey) {

        StringBuffer source = new StringBuffer();
        source.append("version=V1.0").append("&resCode=").append(resCode).append("&resMsg=").append(resMsg)
                .append("&status=").append(status).append(publicKey);

        return Md5Util.MD5(source.toString());

    }

    public static String sign(String resCode, String resMsg, String status, String publicKey, String goodsNo, String goodsStatus) {

        StringBuffer source = new StringBuffer();
        source.append("version=V1.0").append("&resCode=").append(resCode).append("&resMsg=").append(resMsg)
                .append("&status=").append(status).append("&goodsNo=").append(goodsNo).append("&goodsStatus=").append(goodsStatus)
                .append(publicKey);

        return Md5Util.MD5(source.toString());

    }

    public static String encrypt(EmaSplitPayReq req, String publicKey) {

        StringBuffer source = new StringBuffer();
        source.append("version=V1.0")
                .append("&orderNo=").append(req.getOrderNo())
                .append("&resCode=").append(req.getResCode())
                .append("&resMsg=").append(req.getResMsg())
                .append("&status=").append(req.getStatus())
                .append(publicKey);

        return Md5Util.MD5(source.toString());

    }

}
