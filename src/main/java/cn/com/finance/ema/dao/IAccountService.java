package cn.com.finance.ema.dao;


import cn.com.finance.ema.model.entity.Account;
import cn.com.finance.ema.model.entity.BaseBatchOrder;
import cn.com.finance.ema.model.entity.CmmPage;
import cn.com.finance.ema.model.req.core.EmaOnlinePayReq;
import cn.com.finance.ema.model.req.core.EmaOnlineRefundReq;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 账户表 服务类
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
public interface IAccountService extends IService<Account> {

    /**
     * 处理渠道交易账单 收+退
     */
    boolean handleOnlineAccBill(BaseBatchOrder batchOrder);

    /**
     * 处理下单账单 线上余额类
     */
    boolean handleOnlineAccBill(EmaOnlinePayReq req);

    /**
     * 处理退款账单 线上余额类
     */
    boolean handleOnlineRefundAccBill(EmaOnlineRefundReq req);

    /**
     * 查询账户
     */
    CmmPage<Account> queryByPage(Integer index, Integer pageSize, List<String> acTypes);

    /**
     * 统计7日终期末余额
     */
    boolean statistic4Day(String acNo, String acDt);

    /**
     * 操作账户余额
     *
     * @param opType          1增加可用余额；2增加冻结余额；3减少可用余额；4减少冻结余额
     * @param acAmt           记账金额
     * @param lastDt          最后记账日期
     * @param lastAcBalDataId 最后记账的资金明细ID
     * @return
     */
    boolean opAcBal(int opType, String acAmt, String extraAmt, String lastDt, String lastAcBalDataId, String acNo);

    public boolean opAcAfter(String bAfter, String fAfter, String lastDt, String lastAcBalDataId, String acNo);
}
