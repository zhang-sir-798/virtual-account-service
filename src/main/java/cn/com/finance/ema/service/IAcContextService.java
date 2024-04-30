package cn.com.finance.ema.service;

import cn.com.finance.ema.model.req.core.MerFeeReq;
import cn.com.finance.ema.model.req.core.PlatformFeeReq;

/**
 * <p>
 * 金额计算服务
 * </p>
 *
 * @author zhangsir
 * @version v1.0.0
 * @since 2021/12/2 13:26
 */
public interface IAcContextService {

    /**
     * 获取当前会计日
     *
     * @return
     */
    String getAcDt();

    /**
     * 累减 减法
     *
     * @param freezeAmt 冻结金额
     * @param tranAmt   交易金额
     * @return
     */
    String calcAcAmt(String freezeAmt, String tranAmt);

    /**
     * 累加 加法
     *
     * @param amt    原始金额
     * @param feeAmt 服务费
     * @return
     */
    String calcExtraAmt(String amt, String feeAmt);


    /**
     * 交易手续费
     * 0 固定费率 1 比例费率 2 固定与比例费率
     */
    String calcFee(MerFeeReq merFeeReq);

    /**
     * 分账手续费
     * 0 固定费率 1 比例费率 2 固定与比例费率
     */
    String calcFee(PlatformFeeReq platformFeeReq);

    /**
     * 分账金额
     */
    String calcSplitAmt(String tranAmt, String ratio);

}
