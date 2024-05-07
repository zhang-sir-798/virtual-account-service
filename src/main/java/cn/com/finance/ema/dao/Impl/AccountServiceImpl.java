package cn.com.finance.ema.dao.Impl;


import cn.com.finance.ema.constants.Constants;
import cn.com.finance.ema.constants.Globals;
import cn.com.finance.ema.dao.IAccountService;
import cn.com.finance.ema.dao.IBaseOperationService;
import cn.com.finance.ema.dao.IBaseTransferOrderService;
import cn.com.finance.ema.mapper.AccountMapper;
import cn.com.finance.ema.model.entity.*;
import cn.com.finance.ema.model.req.core.EmaOnlinePayReq;
import cn.com.finance.ema.model.req.core.EmaOnlineRefundReq;
import cn.com.finance.ema.service.IAcContextService;
import cn.com.finance.ema.utils.id.IdGenerator;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 账户表 服务实现类
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
@Slf4j
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements IAccountService {

    private final IAcContextService iAcContextService;
    private final IBaseOperationService iBaseOperationService;
    private final IBaseTransferOrderService iBaseTransferOrderService;

    public AccountServiceImpl(IBaseOperationService iBaseOperationService, IAcContextService iAcContextService, IBaseTransferOrderService iBaseTransferOrderService) {
        this.iAcContextService = iAcContextService;
        this.iBaseOperationService = iBaseOperationService;
        this.iBaseTransferOrderService = iBaseTransferOrderService;
    }

    /**
     * 记账中心：微、支、云 渠道类产品 异步通知后才可以入账
     */
    @Override
    public boolean handleOnlineAccBill(BaseBatchOrder batchOrder) {

        if (ObjectUtil.isNull(batchOrder)) {
            log.error("[记账中心] [微、支、云] [前置检查未通过: 参数为空]");
            return false;
        }

        String LastDt = iAcContextService.getAcDt();

        if (merAcBillOnline(batchOrder, LastDt) == false) {
            log.error("[记账中心] [微、支、云] [登记商户账户冻结余额失败][订单详情:{}]", batchOrder);
            return false;
        }

        if (platformAcBillOnline(batchOrder, LastDt) == false) {
            log.error("[记账中心] [微、支、云] [登记平台账户冻结余额失败][订单详情:{}]", batchOrder);
            return false;
        }
        log.info("[记账中心--微、支、云--处理成功] 系统单号:{}", batchOrder.getSysOrderNo());
        return true;
    }

    /**
     * 记账中心：线上余额类 自营产品直接入账  渠道类产品 异步通知后才可以入账
     */
    @Override
    //@Transactional(rollbackFor = Exception.class)
    public boolean handleOnlineAccBill(EmaOnlinePayReq req) {

        if (req.isRecorded()) {
            if (ObjectUtil.isNull(req)) {
                log.error("[记账中心][线上余额类 前置检查未通过: 参数为空]");
                return false;
            }

            String LastDt = iAcContextService.getAcDt();

            if (funAcBillOnline(req, LastDt) == false) {
                log.error("[记账中心][线上余额类 登记功能账户余额失败][订单详情:{}]", req);
                return false;
            }

            if (merAcBillOnline(req, LastDt) == false) {
                log.error("[记账中心][线上余额类 登记商户账户冻结余额失败][订单详情:{}]", req);
                return false;
            }

            if (platformAcBillOnline(req, LastDt) == false) {
                log.error("[记账中心][线上余额类 登记平台账户冻结余额失败][订单详情:{}]", req);
                return false;
            }

            if (customerAcBill(req, LastDt) == false) {
                log.error("[记账中心][登记个人用户资金操作流水失败][订单详情:{}]", req);
                return false;

            }
            log.info("[记账中心--线上余额类--处理成功 ] 系统单号:{}", req.getBaseBatchOrder().getSysOrderNo());
        }

        return true;
    }

    /**
     * 记账中心：线上余额类 退款
     */
    @Override
    //@Transactional(rollbackFor = Exception.class)
    public boolean handleOnlineRefundAccBill(EmaOnlineRefundReq req) {
        if (ObjectUtil.isNull(req)) {
            log.error("[记账中心][线上余额类 前置检查未通过: 参数为空]");
            return false;
        }

        if (req.isRecorded()) {

            String LastDt = iAcContextService.getAcDt();

            if (funAcBillOnline(req, LastDt) == false) {
                log.error("[记账中心][线上余额类 登记功能账户余额失败][订单详情:{}]", req);
                return false;
            }

            if (merAcBillOnline(req, LastDt) == false) {
                log.error("[记账中心][线上余额类 登记商户账户冻结余额失败][订单详情:{}]", req);
                return false;
            }

            if (platformAcBillOnline(req, LastDt) == false) {
                log.error("[记账中心][线上余额类 登记平台账户冻结余额失败][订单详情:{}]", req);
                return false;
            }

            if (StrUtil.equalsIgnoreCase("QB", req.getChannelNo()) && StrUtil.equalsIgnoreCase("ONLINE001", req.getTrxType())) {
                if (customerAcBill(req, LastDt) == false) {
                    log.error("[记账中心][登记个人用户资金操作流水失败][订单详情:{}]", req);
                    return false;
                }
            }
            log.info("[记账中心--线上余额类--处理成功 ] 系统单号:{}", req.getBaseBatchOrder().getSysOrderNo());
        }

        return true;
    }

    /**
     * 记账中心：微、支、云 商户账户 交易+退款
     */
    private boolean merAcBillOnline(BaseBatchOrder req, String LastDt) {
        boolean flag;

        Long totalTransFee = 0L;

        List<BaseTransferOrder> orders = iBaseTransferOrderService.list(new LambdaQueryWrapper<BaseTransferOrder>().eq(BaseTransferOrder::getBatchNo, req.getBatchNo()));
        //处理商户账户额度
        for (BaseTransferOrder reqSub : orders) {
            Account merAccount = getOne(new LambdaQueryWrapper<Account>().eq(Account::getAccountNo, reqSub.getMerchantSubNo()));
            if (ObjectUtil.isNull(merAccount)) {
                log.error("[记账中心][微、支、云类型 前置检查未通过: 商户额度账户余额不存在][merchantSubNo:{}]", reqSub.getMerchantSubNo());
                return false;
            }
            //交易手续费合计
            totalTransFee = totalTransFee + Long.parseLong(reqSub.getTransFee());
            String afterMerBalMoney = StrUtil.equalsIgnoreCase(Constants.TRANS_TYPE_PAY, reqSub.getTransType()) ?
                    iAcContextService.calcExtraAmt(merAccount.getBalance(), reqSub.getMerchantAmount()) :
                    iAcContextService.calcAcAmt(merAccount.getBalance(), reqSub.getMerchantAmount());
            merAccount.setLastAcBalDataId(Globals.AC_BME_PREFIX + IdGenerator.nextId());
            //增加或减少商户账户余额
            flag = StrUtil.equalsIgnoreCase(Constants.TRANS_TYPE_PAY, reqSub.getTransType()) ?
                    opAcBal(1, reqSub.getMerchantAmount(), "0", LastDt, merAccount.getLastAcBalDataId(), merAccount.getAccountNo()) :
                    opAcBal(3, reqSub.getMerchantAmount(), "0", LastDt, merAccount.getLastAcBalDataId(), merAccount.getAccountNo());
            if (flag == false) {
                log.error("[记账中心][微、支、云类型 增加或减少商户账户余额][处理失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", merAccount.getAccountNo(), reqSub.getSelfOrderNo(), merAccount.getLastAcBalDataId(), merAccount.getBalance(), afterMerBalMoney);
                return false;
            }
            //商户账户：登记资金明细
            flag = regAcbalDataOnline(reqSub.getOrderNo(), merAccount.getLastAcBalDataId(), merAccount.getAccountNo(), reqSub.getMerchantAmount(), reqSub.getTransFee(), merAccount.getBalance(), afterMerBalMoney, StrUtil.equalsIgnoreCase(Constants.TRANS_TYPE_PAY, req.getTransType()) ? "交易" : "退款", StrUtil.equalsIgnoreCase(Constants.TRANS_TYPE_PAY, req.getTransType()) ? "PLUS" : "MINUS", LastDt, merAccount.getType(), "00", "登记商户分账金额");
            if (flag == false) {
                log.error("[记账中心][微,云,支类型 登记商户资金明细流水][保存资金明细流水失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", merAccount.getAccountNo(), reqSub.getSelfOrderNo(), merAccount.getLastAcBalDataId(), merAccount.getBalance(), afterMerBalMoney);
            }

        }

        //------------处理交易手续费账户：增加余额一笔  退款要退手续费-----------
        Account transFeeAccount = getOne(new LambdaQueryWrapper<Account>().eq(Account::getAccountNo, Constants.TRANS_FEE));
        if (ObjectUtil.isNull(transFeeAccount)) {
            log.error("[记账中心][微,云,支类型 前置检查未通过: 交易手续费额度账户余额不存在]");
            return false;
        }
        String totalTransFeeAmt = String.valueOf(totalTransFee);
        String afterTransFeeBalance = StrUtil.equalsIgnoreCase(Constants.TRANS_TYPE_PAY, req.getTransType()) ? iAcContextService.calcExtraAmt(transFeeAccount.getBalance(), req.getTotalFee()) : iAcContextService.calcAcAmt(transFeeAccount.getBalance(), req.getTotalFee());
        transFeeAccount.setLastAcBalDataId(Globals.AC_BTF_PREFIX + IdGenerator.nextId());
        //增加或减少交易手续费账户余额
        flag = StrUtil.equalsIgnoreCase(Constants.TRANS_TYPE_PAY, req.getTransType()) ?
                opAcBal(1, totalTransFeeAmt, "0", LastDt, transFeeAccount.getLastAcBalDataId(), transFeeAccount.getAccountNo()) :
                opAcBal(3, totalTransFeeAmt, "0", LastDt, transFeeAccount.getLastAcBalDataId(), transFeeAccount.getAccountNo());
        if (flag == false) {
            log.error("[记账中心][微,云,支类型 增加或减少手续费账户余额][处理失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", transFeeAccount.getAccountNo(), req.getBatchNo(), transFeeAccount.getLastAcBalDataId(), transFeeAccount.getBalance(), afterTransFeeBalance);
            return false;
        }
        //交易手续费账户：登记资金明细
        flag = regAcbalDataOnline(req.getBatchNo(), transFeeAccount.getLastAcBalDataId(), transFeeAccount.getAccountNo(), req.getTotalAmount(), totalTransFeeAmt, transFeeAccount.getBalance(), afterTransFeeBalance, StrUtil.equals("0", req.getTransType()) ? "交易" : "退款", "PLUS", LastDt, transFeeAccount.getType(), "00", "登记交易手续费");
        if (flag == false) {
            log.error("[记账中心][微,云,支类型 登记手续费账户资金明细流水][保存资金明细流水失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", transFeeAccount.getAccountNo(), req.getBatchNo(), transFeeAccount.getLastAcBalDataId(), transFeeAccount.getBalance(), afterTransFeeBalance);
        }

        return flag;
    }

    private boolean merAcBillOnline(EmaOnlinePayReq req, String LastDt) {
        boolean flag;
        Long totalTransFee = 0L;
        //处理商户账户额度 -> 商户账户：线上余额类 -> 增加余额而非冻结额度
        for (BaseTransferOrder reqSub : req.getBaseTransferOrders()) {
            Account merAccount = getOne(new LambdaQueryWrapper<Account>().eq(Account::getAccountNo, reqSub.getMerchantSubNo()));
            if (ObjectUtil.isNull(merAccount)) {
                log.error("[记账中心][线上余额类 前置检查未通过: 商户额度账户余额不存在][merchantSubNo:{}]", reqSub.getMerchantSubNo());
                return false;
            }
            //交易手续费合计
            totalTransFee = totalTransFee + Long.parseLong(reqSub.getTransFee());
            String afterMerBalMoney = iAcContextService.calcExtraAmt(merAccount.getBalance(), reqSub.getMerchantAmount());
            merAccount.setLastDt(LastDt);
            merAccount.setLastAcBalDataId(Globals.AC_BME_PREFIX + IdGenerator.nextId());
            //商户账户：加余额
            flag = opAcBal(1, reqSub.getMerchantAmount(), "0", LastDt, merAccount.getLastAcBalDataId(), merAccount.getAccountNo());
            if (flag == false) {
                log.error("[记账中心][线上余额类 增加商户余额][处理失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", merAccount.getAccountNo(), reqSub.getSelfOrderNo(), merAccount.getLastAcBalDataId(), merAccount.getBalance(), afterMerBalMoney);
                return false;
            }
            //商户账户：登记资金明细
            flag = regAcbalDataOnline(reqSub.getOrderNo(), merAccount.getLastAcBalDataId(), merAccount.getAccountNo(), reqSub.getMerchantAmount(), reqSub.getTransFee(), merAccount.getBalance(), afterMerBalMoney, StrUtil.equalsIgnoreCase(Constants.TRANS_TYPE_PAY, req.getTransType()) ? "交易" : "退款", StrUtil.equalsIgnoreCase(Constants.TRANS_TYPE_PAY, req.getTransType()) ? "PLUS" : "MINUS", LastDt, merAccount.getType(), "00", "登记商户分账金额");
            if (flag == false) {
                log.error("[记账中心][线上余额类 登记商户资金明细流水][保存资金明细流水失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", merAccount.getAccountNo(), reqSub.getSelfOrderNo(), merAccount.getLastAcBalDataId(), merAccount.getBalance(), afterMerBalMoney);
            }
        }

        //-----------处理交易手续费账户：增加余额一笔  退款要退手续费----------
        Account transFeeAccount = getOne(new LambdaQueryWrapper<Account>().eq(Account::getAccountNo, Constants.TRANS_FEE));
        if (ObjectUtil.isNull(transFeeAccount)) {
            log.error("[记账中心][线上余额类 前置检查未通过: 交易手续费额度账户余额不存在]");
            return false;
        }
        String totalTransFeeAmt = String.valueOf(totalTransFee);
        String afterTransFeeBalance = iAcContextService.calcExtraAmt(transFeeAccount.getBalance(), totalTransFeeAmt);
        transFeeAccount.setLastDt(LastDt);
        transFeeAccount.setLastAcBalDataId(Globals.AC_BTF_PREFIX + IdGenerator.nextId());
        //手续费账户：加余额
        flag = opAcBal(1, totalTransFeeAmt, "0", transFeeAccount.getLastDt(), transFeeAccount.getLastAcBalDataId(), transFeeAccount.getAccountNo());
        if (flag == false) {
            log.error("[记账中心][线上余额类 增加手续费账余额][处理失败][acNo:{}, 交易流水号:{}]", transFeeAccount.getAccountNo(), req.getOrderNo());
            return false;
        }
        //交易手续费账户：登记资金明细
        flag = regAcbalDataOnline(req.getOrderNo(), transFeeAccount.getLastAcBalDataId(), transFeeAccount.getAccountNo(), req.getAmount(), totalTransFeeAmt, transFeeAccount.getBalance(), afterTransFeeBalance, StrUtil.equals("0", req.getTransType()) ? "交易" : "退款", "PLUS", LastDt, transFeeAccount.getType(), "00", "登记交易手续费");
        if (flag == false) {
            log.error("[记账中心][线上余额类 登记手续费账户资金明细流水][保存资金明细流水失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", transFeeAccount.getAccountNo(), req.getOrderNo(), transFeeAccount.getLastAcBalDataId(), transFeeAccount.getBalance(), afterTransFeeBalance);
        }

        return flag;
    }

    private boolean merAcBillOnline(EmaOnlineRefundReq req, String LastDt) {
        boolean flag;
        Long totalTransFee = 0L;
        //处理商户账户额度 -> 商户账户：线上余额类 -> 增加余额而非冻结额度
        for (BaseTransferOrder baseOrder : req.getBaseTransferOrders()) {
            Account merAccount = getOne(new LambdaQueryWrapper<Account>().eq(Account::getAccountNo, baseOrder.getMerchantSubNo()));
            if (ObjectUtil.isNull(merAccount)) {
                log.error("[记账中心][线上余额类 前置检查未通过: 商户额度账户余额不存在][merchantSubNo:{}]", baseOrder.getMerchantSubNo());
                return false;
            }
            //交易手续费合计
            totalTransFee = totalTransFee + Long.parseLong(baseOrder.getTransFee());
            String afterMerBalMoney = iAcContextService.calcAcAmt(merAccount.getBalance(), baseOrder.getMerchantAmount());
            merAccount.setLastDt(LastDt);
            merAccount.setLastAcBalDataId(Globals.AC_BME_PREFIX + IdGenerator.nextId());
            //商户账户：扣减余额
            flag = opAcBal(3, baseOrder.getMerchantAmount(), "0", LastDt, merAccount.getLastAcBalDataId(), merAccount.getAccountNo());
            if (flag == false) {
                log.error("[记账中心][线上余额类 扣减商户账户余额][处理失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", merAccount.getAccountNo(), baseOrder.getSelfOrderNo(), merAccount.getLastAcBalDataId(), merAccount.getBalance(), afterMerBalMoney);
                return false;
            }
            //商户账户：登记资金明细
            flag = regAcbalDataOnline(baseOrder.getOrderNo(), merAccount.getLastAcBalDataId(), merAccount.getAccountNo(), baseOrder.getMerchantAmount(), "0", merAccount.getBalance(), afterMerBalMoney, StrUtil.equalsIgnoreCase(Constants.TRANS_TYPE_PAY, baseOrder.getTransType()) ? "交易" : "退款", StrUtil.equalsIgnoreCase(Constants.TRANS_TYPE_PAY, baseOrder.getTransType()) ? "PLUS" : "MINUS", LastDt, merAccount.getType(), "00", "登记商户分账金额");
            if (flag == false) {
                log.error("[记账中心][线上余额类 登记商户资金明细流水][保存资金明细流水失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", merAccount.getAccountNo(), baseOrder.getSelfOrderNo(), merAccount.getLastAcBalDataId(), merAccount.getBalance(), afterMerBalMoney);
            }

        }

        //--------------处理交易手续费账户：增加余额一笔  退款要退手续费---------------------
        Account transFeeAccount = getOne(new LambdaQueryWrapper<Account>().eq(Account::getAccountNo, Constants.TRANS_FEE));
        if (ObjectUtil.isNull(transFeeAccount)) {
            log.error("[记账中心][线上余额类 前置检查未通过: 交易手续费额度账户余额不存在]");
            return false;
        }
        String totalTransFeeAmt = String.valueOf(totalTransFee);
        String afterTransFeeBalance = iAcContextService.calcAcAmt(transFeeAccount.getBalance(), totalTransFeeAmt);
        transFeeAccount.setLastDt(LastDt);
        transFeeAccount.setLastAcBalDataId(Globals.AC_BTF_PREFIX + IdGenerator.nextId());
        //扣减交易手续费账户额度
        flag = opAcBal(3, totalTransFeeAmt, "0", transFeeAccount.getLastDt(), transFeeAccount.getLastAcBalDataId(), transFeeAccount.getAccountNo());
        if (flag == false) {
            log.error("[记账中心][线上余额类 扣减手续费账户余额][处理失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", transFeeAccount.getAccountNo(), req.getOrderNo(), transFeeAccount.getLastAcBalDataId(), transFeeAccount.getBalance(), afterTransFeeBalance);
            return false;
        }
        //交易手续费账户：登记资金明细
        flag = regAcbalDataOnline(req.getOrderNo(), transFeeAccount.getLastAcBalDataId(), transFeeAccount.getAccountNo(), req.getTotalAmount(), totalTransFeeAmt, transFeeAccount.getBalance(), afterTransFeeBalance, "退款", "PLUS", LastDt, transFeeAccount.getType(), "00", "登记交易手续费");
        if (flag == false) {
            log.error("[记账中心][线上余额类 登记手续费账户资金明细流水][保存资金明细流水失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", transFeeAccount.getAccountNo(), req.getOrderNo(), transFeeAccount.getLastAcBalDataId(), transFeeAccount.getBalance(), afterTransFeeBalance);
        }

        return flag;
    }

    private boolean platformAcBillOnline(BaseBatchOrder req, String LastDt) {
        boolean flag;
        //处理平台账户额
        Account platformAccount = getOne(new LambdaQueryWrapper<Account>().eq(Account::getAccountNo, req.getPlateSubNo()));
        if (ObjectUtil.isNull(platformAccount)) {
            log.error("[记账中心][微支云交易 前置检查未通过: 平台额度账户余额不存在][plateSubNo:{}]", req.getPlateSubNo());
            return false;
        }
        //String platExtAmt = iAcContextService.calcAcAmt(req.getTotalPlatformAmount(), req.getTotalFee());
        String afterPlatformBalMoney = StrUtil.equalsIgnoreCase(Constants.TRANS_TYPE_PAY, req.getTransType()) ?
                iAcContextService.calcExtraAmt(platformAccount.getBalance(), iAcContextService.calcAcAmt(req.getTotalPlatformAmount(), req.getTotalFee())) :
                iAcContextService.calcAcAmt(platformAccount.getBalance(), iAcContextService.calcAcAmt(req.getTotalPlatformAmount(), req.getTotalFee()));
        platformAccount.setLastAcBalDataId(Globals.AC_BPL_PREFIX + IdGenerator.nextId());
        //增加或减少平台账户余额
        flag = StrUtil.equalsIgnoreCase(Constants.TRANS_TYPE_PAY, req.getTransType()) ?
                opAcBal(5, req.getTotalPlatformAmount(), req.getTotalFee(), LastDt, platformAccount.getLastAcBalDataId(), platformAccount.getAccountNo()) :
                opAcBal(6, req.getTotalPlatformAmount(), req.getTotalFee(), LastDt, platformAccount.getLastAcBalDataId(), platformAccount.getAccountNo());
        if (flag == false) {
            log.error("[记账中心][微支云交易 增加或减少平台账户余额][处理失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", platformAccount.getAccountNo(), req.getBatchNo(), platformAccount.getLastAcBalDataId(), platformAccount.getBalance(), afterPlatformBalMoney);
            return false;
        }
        //登记资金明细流水
        flag = regAcbalDataOnline(req.getBatchNo(), platformAccount.getLastAcBalDataId(), platformAccount.getAccountNo(),
                req.getTotalPlatformAmount(), req.getTotalFee(), platformAccount.getBalance(), afterPlatformBalMoney, StrUtil.equals("0", req.getTransType()) ? "交易" : "退款", StrUtil.equalsIgnoreCase(Constants.TRANS_TYPE_PAY, req.getTransType()) ? "PLUS" : "MINUS", LastDt, platformAccount.getType(), "00", "登记平台分账金额");
        if (flag == false) {
            log.error("[记账中心][线上余额类 登记平台账户资金明细流水][保存资金明细流水失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", platformAccount.getAccountNo(), req.getBatchNo(), platformAccount.getLastAcBalDataId(), platformAccount.getBalance(), afterPlatformBalMoney);
        }

        //------------处理分账手续费账户：退款不退分账手续费-----------
        if (StrUtil.equalsIgnoreCase(Constants.TRANS_TYPE_PAY, req.getTransType())) {
            Account splitFeeAccount = getOne(new LambdaQueryWrapper<Account>().eq(Account::getAccountNo, Constants.TRANS_SUB_FEE));
            if (ObjectUtil.isNull(splitFeeAccount)) {
                log.error("[记账中心][线上余额类 前置检查未通过: 分账手续费额度账户不存在]");
                return false;
            }
            String afterSplitFeeBalance = iAcContextService.calcExtraAmt(splitFeeAccount.getBalance(), req.getTotalSplitFee());
            splitFeeAccount.setLastAcBalDataId(Globals.AC_BSF_PREFIX + IdGenerator.nextId());
            //增加分账手续费账户余额
            flag = opAcBal(1, req.getTotalSplitFee(), "0", LastDt, splitFeeAccount.getLastAcBalDataId(), splitFeeAccount.getAccountNo());
            if (flag == false) {
                log.error("[记账中心][线上余额类 增加分账手续费账户余额][处理失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", splitFeeAccount.getAccountNo(), req.getBatchNo(), splitFeeAccount.getLastAcBalDataId(), splitFeeAccount.getBalance(), afterSplitFeeBalance);
                return false;
            }
            //分账手续费账户：登记资金明细
            flag = regAcbalDataOnline(req.getBatchNo(), splitFeeAccount.getLastAcBalDataId(), splitFeeAccount.getAccountNo(), req.getTotalSplitFee(), req.getTotalSplitFee(), splitFeeAccount.getBalance(), afterSplitFeeBalance, StrUtil.equals("0", req.getTransType()) ? "交易" : "退款", "PLUS", LastDt, splitFeeAccount.getType(), "00", "登记分账手续费");
            if (flag == false) {
                log.error("[记账中心][线上余额类 登记手续费账户资金明细流水][保存资金明细流水失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", splitFeeAccount.getAccountNo(), req.getBatchNo(), splitFeeAccount.getLastAcBalDataId(), splitFeeAccount.getBalance(), afterSplitFeeBalance);
            }
        }
        return flag;
    }

    private boolean platformAcBillOnline(EmaOnlinePayReq req, String LastDt) {
        boolean flag;

        Account platformAccount = getOne(new LambdaQueryWrapper<Account>().eq(Account::getAccountNo, req.getPlateSubNo()));
        if (ObjectUtil.isNull(platformAccount)) {
            log.error("[记账中心][线上余额类 前置检查未通过: 平台额度账户余额不存在][plateSubNo:{}]", req.getPlateSubNo());
            return false;
        }

        String afterPlatformBalMoney = iAcContextService.calcExtraAmt(platformAccount.getBalance(), req.getBaseBatchOrder().getTotalPlatformAmount());
        platformAccount.setLastDt(LastDt);
        platformAccount.setLastAcBalDataId(Globals.AC_BPL_PREFIX + IdGenerator.nextId());
        //增加平台账户余额
        flag = opAcBal(1, req.getBaseBatchOrder().getTotalPlatformAmount(), "0", platformAccount.getLastDt(), platformAccount.getLastAcBalDataId(), platformAccount.getAccountNo());
        if (flag == false) {
            log.error("[记账中心][线上余额类 增加平台账户余额][处理失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", platformAccount.getAccountNo(), req.getOrderNo(), platformAccount.getLastAcBalDataId(), platformAccount.getBalance(), afterPlatformBalMoney);
            return false;
        }
        //登记资金明细流水
        flag = regAcbalDataOnline(req.getOrderNo(), platformAccount.getLastAcBalDataId(), platformAccount.getAccountNo(), req.getBaseBatchOrder().getTotalPlatformAmount(), req.getTransFee(), platformAccount.getBalance(), afterPlatformBalMoney, StrUtil.equals("0", req.getTransType()) ? "交易" : "退款", StrUtil.equalsIgnoreCase(Constants.TRANS_TYPE_PAY, req.getTransType()) ? "PLUS" : "MINUS", LastDt, platformAccount.getType(), "00", "登记平台分账金额");
        if (flag == false) {
            log.error("[记账中心][线上余额类 登记平台账户资金明细流水][保存资金明细流水失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", platformAccount.getAccountNo(), req.getOrderNo(), platformAccount.getLastAcBalDataId(), platformAccount.getBalance(), afterPlatformBalMoney);
        }

        //------------处理分账手续费账户：增加余额一笔  退款不退手续费-------------
        if (StrUtil.equalsIgnoreCase(Constants.TRANS_TYPE_PAY, req.getTransType())) {
            Account splitFeeAccount = getOne(new LambdaQueryWrapper<Account>().eq(Account::getAccountNo, Constants.TRANS_SUB_FEE));
            if (ObjectUtil.isNull(splitFeeAccount)) {
                log.error("[记账中心][线上余额类 前置检查未通过: 分账手续费额度账户不存在]");
                return false;
            }
            String afterSplitFeeBalance = iAcContextService.calcExtraAmt(splitFeeAccount.getBalance(), req.getSplitFee());
            splitFeeAccount.setLastDt(LastDt);
            splitFeeAccount.setLastAcBalDataId(Globals.AC_BSF_PREFIX + IdGenerator.nextId());
            //分账手续费账户：增加余额
            flag = opAcBal(1, req.getSplitFee(), "0", splitFeeAccount.getLastDt(), splitFeeAccount.getLastAcBalDataId(), splitFeeAccount.getAccountNo());
            if (flag == false) {
                log.error("[记账中心][线上余额类 增加手续费账户余额][处理失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", splitFeeAccount.getAccountNo(), req.getOrderNo(), splitFeeAccount.getLastAcBalDataId(), splitFeeAccount.getBalance(), afterSplitFeeBalance);
                return false;
            }
            //分账手续费账户：登记资金明细
            flag = regAcbalDataOnline(req.getOrderNo(), splitFeeAccount.getLastAcBalDataId(), splitFeeAccount.getAccountNo(), req.getAmount(), req.getSplitFee(), splitFeeAccount.getBalance(), afterSplitFeeBalance, StrUtil.equals("0", req.getTransType()) ? "交易" : "退款", "PLUS", LastDt, splitFeeAccount.getType(), "00", "登记分账手续费");
            if (flag == false) {
                log.error("[记账中心][线上余额类 登记手续费账户资金明细流水][保存资金明细流水失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", splitFeeAccount.getAccountNo(), req.getOrderNo(), splitFeeAccount.getLastAcBalDataId(), splitFeeAccount.getBalance(), afterSplitFeeBalance);
            }
        }

        return flag;
    }

    private boolean platformAcBillOnline(EmaOnlineRefundReq req, String LastDt) {
        boolean flag = false;

        for (BaseTransferOrder baseOrder : req.getBaseTransferOrders()) {
            Account platformAccount = getOne(new LambdaQueryWrapper<Account>().eq(Account::getAccountNo, baseOrder.getPlateSubNo()));
            if (ObjectUtil.isNull(platformAccount)) {
                log.error("[记账中心][线上余额类 前置检查未通过: 平台额度账户余额不存在][plateSubNo:{}]", baseOrder.getPlateSubNo());
                return false;
            }
            String afterPlatformBalMoney = iAcContextService.calcAcAmt(platformAccount.getBalance(), baseOrder.getPlatformAmount());
            platformAccount.setLastDt(LastDt);
            platformAccount.setLastAcBalDataId(Globals.AC_BPL_PREFIX + IdGenerator.nextId());
            //扣减平台账户余额
            flag = opAcBal(3, baseOrder.getPlatformAmount(), "0", platformAccount.getLastDt(), platformAccount.getLastAcBalDataId(), platformAccount.getAccountNo());
            if (flag == false) {
                log.error("[记账中心][线上余额类 扣减平台账户余额][处理失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", platformAccount.getAccountNo(), req.getOrderNo(), platformAccount.getLastAcBalDataId(), platformAccount.getBalance(), afterPlatformBalMoney);
                return false;
            }
            //登记资金明细流水
            flag = regAcbalDataOnline(req.getOrderNo(), platformAccount.getLastAcBalDataId(), platformAccount.getAccountNo(), baseOrder.getPlatformAmount(), baseOrder.getTransFee(), platformAccount.getBalance(), afterPlatformBalMoney, StrUtil.equals("0", baseOrder.getTransType()) ? "交易" : "退款", StrUtil.equalsIgnoreCase(Constants.TRANS_TYPE_PAY, baseOrder.getTransType()) ? "PLUS" : "MINUS", LastDt, platformAccount.getType(), "00", "登记平台分账");
            if (flag == false) {
                log.error("[记账中心][线上余额类 登记平台账户资金明细流水][保存资金明细流水失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", platformAccount.getAccountNo(), req.getOrderNo(), platformAccount.getLastAcBalDataId(), platformAccount.getBalance(), afterPlatformBalMoney);
            }
        }

        return flag;
    }

    private boolean funAcBillOnline(EmaOnlinePayReq req, String LastDt) {
        boolean flag;

        //记账：功能账户扣减余额
        Account funAccount = getOne(new LambdaQueryWrapper<Account>().eq(Account::getAccountNo, req.getFunNo()));
        if (ObjectUtil.isNull(funAccount)) {
            log.error("[记账中心][线上余额类 前置检查未通过: 功能账户不存在][FunNo:{}]", req.getFunNo());
            return false;
        }
        //账户资金流额度（余额） 交易手续费
        String afterBusBalanceMoney = iAcContextService.calcAcAmt(funAccount.getBalance(), req.getAmount());
        funAccount.setLastDt(LastDt);
        funAccount.setLastAcBalDataId(Globals.AC_FFN_PREFIX + IdGenerator.nextId());
        //扣减额度
        flag = opAcBal(3, req.getAmount(), "0", funAccount.getLastDt(), funAccount.getLastAcBalDataId(), funAccount.getAccountNo());
        if (!flag) {
            log.error("[记账中心][线上余额类 扣减功能账户资金余额][更新失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", funAccount.getAccountNo(), req.getOrderNo(), funAccount.getLastAcBalDataId(), funAccount.getBalance(), afterBusBalanceMoney);
            return false;
        }
        //登记资金明细流水
        flag = regAcbalDataOnline(req.getOrderNo(), funAccount.getLastAcBalDataId(), funAccount.getAccountNo(), req.getAmount(), "0", funAccount.getBalance(), afterBusBalanceMoney, StrUtil.equals("0", req.getTransType()) ? "交易" : "退款", StrUtil.equalsIgnoreCase(Constants.TRANS_TYPE_PAY, req.getTransType()) ? "MINUS" : "PLUS", LastDt, funAccount.getType(), "00", "登记功能账户余额");
        if (flag == false) {
            log.error("[记账中心][线上余额类 登记平台功能账户资金明细流水][保存资金明细流水失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", funAccount.getAccountNo(), req.getOrderNo(), funAccount.getLastAcBalDataId(), funAccount.getBalance(), afterBusBalanceMoney);
        }

        return flag;
    }

    private boolean funAcBillOnline(EmaOnlineRefundReq req, String LastDt) {
        boolean flag;
        Account funAccount = getOne(new LambdaQueryWrapper<Account>().eq(Account::getAccountNo, req.getFunNo()));
        if (ObjectUtil.isNull(funAccount)) {
            log.error("[记账中心][线上余额类 前置检查未通过: 功能账户不存在][FunNo:{}]", req.getFunNo());
            return false;
        }
        //账户资金流额度（余额）讨论功能账户 是否扣除手续费 再入账 不应该扣手续费  手续费内扣 必须全额扣除功能账户额度
        String afterBusBalanceMoney = iAcContextService.calcExtraAmt(funAccount.getBalance(), req.getTotalAmount());
        funAccount.setLastDt(LastDt);
        funAccount.setLastAcBalDataId(Globals.AC_FFN_PREFIX + IdGenerator.nextId());
        //增加额度
        flag = opAcBal(1, req.getTotalAmount(), "0", funAccount.getLastDt(), funAccount.getLastAcBalDataId(), funAccount.getAccountNo());
        if (!flag) {
            log.error("[记账中心][线上余额类 增加功能账户余额][更新失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", funAccount.getAccountNo(), req.getOrderNo(), funAccount.getLastAcBalDataId(), funAccount.getBalance(), afterBusBalanceMoney);
            return false;
        }
        //登记资金明细流水
        flag = regAcbalDataOnline(req.getOrderNo(), funAccount.getLastAcBalDataId(), funAccount.getAccountNo(), req.getTotalAmount(), "0", funAccount.getBalance(), afterBusBalanceMoney, "退款", "PLUS", LastDt, funAccount.getType(), "00", "登记功能账户余额");
        if (flag == false) {
            log.error("[记账中心][线上余额类 登记平台功能账户资金明细流水][保存资金明细流水失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", funAccount.getAccountNo(), req.getOrderNo(), funAccount.getLastAcBalDataId(), funAccount.getBalance(), afterBusBalanceMoney);
        }

        return flag;
    }

    private boolean customerAcBill(EmaOnlinePayReq req, String LastDt) {
        boolean flag;

        Account customerAccount = getOne(new LambdaQueryWrapper<Account>().eq(Account::getAccountNo, req.getOpenId()));
        if (ObjectUtil.isNull(customerAccount)) {
            log.error("[记账中心][前置检查未通过: 个人用户账户不存在][customerNo:{}]", req.getOpenId());
            return false;
        }
        //个人用户账户信息流额度（余额） 只记录流水
        String afterCustomerBalanceMoney = iAcContextService.calcExtraAmt(customerAccount.getBalance(), req.getAmount());
        customerAccount.setLastDt(LastDt);
        customerAccount.setLastAcBalDataId(Globals.AC_FCS_PREFIX + IdGenerator.nextId());
        //增加个人账户余额
        flag = opAcBal(1, req.getAmount(), "0", customerAccount.getLastDt(), customerAccount.getLastAcBalDataId(), customerAccount.getAccountNo());
        if (flag == false) {
            log.error("[记账中心][增加个人用户账户余额][处理失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", customerAccount.getAccountNo(), req.getOrderNo(), customerAccount.getLastAcBalDataId(), customerAccount.getBalance(), afterCustomerBalanceMoney);
            return false;
        }
        //登记资金明细流水
        flag = regAcbalDataOnline(req.getOrderNo(), customerAccount.getLastAcBalDataId(), customerAccount.getAccountNo(), req.getAmount(), "0", customerAccount.getBalance(), afterCustomerBalanceMoney, "交易", "PLUS", LastDt, customerAccount.getType(), "00", "登记客户账户余额");
        if (flag == false) {
            log.error("[记账中心][登记平台个人用户账户资金明细流水][保存资金明细流水失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", customerAccount.getAccountNo(), req.getOrderNo(), customerAccount.getLastAcBalDataId(), customerAccount.getBalance(), afterCustomerBalanceMoney);
        }

        return flag;
    }

    private boolean customerAcBill(EmaOnlineRefundReq req, String LastDt) {
        boolean flag;

        Account customerAccount = getOne(new LambdaQueryWrapper<Account>().eq(Account::getAccountNo, req.getCustomerNo()));
        if (ObjectUtil.isNull(customerAccount)) {
            log.error("[记账中心][前置检查未通过: 个人用户账户不存在][customerNo:{}]", req.getCustomerNo());
            return false;
        }
        //个人用户账户信息流额度（余额）
        String afterCustomerBalanceMoney = iAcContextService.calcAcAmt(customerAccount.getBalance(), req.getTotalAmount());
        customerAccount.setLastDt(LastDt);
        customerAccount.setLastAcBalDataId(Globals.AC_FCS_PREFIX + IdGenerator.nextId());
        //扣减个人用户账户余额
        flag = opAcBal(3, req.getTotalAmount(), "0", customerAccount.getLastDt(), customerAccount.getLastAcBalDataId(), customerAccount.getAccountNo());
        if (flag == false) {
            log.error("[记账中心][扣减个人用户账户余额][处理失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", customerAccount.getAccountNo(), req.getOrderNo(), customerAccount.getLastAcBalDataId(), customerAccount.getBalance(), afterCustomerBalanceMoney);
            return false;
        }
        //登记资金明细流水
        flag = regAcbalDataOnline(req.getOrderNo(), customerAccount.getLastAcBalDataId(), customerAccount.getAccountNo(), req.getTotalAmount(), "0", customerAccount.getBalance(), afterCustomerBalanceMoney, "退款", "MINUS", LastDt, customerAccount.getType(), "00", "登记客户账户余额");
        if (flag == false) {
            log.error("[记账中心][登记平台个人用户账户资金明细流水][保存资金明细流水失败][acNo:{}, 交易流水号:{} , 资金明细号:{}, 变更前余额:{}, 变更后余额:{}]", customerAccount.getAccountNo(), req.getOrderNo(), customerAccount.getLastAcBalDataId(), customerAccount.getBalance(), afterCustomerBalanceMoney);
        }
        return flag;
    }

    @Override
    public CmmPage<Account> queryByPage(Integer index, Integer pageSize, List<String> acTypes) {
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(Account::getType, acTypes);
        queryWrapper.lambda().eq(Account::getStatus, "0");
        queryWrapper.lambda().isNotNull(Account::getLastDt);
        queryWrapper.lambda().isNotNull(Account::getLastAcBalDataId);
        IPage<Account> pageResult = page(new Page<>(index, pageSize), queryWrapper);
        CmmPage<Account> cmmPage = new CmmPage<>();
        cmmPage.setPageSize(Convert.toInt(pageResult.getSize()));
        cmmPage.setPages((int) pageResult.getPages());
        cmmPage.setTotal(pageResult.getTotal());
        cmmPage.setDatas(pageResult.getRecords());

        return cmmPage;
    }

    @Override
    public boolean statistic4Day(String acNo, String acDt) {
        String balance; // 7日期末余额
        String freeze; // 7日期末冻结余额
        try {
            Account account = getBaseMapper().selectOne(new LambdaQueryWrapper<Account>().eq(Account::getAccountNo, acNo));
            BaseOperation operation = iBaseOperationService.queryLast(acNo, acDt);

            if (ObjectUtil.isNotNull(account) && ObjectUtil.isNotNull(operation)) {
                log.info("[执行-统计期末余额][查询原始额度信息, acNo:{} 会计日:{} 余额:{} 冻结:{}]", acNo, acDt, account.getBalance(), account.getFreezeMoney());
                balance = iAcContextService.calcExtraAmt(account.getBalance(), operation.getAfterMoney());
                freeze = iAcContextService.calcAcAmt(account.getFreezeMoney(), operation.getAfterMoney());
            } else { // 当前会计日，没有成功的资金明细， 根据账户的最后一期余额，统计期末余额
                log.error("[执行-统计期末余额][查询最后一笔成功的 资金明细operation:{} 统计账户account:{}]", operation, account);
                balance = account.getBalance();
                freeze = account.getFreezeMoney();
            }
            account.setLastAcBalDataId(Globals.AC_FME_PREFIX + IdGenerator.nextId());
            account.setLastDt(iAcContextService.getAcDt());

            if (saveOperations(account, balance, freeze, ObjectUtil.isNotNull(operation) ? operation.getAfterMoney() : "0")) {
                account.setBalance(balance);
                account.setFreezeMoney(freeze);
                boolean flag = opAcAfter(operation.getAfterMoney(), operation.getAfterMoney(), account.getLastDt(), account.getLastAcBalDataId(), account.getAccountNo());
                log.info("[执行-统计期末余额][保存期末余额成功, acNo:{} 会计日:{} 余额:{} 冻结:{} 执行保存结果:{}]", acNo, acDt, balance, freeze, flag);
                return flag;
            }

            return false;
        } catch (Exception e) {
            log.error("[执行-统计期末余额][保存期末余额信息时异常:{}, acNo:{} 会计日{}]", e, acNo, acDt);
            return false;
        }

    }

    @Override
    public boolean opAcAfter(String bAfter, String fAfter, String lastDt, String lastAcBalDataId, String acNo) {
        return getBaseMapper().opAcAfter(bAfter, fAfter, lastDt, lastAcBalDataId, acNo) > 0;
    }

    @Override
    public boolean opAcBal(int opType, String acAmt, String extraAmt, String lastDt, String lastAcBalDataId, String acNo) {

        int executeCount = 0;
        switch (opType) {
            case 1:
                //加余额
                executeCount = getBaseMapper().addCash(acAmt, lastDt, lastAcBalDataId, acNo);
                break;
            case 2:
                //加冻结
                executeCount = getBaseMapper().addUncash(acAmt, lastDt, lastAcBalDataId, acNo);
                break;
            case 3:
                //减余额
                executeCount = getBaseMapper().subtractCash(acAmt, lastDt, lastAcBalDataId, acNo);
                break;
            case 4:
                //减冻结
                executeCount = getBaseMapper().subtractUncash(acAmt, lastDt, lastAcBalDataId, acNo);
                break;
            case 5:
                //加余额-带手续费
                executeCount = getBaseMapper().addCashWithFee(acAmt, extraAmt, lastDt, lastAcBalDataId, acNo);
                break;
            case 6:
                //减余额-带手续费
                executeCount = getBaseMapper().subtractCashWithFee(acAmt, extraAmt, lastDt, lastAcBalDataId, acNo);
                break;

            default:
                executeCount = 0;
                break;
        }
        return executeCount > 0;

    }

    private boolean regAcbalDataOnline(String orderNo, String acBalDataId, String accountNo, String operationAmount, String operationFee,
                                       String beforeMoney, String afterMoney, String featureType, String operationType,
                                       String lastDt, String accType, String vochType, String remark) {

        BaseOperation baseOperation = new BaseOperation();
        //系统订单号
        baseOperation.setAccountNo(accountNo);
        baseOperation.setAcBalDataId(acBalDataId);
        baseOperation.setOperationNo(orderNo);
        baseOperation.setOperationAmount(operationAmount);
        baseOperation.setOperationFee(StrUtil.isBlank(operationFee) ? "0" : operationFee);
        baseOperation.setBeforeMoney(beforeMoney);
        baseOperation.setAfterMoney(afterMoney);
        baseOperation.setFeatureType(featureType);
        baseOperation.setOperationType(operationType);
        baseOperation.setAccountType(accType);
        baseOperation.setVochType(vochType);
        baseOperation.setLastDt(lastDt);
        baseOperation.setOperationRemark(remark);
        return iBaseOperationService.save(baseOperation);
    }

    //保存流水信息，并发情况会引发幻读，后面会用mq消息队列顺序发放解决，分步式部署情况，消费者记得枷锁redis锁即可
    private boolean saveOperations(Account account, String balance, String freeze, String optAmt) {

        List<BaseOperation> baseOperations = new ArrayList<>();

        BaseOperation operationB = new BaseOperation();
        operationB.setAccountNo(account.getAccountNo());
        operationB.setAcBalDataId(account.getLastAcBalDataId());
        operationB.setOperationNo(Globals.AC_RZB_PREFIX + IdGenerator.nextId());
        operationB.setSourceNo(operationB.getOperationNo());
        operationB.setPlatformNo(account.getPlatformNo());
        operationB.setOperationAmount(optAmt);
        operationB.setBeforeMoney(account.getBalance());
        operationB.setAfterMoney(balance);
        operationB.setFeatureType("7日终资金划转流水-余额");
        operationB.setOperationType("PLUS");
        operationB.setOperationRemark("7日终资金划转流水-余额-" + account.getLastDt());
        operationB.setOperationDate(account.getLastDt());
        operationB.setAccountType(account.getType());
        operationB.setVochType("02");

        QueryWrapper<BaseOperation> queryBalance = new QueryWrapper();
        queryBalance.lambda().eq(BaseOperation::getAccountNo, account.getAccountNo());
        queryBalance.lambda().eq(BaseOperation::getOperationAmount, optAmt);
        queryBalance.lambda().eq(BaseOperation::getFeatureType, "7日终资金划转流水-余额");
        queryBalance.lambda().eq(BaseOperation::getOperationType, "PLUS");
        queryBalance.lambda().eq(BaseOperation::getOperationRemark, "7日终资金划转流水-余额-" + account.getLastDt());
        queryBalance.lambda().eq(BaseOperation::getOperationDate, account.getLastDt());

        if (ObjectUtil.isNotNull(iBaseOperationService.getBaseMapper().selectOne(queryBalance))) {
            log.error("[记账中心][7日终资金划转流水-余额账户失败][原因重复划转][operationB操作明细:{}]", operationB);
            return false;
        }

        baseOperations.add(operationB);
        BaseOperation operationF = new BaseOperation();
        operationF.setAccountNo(account.getAccountNo());
        operationF.setAcBalDataId(account.getLastAcBalDataId());
        operationF.setOperationNo(Globals.AC_RZF_PREFIX + IdGenerator.nextId());
        operationF.setSourceNo(operationF.getOperationNo());
        operationF.setPlatformNo(account.getPlatformNo());
        operationF.setOperationAmount(optAmt);
        operationF.setBeforeMoney(account.getFreezeMoney());
        operationF.setAfterMoney(freeze);
        operationF.setFeatureType("7日终资金划转流水-冻结");
        operationF.setOperationType("MINUS");
        operationF.setOperationRemark("7日终资金划转流水-冻结-" + account.getLastDt());
        operationF.setOperationDate(account.getLastDt());
        operationF.setAccountType(account.getType());
        operationF.setVochType("02");

        QueryWrapper<BaseOperation> queryFreeze = new QueryWrapper();
        queryFreeze.lambda().eq(BaseOperation::getAccountNo, account.getAccountNo());
        queryFreeze.lambda().eq(BaseOperation::getOperationAmount, optAmt);
        queryFreeze.lambda().eq(BaseOperation::getFeatureType, "7日终资金划转流水-冻结");
        queryFreeze.lambda().eq(BaseOperation::getOperationType, "MINUS");
        queryFreeze.lambda().eq(BaseOperation::getOperationRemark, "7日终资金划转流水-冻结-" + account.getLastDt());
        queryFreeze.lambda().eq(BaseOperation::getOperationDate, account.getLastDt());

        if (ObjectUtil.isNotNull(iBaseOperationService.getBaseMapper().selectOne(queryFreeze))) {
            log.error("[记账中心][7日终资金划转流水-冻结账户失败][原因重复划转][operationF操作明细:{}]", operationB);
            return false;
        }
        baseOperations.add(operationF);

        return iBaseOperationService.saveBatch(baseOperations);


    }


}
