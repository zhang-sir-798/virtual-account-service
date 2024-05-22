package cn.com.finance.ema.constants;

/**
 * <p>
 * 全局变量定义
 * </p>
 *
 * @author zhang_sir
 * @version v1.0.0
 * @since 2022/06/21 15:13
 */
public final class Globals {

    //本系统流水号
    public static final String SELF_ORDER_PREFIX = "SS";
    //通道
    public static final String CH_ORDER_PREFIX = "CH";

    /**
     * 资金明细前缀
     */
    //商户账户 冻结
    public static final String AC_FME_PREFIX = "FME";

    //商户账户 余额
    public static final String AC_BME_PREFIX = "BME";

    //平台账户 冻结
    public static final String AC_FPL_PREFIX = "FPL";

    //平台账户 余额
    public static final String AC_BPL_PREFIX = "FPL";

    //功能账户 余额
    public static final String AC_FFN_PREFIX = "FFN";

    //个人用户账户 冻结
    public static final String AC_FCS_PREFIX = "FCS";

    //分账手续费 余额
    public static final String AC_BSF_PREFIX = "BSF";

    //交易手续费 余额
    public static final String AC_BTF_PREFIX = "BTF";

    //系统7日终余额
    public static final String AC_RZB_PREFIX = "RZB7";

    //系统7日终冻结金额
    public static final String AC_RZF_PREFIX = "RZF7";

    /**
     * 字符编码
     */
    public static final String DEFAULT_ENCODING = "UTF-8";


}
