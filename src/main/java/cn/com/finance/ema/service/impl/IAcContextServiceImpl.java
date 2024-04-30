package cn.com.finance.ema.service.impl;

import cn.com.finance.ema.model.req.core.MerFeeReq;
import cn.com.finance.ema.model.req.core.PlatformFeeReq;
import cn.com.finance.ema.service.IAcContextService;
import cn.com.finance.ema.utils.CalcUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * <p>
 * 金额计算服务实现类
 * </p>
 *
 * @author zhangsir
 * @version v1.0.0
 * @since 2021/12/2 10:38
 */
@Slf4j
@Service
public class IAcContextServiceImpl implements IAcContextService {
    @Override
    public String getAcDt() {
        return LocalDateTimeUtil.format(LocalDate.now(), "yyyyMMdd");
    }

    /**
     * 累计冻结额度减法
     *
     * @param freezeAmt 冻结额度
     * @param tranAmt   交易金额
     * @return
     */
    @Override
    public String calcAcAmt(String freezeAmt, String tranAmt) {
        CalcUtil calcUtil = new CalcUtil(StrUtil.isEmpty(freezeAmt) ? "0" : freezeAmt);
        return String.valueOf(calcUtil.subtract(tranAmt).getResult().longValue());
    }

    /**
     * 服务费计算 累加
     *
     * @param amt    原始金额
     * @param feeAmt 服务费
     * @return
     */
    @Override
    public String calcExtraAmt(String amt, String feeAmt) {

        CalcUtil calcUtil = new CalcUtil(StrUtil.isEmpty(amt) ? "0" : amt);
        return String.valueOf(calcUtil.add(feeAmt).getResult().longValue());
    }

    @Override
    public String calcFee(MerFeeReq merFeeReq) {
        String merFee;
        switch (merFeeReq.getFeeType()) {
            case "0":
                merFee = merFeeReq.getFixedFee();
                break;
            case "1":
                CalcUtil calcUtil = new CalcUtil(merFeeReq.getRatioFee());
                merFee = String.valueOf(calcUtil.multiply(merFeeReq.getOrderAmount()).getResult().longValue());
                break;
            case "2":
                CalcUtil calcUtils = new CalcUtil(merFeeReq.getRatioFee());
                merFee = calcExtraAmt(merFeeReq.getFixedFee(), String.valueOf(calcUtils.multiply(merFeeReq.getOrderAmount()).getResult().longValue()));
                break;
            default:
                merFee = merFeeReq.getOrderAmount();
                break;
        }
        return merFee;
    }

    @Override
    public String calcFee(PlatformFeeReq platformFeeReq) {
        String platformFee;
        // 0 固定费率 1 比例费率 2 固定与比例费率
        switch (platformFeeReq.getFeeType()) {
            case "0":
                platformFee = platformFeeReq.getFixedFee();
                break;
            case "1":
                CalcUtil calcUtil = new CalcUtil(platformFeeReq.getRatioFee());
                platformFee = String.valueOf(calcUtil.multiply(platformFeeReq.getOrderAmount()).getResult().longValue());
                break;
            case "2":
                CalcUtil calcUtils = new CalcUtil(platformFeeReq.getRatioFee());
                platformFee = calcExtraAmt(platformFeeReq.getFixedFee(), String.valueOf(calcUtils.multiply(platformFeeReq.getOrderAmount()).getResult().longValue()));
                break;
            default:
                platformFee = platformFeeReq.getOrderAmount();
                break;
        }
        return platformFee;
    }


    public String calcSplitAmt(String orderAmt, String ratio) {
        CalcUtil calcUtil = new CalcUtil(orderAmt);
        return String.valueOf(calcUtil.multiply(ratio).getResult().longValue());
    }

}
