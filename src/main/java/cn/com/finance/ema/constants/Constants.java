package cn.com.finance.ema.constants;

/**
 * <p>
 * 常量
 * </p>
 *
 * @author zhang_sir
 * @version v1.0.0
 * @since 2022/06/21 15:13
 */
public class Constants {

    public static final String VERSION = "V1.0";

    /**
     * 成功返回  交易/提现成功
     */
    public static final String SUCCESS = "SUCCESS";

    /**
     * 处理中
     */
    public static final String DOING = "DOING";

    /**
     * 找不到原笔交易(用户未扫码)还未和上游交互
     */
    public static final String INIT = "INIT";

    /**
     * 处理失败
     */
    public static final String FAIL = "FAIL";
    //未知
    public static final String UNKONW = "UNKONW";
    /**
     * 异常
     */
    public static final String ERROR = "ERROR";

    /**
     * 退款成功 线下余额类
     */
    public static final String REFUNDSUCCESS = "SUCCESS";
    /**
     * 退款成功 线上余额类
     */
    public static final String ONLINEREFUNDSUCCESS = "REFUNDSUCCESS";

    /**
     * 退款失败 线上余额类
     */
    public static final String ONLINREFUNDFAILED = "REFUNDFAILED";

    /**
     * 出现异常 提现/交易失败
     */
    public static final String STATUS_500 = "9999";

    //交易
    public static final String TRANS_TYPE_PAY = "0";
    //退款
    public static final String TRANS_TYPE_REFUND = "1";

    //集团编号
    public static final String GROUP_N0 = "GP001";

    //交易手续费账户
    public static final String TRANS_FEE = "TRANSFEE001";

    //交易分账手续费账户
    public static final String TRANS_SUB_FEE = "TRANSSUBFEE001";

    //功能账户-余额类 FUN001
    public static final String FUN_NO = "FUN001";

    //产品-余额类消费
    public static final String PROD_001 = "SUB001";

    public static final String PROD_TRANS_001 = "TRANS001";

    public static final String CODE = "respCode";
    public static final String MSG = "respMsg";
    public static final String SIGN = "sign";

    //QBS秘钥存放地址
    //public final static String QB_RSA_PATH = "/usr/local/certs/qb/";
    public final static String QB_RSA_PATH = "D:\\Temp\\Certs.test\\";

    //系统产品
    public static class ProdCode {
        public final static String JD001 = "JD001"; // 动态聚合码
        public final static String JJ001 = "JJ001"; // 聚合静态码
        public final static String JB001 = "JB001"; // 聚合被扫
        public final static String DH001 = "DH001"; // 信用卡小额还款
        public final static String SMALL = "SMALL"; // 信用卡大额还款低费率
        public final static String LARGE = "LARGE"; // 信用卡大额还款高费率
        public final static String APPLET = "APPLET"; // 微信小程序
        public final static String OFFICIAL = "OFFICIAL"; // 微信公众号
        public final static String U002 = "U002";//银联云闪付
        public final static String Z001 = "Z001";//支付宝
    }

    //渠道产品类型
    public static class channelProd {
        //yisheng子产品
        public final static String wx_jsapi = "wx_jsapi";
        public final static String zfb_jsapi = "zfb_jsapi";
        public final static String ysf_jsapi = "ysf_jsapi";
        public final static String ysf_bar = "ysf_bar";
        public final static String zfb_bar = "zfb_bar";
        public final static String wx_bar = "wx_bar";
        //QB子产品
        public final static String W02 = "W02"; //微信公众号支付
        public final static String W03 = "W03"; //微信刷卡（反扫）
        public final static String W06 = "W06"; //微信小程序
        public final static String A03 = "A03"; //支付窗支付
        public final static String A02 = "A02"; //支付宝刷卡支付
        public final static String U01 = "U01"; //银联二维码扫码支付
        public final static String U03 = "U03"; //银联二维码被扫
        public final static String U05 = "U05"; //银联云闪付 ApplePay
        public final static String U06 = "U06"; //银联行业码
        //huanxun子产品
        public final static String A9505 = "9505"; //微信公众号支付(JSAPI)
        public final static String A9503 = "9503"; //微信刷卡（反扫）
        public final static String A9527 = "9527"; //支付窗支付(JSAPI)
        public final static String A9504 = "9504"; //支付宝刷卡支付
        public final static String A9532 = "9532"; //银联行业码(NATIVE)
        public final static String A9515 = "9515"; //银联二维码被扫
        public final static String A9516 = "9516"; //银联二维码主扫(NATIVE)
        //ronghui子产品
        public final static String UNIONPAY_QRCODE_DH = "UNIONPAY_QRCODE_DH"; //银联二维码被扫
        public final static String QUICK_GAR_SMALL = "QUICK_GAR_SMALL"; //银联二维码被扫
        public final static String QUICK_GAR_LARGE = "QUICK_GAR_LARGE"; //银联二维码被扫
    }

}
