package cn.com.finance.ema.service.impl;

import cn.com.finance.ema.constants.Constants;
import cn.com.finance.ema.constants.Globals;
import cn.com.finance.ema.dao.*;
import cn.com.finance.ema.enums.CodeEnum;
import cn.com.finance.ema.model.entity.*;
import cn.com.finance.ema.model.req.core.*;
import cn.com.finance.ema.service.IAcContextService;
import cn.com.finance.ema.service.IEmaCheckService;
import cn.com.finance.ema.utils.SignOlUtil;
import cn.com.finance.ema.utils.id.IdGenerator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
public class IEmaCheckServiceImpl implements IEmaCheckService {

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
    private final IChannelPlatformService iChannelPlatformService;
    private final IBaseTransferOrderService iBaseTransferOrderService;

    public IEmaCheckServiceImpl(IChannelPlatformService iChannelPlatformService, IGoodsService iGoodsService, IPersonService iPersonService, IAccountService iAccountService, IProductService iProductService, IMerchantService iMerchantService, IAcContextService iAcContextService, IMerchantFeeService iMerchantFeeService, IPlatformFeeService iPlatformFeeService, IBaseTransferOrderService iBaseTransferOrderService, IBaseBatchOrderService iBaseBatchOrderService, IPlatformService iPlatformService) {
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
        this.iChannelPlatformService = iChannelPlatformService;
        this.iBaseTransferOrderService = iBaseTransferOrderService;
    }

    @Override
    public boolean checkOnlineSplitPayInfo(EmaOnlinePayReq req, String reqStr) {
        //1.取代理商秘钥 进行验签
        Platform platform = iPlatformService.getBaseMapper().selectOne(new LambdaQueryWrapper<Platform>().eq(Platform::getPlatformNo, req.getAgentNo()).eq(Platform::getStatus, "0"));
        if (ObjectUtil.isNull(platform)) {
            log.error("[交易请求][前置检查未通过][platform查询结果为空 , 请求参数：{}]", req);
            req.setResCode(CodeEnum.ILLEGAL_PARAM.getResCode());
            req.setResMsg(CodeEnum.ILLEGAL_PARAM.getResMsg() + "，平台信息有误");
            req.setTransStatus(Constants.FAIL);
            return false;
        }
        req.setPublicKey(platform.getReqKey());
        //2.验签
        if (!SignOlUtil.verify(reqStr, req.getPublicKey())) {
            log.error("[交易请求][前置检查未通过][签名验证失败 , 请求参数：{}]", req);
            req.setResCode(CodeEnum.SIGN_ERROR.getResCode());
            req.setResMsg(CodeEnum.SIGN_ERROR.getResMsg());
            req.setTransStatus(Constants.FAIL);
            return false;
        }
        //查询渠道商编
        ChannelPlatform channelPlatform = iChannelPlatformService.getBaseMapper().selectOne(new LambdaQueryWrapper<ChannelPlatform>().eq(ChannelPlatform::getPlatformNo, req.getAgentNo()));
        if (ObjectUtil.isNull(channelPlatform)) {
            log.error("[交易请求][前置检查未通过][channelPlatform查询结果为空 , 请求参数：{}]", req);
            req.setResCode(CodeEnum.ILLEGAL_PARAM.getResCode());
            req.setResMsg(CodeEnum.ILLEGAL_PARAM.getResMsg() + "，平台信息有误");
            req.setTransStatus(Constants.FAIL);
            return false;
        }
        req.setMerNo(channelPlatform.getChannelMerNo());

        if (StrUtil.isEmpty(req.getOrderNo())) {
            log.error("[交易请求][前置检查未通过][订单号为空 , 请求参数：{}]", req);
            req.setResCode(CodeEnum.ILLEGAL_PARAM_ORDER_NULL.getResCode());
            req.setResMsg(CodeEnum.ILLEGAL_PARAM_ORDER_NULL.getResMsg());
            req.setTransStatus(Constants.FAIL);
            return false;
        }

        BaseBatchOrder batchOrder = iBaseBatchOrderService.getBaseMapper().selectOne(new LambdaQueryWrapper<BaseBatchOrder>().eq(BaseBatchOrder::getBatchNo, req.getOrderNo()));
        if (ObjectUtil.isNotNull(batchOrder)) {
            log.error("[交易请求][前置检查未通过][交易流水号重复 , 请求参数：{}]", req);
            req.setResCode(CodeEnum.ILLEGAL_PARAM_ORDER.getResCode());
            req.setResMsg(CodeEnum.ILLEGAL_PARAM_ORDER.getResMsg());
            req.setTransStatus(Constants.FAIL);
            return false;
        }

        //3.限额检查 取哪里 建议验证最外层 总金额
        Product product = iProductService.getBaseMapper().selectOne(new LambdaQueryWrapper<Product>().eq(Product::getProductNo, req.getTrxType()).eq(Product::getStatus, "0"));
        if (ObjectUtil.isNull(product)) {
            log.error("[交易请求][前置检查未通过][product查询结果为空 , 请求参数：{}]", req);
            req.setResCode(CodeEnum.ILLEGAL_PARAM.getResCode());
            req.setResMsg(CodeEnum.ILLEGAL_PARAM.getResMsg() + "，产品信息有误");
            req.setTransStatus(Constants.FAIL);
            return false;
        }

        if (Long.parseLong(req.getAmount()) < Long.parseLong(product.getSingleMinLimit())) {
            log.error("[交易请求][前置检查未通过][单笔小于最小限额, 请求参数：{}]", req);
            req.setResCode(CodeEnum.ILLEGAL_PARAM_AMT.getResCode());
            req.setResMsg(CodeEnum.ILLEGAL_PARAM_AMT.getResMsg() + "，单笔小于最小限额");
            req.setTransStatus(Constants.FAIL);
            return false;
        }

        if (Long.parseLong(req.getAmount()) > Long.parseLong(product.getSingleMaxLimit())) {
            log.error("[交易请求][前置检查未通过][单笔大于最大限额, 请求参数：{}]", req);
            req.setResCode(CodeEnum.ILLEGAL_PARAM_AMT.getResCode());
            req.setResMsg(CodeEnum.ILLEGAL_PARAM_AMT.getResMsg() + "，单笔大于最大限额");
            req.setTransStatus(Constants.FAIL);
            return false;
        }
        req.setTransType("0");

        //4.计算交易手续费  取平台Fee 里面的费率 入手续费账户
        PlatformFee platformFee = iPlatformFeeService.getBaseMapper().selectOne(new LambdaQueryWrapper<PlatformFee>()
                .eq(PlatformFee::getPlatformNo, req.getAgentNo()).eq(PlatformFee::getProductNo, req.getTrxType())
                .eq(PlatformFee::getChannelNo, req.getChannelNo()).eq(PlatformFee::getStatus, "0").eq(PlatformFee::getTsType, "0"));
        if (ObjectUtil.isNull(platformFee)) {
            log.error("[交易请求][前置检查未通过][platformFee查询结果为空 , 请求参数：{}]", req);
            req.setResCode(CodeEnum.ILLEGAL_PARAM.getResCode());
            req.setResMsg(CodeEnum.ILLEGAL_PARAM.getResMsg() + "，平台费率信息有误");
            req.setTransStatus(Constants.FAIL);
            return false;
        }
        PlatformFeeReq platformFeeReq = new PlatformFeeReq();
        platformFeeReq.setOrderAmount(req.getAmount());
        platformFeeReq.setFeeType(platformFee.getFeeType());
        platformFeeReq.setFixedFee(platformFee.getFixedFee());
        platformFeeReq.setRatioFee(platformFee.getRatioFee());
        req.setTransFee(iAcContextService.calcFee(platformFeeReq));

        Long totalTransFee = Long.parseLong(req.getTransFee());
        Long totalTransFeeTemp = 0L;

        //5.取功能账户控制限额
        if (StrUtil.equalsIgnoreCase("ONLINE001", req.getTrxType())) {

            Account funAccount = iAccountService.getBaseMapper().selectOne(new LambdaQueryWrapper<Account>()
                    .eq(Account::getPlatformNo, platform.getPlatformNo()).eq(Account::getStatus, "0").eq(Account::getType, "9"));
            if (ObjectUtil.isNull(funAccount)) {
                log.error("[交易请求][前置检查未通过][功能账户未查询到或已经关闭, 请求参数：{}]", req);
                req.setResCode(CodeEnum.ILLEGAL_PARAM.getResCode());
                req.setResMsg(CodeEnum.ILLEGAL_PARAM.getResMsg() + " , 功能账户未查询到或已经关闭");
                req.setTransStatus(Constants.FAIL);
                return false;
            }
            String checkAmount = iAcContextService.calcAcAmt(funAccount.getBalance(), req.getAmount());
            if (Long.parseLong(checkAmount) < 0L) {
                log.error("[交易请求][前置检查未通过][账户额度不足,无法允许交易 , 请求参数：{}]", req);
                req.setResCode(CodeEnum.ILLEGAL_PARAM_FUNC_AMT.getResCode());
                req.setResMsg(CodeEnum.ILLEGAL_PARAM_FUNC_AMT.getResMsg());
                req.setTransStatus(Constants.FAIL);
                return false;
            }
            req.setFunNo(funAccount.getAccountNo());//功能账户
            req.setSubsidiaryNo(funAccount.getSubsidiaryNo());//子集团编号
        } else {
            //微信支付宝 则没有功能账户 是否直接取他自己的账户当做功能账户
        }

        if (StrUtil.isEmpty(req.getOpenId())) {
            log.error("[交易请求][前置检查未通过][OpenId为空 , 请求参数：{}]", req);
            req.setResCode(CodeEnum.ILLEGAL_PARAM.getResCode());
            req.setResMsg(CodeEnum.ILLEGAL_PARAM.getResMsg() + "，OpenId为空");
            req.setTransStatus(Constants.FAIL);
            return false;
        }
        if (StrUtil.equalsIgnoreCase("ONLINE001", req.getTrxType())) {
            //验证个人用户 openid -> customerNo
            Person person = iPersonService.getBaseMapper().selectOne(new LambdaQueryWrapper<Person>().eq(Person::getPersonNo, req.getOpenId()).eq(Person::getStatus, "0"));
            if (ObjectUtil.isNull(person)) {
                log.error("[交易请求][前置检查未通过][person查询结果为空 , 请求参数：{}]", req);
                req.setResCode(CodeEnum.ILLEGAL_PARAM.getResCode());
                req.setResMsg(CodeEnum.ILLEGAL_PARAM.getResMsg() + "，客户信息有误");
                req.setTransStatus(Constants.FAIL);
                return false;
            }
        }

        //6.查询平台账户号
        Account plateSubAccNo = getPAccNO(req, platform, req.getSubsidiaryNo());
        if (plateSubAccNo == null) {
            log.error("[交易请求][前置检查未通过][平台账户号未查询到 , 请求参数：{}]", req);
            req.setResCode(CodeEnum.ILLEGAL_PARAM.getResCode());
            req.setResMsg(CodeEnum.ILLEGAL_PARAM.getResMsg() + " , 平台账户号未查询到");
            req.setTransStatus(Constants.FAIL);
            return false;
        }
        req.setPlateSubNo(plateSubAccNo.getAccountNo());

        //7.预处理订单 处理分账金额 商户和平台
        PlatformFee platformSplitFee = iPlatformFeeService.getBaseMapper().selectOne(new LambdaQueryWrapper<PlatformFee>()
                .eq(PlatformFee::getPlatformNo, req.getAgentNo())
                .eq(PlatformFee::getProductNo, req.getTrxType())
                .eq(PlatformFee::getChannelNo, req.getChannelNo()).eq(PlatformFee::getStatus, "0").eq(PlatformFee::getTsType, "1"));
        if (ObjectUtil.isNull(platformFee)) {
            log.error("[交易请求][前置检查未通过][platformFee查询结果为空 , 请求参数：{}]", req);
            req.setResCode(CodeEnum.ILLEGAL_PARAM.getResCode());
            req.setResMsg(CodeEnum.ILLEGAL_PARAM.getResMsg() + "，平台信息有误");
            req.setTransStatus(Constants.FAIL);
            return false;
        }

        PlatformFeeReq platformSplitFeeReq = new PlatformFeeReq();
        platformSplitFeeReq.setFeeType(platformSplitFee.getFeeType());
        platformSplitFeeReq.setFixedFee(platformSplitFee.getFixedFee());
        platformSplitFeeReq.setRatioFee(platformSplitFee.getRatioFee());
//        platformFeeReq.setFeeType(platformSplitFee.getFeeType());
//        platformFeeReq.setFixedFee(platformSplitFee.getFixedFee());
//        platformFeeReq.setRatioFee(platformSplitFee.getRatioFee());

        String checkMerAmt = "0";
        String checkItemAmt = "0";

        String splitFee = "0";
        String totalPlatformAmount = "0";
        String totalMerAmount = "0";
        //2.处理特殊商户
        //String specialMer = "0";

        String sysOrderNo = Globals.SELF_ORDER_PREFIX + IdGenerator.nextId();

        List<BaseTransferOrder> baseTransferOrders = new ArrayList<>();
        for (EmaOrderReqSub orderSub : req.getEmaOnlinePayReqSub()) {

            //1.检查商户状态
            Merchant merchant = iMerchantService.getBaseMapper().selectOne(new LambdaQueryWrapper<Merchant>().eq(Merchant::getMerchantNo, orderSub.getSubMerNo()).eq(Merchant::getStatus, "0"));
            if (ObjectUtil.isNull(merchant)) {
                log.error("[交易请求][前置检查未通过][mer查询结果为空或已关停 , 请求参数：{}]", req);
                req.setResCode(CodeEnum.ILLEGAL_PARAM.getResCode());
                req.setResMsg(CodeEnum.ILLEGAL_PARAM.getResMsg() + "，商户信息不存在或已关停");
                req.setTransStatus(Constants.FAIL);
                return false;
            }

            //2.查询商户账户号
            Account merSubAccNo = getMAccNO(orderSub.getSubMerNo(), req);
            if (merSubAccNo == null) {
                log.error("[交易请求][前置检查未通过][商户账户号 ,未查询到 , 请求参数：{}]", req);
                req.setResCode(CodeEnum.ILLEGAL_PARAM.getResCode());
                req.setResMsg(CodeEnum.ILLEGAL_PARAM.getResMsg() + "商户账户号未查询到");
                req.setTransStatus(Constants.FAIL);
                return false;
            }

            //平台分账手续费
            platformSplitFeeReq.setOrderAmount(orderSub.getSubAmount());
            splitFee = iAcContextService.calcExtraAmt(splitFee, iAcContextService.calcFee(platformSplitFeeReq));
            //检查金额 -商户级别
            checkMerAmt = iAcContextService.calcExtraAmt(checkMerAmt, orderSub.getSubAmount());

            for (EmaProductsReqSub productSub : orderSub.getProductList()) {

                BaseTransferOrder order = new BaseTransferOrder();
                order.setOrderNo(productSub.getSubProdTradeNo());
                order.setSourceOrderNo(order.getOrderNo());
                order.setSelfOrderNo(Globals.SELF_ORDER_PREFIX + IdGenerator.nextId());
                order.setSubMerOrderNo(orderSub.getSubTradeNo());
                order.setSubMerTransAmount(orderSub.getSubAmount());
                order.setBatchNo(req.getOrderNo());
                order.setSysOrderNo(sysOrderNo);
                order.setPaymentStatus("0");
                order.setTransType(Constants.TRANS_TYPE_PAY);
                order.setRemark(req.getRemark());
                order.setTrxType(req.getTrxType());
                order.setMerchantNo(orderSub.getSubMerNo());
                order.setChannelNo(req.getChannelNo());
                order.setChannelFee("0");//渠道成本一笔
                order.setPlatformNo(platformFee.getPlatformNo());
                order.setGroupNo(Constants.GROUP_N0);
                order.setProductNo(platformFee.getProductNo());
                order.setGoodsNo(productSub.getProductNo());
                order.setTransNotifyUrl(platform.getTransNotifyUrl());
                order.setCreateTime(LocalDateTime.now());
                if (StrUtil.equalsIgnoreCase("ONLINE001", req.getTrxType())) {
                    order.setResCode(CodeEnum.SUCCESS.getResCode());
                    order.setResMsg(CodeEnum.SUCCESS.getResMsg());
                    order.setTransStatus(Constants.SUCCESS);
                    order.setResultTime(order.getCreateTime());
                } else {
                    order.setResCode(CodeEnum.BUS_TRUE.getResCode());
                    order.setResMsg(CodeEnum.BUS_TRUE.getResMsg());
                    order.setTransStatus(Constants.DOING);
                }
                order.setUpdateTime(order.getCreateTime());
                order.setMerchantSubNo(merSubAccNo.getAccountNo());
                order.setPlateSubNo(req.getPlateSubNo());
                order.setFunNo(req.getFunNo());
                order.setSubsidiaryNo(req.getSubsidiaryNo());
                order.setReqKey(req.getPublicKey());
                order.setIsRefund("0");
                order.setCustomerNo(req.getOpenId());
                order.setTransAmount(productSub.getProductAmount());

                //每一个产品级别子订单
                Goods goods = iGoodsService.getBaseMapper().selectOne(new LambdaQueryWrapper<Goods>().eq(Goods::getGoodsNo, productSub.getProductNo()).eq(Goods::getMerchantNo, orderSub.getSubMerNo()).eq(Goods::getStatus, "0"));
                if (ObjectUtil.isNull(goods)) {
                    log.error("[交易请求][前置检查未通过][goods查询结果为空 , 请求参数：{}]", req);
                    req.setResCode(CodeEnum.ILLEGAL_PARAM.getResCode());
                    req.setResMsg(CodeEnum.ILLEGAL_PARAM.getResMsg() + "，商品产品占比信息有误");
                    req.setTransStatus(Constants.FAIL);
                    return false;
                }

                //产品占比
                order.setBankRatio(goods.getGoodsRate());
                order.setMerchantAmount(iAcContextService.calcSplitAmt(order.getTransAmount(), order.getBankRatio()));//"商户分账金额"
                order.setPlatformAmount(iAcContextService.calcAcAmt(order.getTransAmount(), order.getMerchantAmount()));//"平台分账金额"
                //order.setPlatformFee(splitFee);//平台分账手续费 多笔会一样但是只扣减一次
                //累计平台分账总金额
                totalPlatformAmount = iAcContextService.calcExtraAmt(totalPlatformAmount, order.getPlatformAmount());
                //累计商户分账总金额
                totalMerAmount = iAcContextService.calcExtraAmt(totalMerAmount, order.getMerchantAmount());

                //检查金额 -订单级别
                checkItemAmt = iAcContextService.calcExtraAmt(checkItemAmt, productSub.getProductAmount());

                platformFeeReq.setOrderAmount(order.getTransAmount());
                platformFeeReq.setFeeType(platformFee.getFeeType());
                platformFeeReq.setFixedFee(platformFee.getFixedFee());
                platformFeeReq.setRatioFee(platformFee.getRatioFee());
                order.setTransFee(iAcContextService.calcFee(platformFeeReq));
                totalTransFeeTemp = totalTransFeeTemp + Long.parseLong(iAcContextService.calcFee(platformFeeReq));

                baseTransferOrders.add(order);
            }

        }
        req.setSplitFee(splitFee);
        //试算平衡
        Long feeResult = totalTransFee - totalTransFeeTemp;
        if (feeResult != 0L) {
            //条处理手续费
            String feeTemp = baseTransferOrders.get(0).getTransFee();
            baseTransferOrders.get(0).setTransFee(iAcContextService.calcExtraAmt(feeTemp, String.valueOf(feeResult)));
        }
        //8.处理批次表
        BaseBatchOrder baseBatchOrder = new BaseBatchOrder();
        baseBatchOrder.setBatchNo(req.getOrderNo());
        baseBatchOrder.setSourceBatchNo(req.getOrderNo());
        baseBatchOrder.setSysOrderNo(sysOrderNo);
        baseBatchOrder.setChannelBatchNo(Globals.CH_ORDER_PREFIX + IdGenerator.nextId());
        baseBatchOrder.setChannelNo(req.getChannelNo());
        baseBatchOrder.setFunNo(req.getFunNo());
        baseBatchOrder.setSuperMerNo(req.getMerNo());
        baseBatchOrder.setPlatformNo(platform.getPlatformNo());
        baseBatchOrder.setPlateSubNo(req.getPlateSubNo());
        baseBatchOrder.setSubsidiaryNo(req.getSubsidiaryNo());
        baseBatchOrder.setGroupNo(Constants.GROUP_N0);
        baseBatchOrder.setProductNo(product.getProductNo());
        baseBatchOrder.setTotalAmount(req.getAmount());
        //交易手续配 一笔 平台承担
        baseBatchOrder.setTotalFee(req.getTransFee());
        //平台分账手续费 一笔 多比子订单合并而成 平台承担
        baseBatchOrder.setTotalSplitFee(req.getSplitFee());

        //订单记录金额是否扣减v0.1
        baseBatchOrder.setTotalPlatformAmount(totalPlatformAmount);
        //订单记录金额是否扣减20220510 暂定订单表不扣手续费
        //baseBatchOrder.setTotalPlatformAmount(iAcContextService.calcAcAmt(totalPlatformAmount, req.getTransFee()));
        baseBatchOrder.setTotalMerAmount(totalMerAmount);
        baseBatchOrder.setChannelFee("0");
        baseBatchOrder.setTransNotifyUrl(platform.getTransNotifyUrl());
        baseBatchOrder.setCreateTime(LocalDateTime.now());
        if (StrUtil.equalsIgnoreCase("ONLINE001", req.getTrxType())) {
            baseBatchOrder.setResCode(CodeEnum.SUCCESS.getResCode());
            baseBatchOrder.setResMsg(CodeEnum.SUCCESS.getResMsg());
            baseBatchOrder.setTransStatus(Constants.SUCCESS);
            baseBatchOrder.setResultTime(baseBatchOrder.getCreateTime());
        } else {
            baseBatchOrder.setResCode(CodeEnum.BUS_TRUE.getResCode());
            baseBatchOrder.setResMsg(CodeEnum.BUS_TRUE.getResMsg());
            baseBatchOrder.setTransStatus(Constants.DOING);
        }
        baseBatchOrder.setUpdateTime(baseBatchOrder.getCreateTime());
        baseBatchOrder.setTransType(Constants.TRANS_TYPE_PAY);
        baseBatchOrder.setTrxType(req.getTrxType());
        baseBatchOrder.setReqKey(req.getPublicKey());
        baseBatchOrder.setRemark(req.getBody());
        //处理 产品级别子订单
        req.setBaseTransferOrders(baseTransferOrders);
        //处理 批次级别订单
        req.setBaseBatchOrder(baseBatchOrder);

        //2.处理特殊商户 不检查金额一致性
//        if (!StrUtil.equalsIgnoreCase("1", specialMer)) {
//            //检查金额 商户级别
//            if (!StrUtil.equals(req.getAmount(), checkMerAmt)) {
//                log.error("[交易请求][前置检查未通过][总金额与子商户金额合计不一致, 请求参数：{}]", req);
//                req.setResCode(CodeEnum.ILLEGAL_PARAM_AMT.getResCode());
//                req.setResMsg(CodeEnum.ILLEGAL_PARAM_AMT.getResMsg() + "，总金额与子商户金额合计不一致");
//                req.setTransStatus(Constants.FAIL);
//                return false;
//            }
//            //检查金额 订单级别
//            if (!StrUtil.equals(req.getAmount(), checkItemAmt)) {
//                log.error("[交易请求][前置检查未通过][总金额与子产品金额合计不一致, 请求参数：{}]", req);
//                req.setResCode(CodeEnum.ILLEGAL_PARAM_AMT.getResCode());
//                req.setResMsg(CodeEnum.ILLEGAL_PARAM_AMT.getResMsg() + "，总金额与子产品金额合计不一致");
//                req.setTransStatus(Constants.FAIL);
//                return false;
//            }
//        }

        //检查金额 商户级别
        if (!StrUtil.equals(req.getAmount(), checkMerAmt)) {
            log.error("[交易请求][前置检查未通过][总金额与子商户金额合计不一致, 请求参数：{}]", req);
            req.setResCode(CodeEnum.ILLEGAL_PARAM_AMT.getResCode());
            req.setResMsg(CodeEnum.ILLEGAL_PARAM_AMT.getResMsg() + "，总金额与子商户金额合计不一致");
            req.setTransStatus(Constants.FAIL);
            return false;
        }
        //检查金额 订单级别
        if (!StrUtil.equals(req.getAmount(), checkItemAmt)) {
            log.error("[交易请求][前置检查未通过][总金额与子产品金额合计不一致, 请求参数：{}]", req);
            req.setResCode(CodeEnum.ILLEGAL_PARAM_AMT.getResCode());
            req.setResMsg(CodeEnum.ILLEGAL_PARAM_AMT.getResMsg() + "，总金额与子产品金额合计不一致");
            req.setTransStatus(Constants.FAIL);
            return false;
        }

        return true;
    }

    @Override
    public boolean checkOnlineRefundInfo(EmaOnlineRefundReq req, String reqStr) {
        //1.取代理商秘钥 进行验签
        Platform platform = iPlatformService.getBaseMapper().selectOne(new LambdaQueryWrapper<Platform>().eq(Platform::getPlatformNo, req.getAgentNo()).eq(Platform::getStatus, "0"));
        if (ObjectUtil.isNull(platform)) {
            log.error("[交易请求][前置检查未通过][platformFee查询结果为空 , 请求参数：{}]", req);
            req.setResCode(CodeEnum.ILLEGAL_PARAM.getResCode());
            req.setResMsg(CodeEnum.ILLEGAL_PARAM.getResMsg() + "，平台信息有误");
            req.setTransStatus(Constants.FAIL);
            return false;
        }
        req.setPublicKey(platform.getReqKey());
        //验签
        if (!SignOlUtil.verify(reqStr, req.getPublicKey())) {
            log.error("[交易请求][前置检查未通过][签名验证失败 , 请求参数：{}]", req);
            req.setResCode(CodeEnum.SIGN_ERROR.getResCode());
            req.setResMsg(CodeEnum.SIGN_ERROR.getResMsg());
            req.setTransStatus(Constants.FAIL);
            return false;
        }

        BaseBatchOrder order = iBaseBatchOrderService.getBaseMapper().selectOne(new LambdaQueryWrapper<BaseBatchOrder>().eq(BaseBatchOrder::getBatchNo, req.getOrderNo()));
        if (ObjectUtil.isNotNull(order)) {
            log.error("[交易请求][前置检查未通过][退款流水号重复 , 请求参数：{}]", req);
            req.setResCode(CodeEnum.ILLEGAL_PARAM_ORDER.getResCode());
            req.setResMsg(CodeEnum.ILLEGAL_PARAM_ORDER.getResMsg());
            req.setTransStatus(Constants.FAIL);
            return false;
        }

        //子产品单号重复检查
        if (req.getSubProdOrders().size() > 1) {
            long count = req.getSubProdOrders().stream().distinct().count();
            if (count < req.getSubProdOrders().size()) {
                log.error("[交易请求][前置检查未通过][退款流水号重复 , 请求参数：{}]", req);
                req.setResCode(CodeEnum.ILLEGAL_DUPLICATION_PARAM_OGR_ORDER.getResCode());
                req.setResMsg(CodeEnum.ILLEGAL_DUPLICATION_PARAM_OGR_ORDER.getResMsg());
                req.setTransStatus(Constants.FAIL);
                return false;
            }
        }
        //查询原始订单
        List<BaseTransferOrder> baseTransferOrders = getTransferOrders(req);
        if (baseTransferOrders.isEmpty()) {
            log.error("[退款请求][前置检查未通过][baseTransferOrder原始订单查询结果为空 , 请求参数：{}]", req);
            req.setResCode(CodeEnum.ILLEGAL_PARAM_OGR_ORDER.getResCode());
            req.setResMsg(CodeEnum.ILLEGAL_PARAM_OGR_ORDER.getResMsg() + "或已完成退款");
            req.setTransStatus(Constants.ONLINREFUNDFAILED);
            return false;
        }
        req.setTrxType(baseTransferOrders.get(0).getTrxType());
        req.setChannelNo(baseTransferOrders.get(0).getChannelNo());
        //n天后禁止退款 取数据库中的 配置信息
        String allowRefundDay = StrUtil.isEmpty(platform.getAllowRefundDay()) ? "29" : platform.getAllowRefundDay();
        if (LocalDate.now().plusDays(-Integer.parseInt(allowRefundDay)).isAfter(baseTransferOrders.get(0).getCreateTime().toLocalDate())) {
            log.error("[退款请求][前置检查未通过][超过最大可退款天数：{}，暂不可退款，订单状态已经结算 , 请求参数：{}]", allowRefundDay, req);
            req.setResCode(CodeEnum.ILLEGAL_PARAM.getResCode());
            req.setResMsg("超过最大可退款天数，暂不可退款");
            req.setTransStatus(Constants.ONLINREFUNDFAILED);
            return false;
        }
        //原始批次订单
        BaseBatchOrder batchOrder = iBaseBatchOrderService.getBaseMapper().selectOne(new LambdaQueryWrapper<BaseBatchOrder>().eq(BaseBatchOrder::getTransStatus, Constants.SUCCESS).eq(BaseBatchOrder::getBatchNo, baseTransferOrders.get(0).getBatchNo()));
        if (ObjectUtil.isNull(batchOrder)) {
            log.error("[交易请求][前置检查未通过][原始批次订单不存在 , 请求参数：{}]", req);
            req.setResCode(CodeEnum.ILLEGAL_PARAM_OGR_ORDER.getResCode());
            req.setResMsg(CodeEnum.ILLEGAL_PARAM_OGR_ORDER.getResMsg());
            req.setTransStatus(Constants.FAIL);
            return false;
        }
        //大商户商编
        req.setMerNo(batchOrder.getSuperMerNo());

        //4.计算交易手续费  取平台Fee 里面的费率 入手续费账户
        PlatformFee platformFee = iPlatformFeeService.getBaseMapper().selectOne(new LambdaQueryWrapper<PlatformFee>().eq(PlatformFee::getPlatformNo, req.getAgentNo()).eq(PlatformFee::getStatus, "0").eq(PlatformFee::getProductNo, baseTransferOrders.get(0).getTrxType()).eq(PlatformFee::getChannelNo, baseTransferOrders.get(0).getChannelNo()).eq(PlatformFee::getTsType, "0"));
        if (ObjectUtil.isNull(platformFee)) {
            log.error("[交易请求][前置检查未通过][platformFee查询结果为空 , 请求参数：{}]", req);
            req.setResCode(CodeEnum.ILLEGAL_PARAM.getResCode());
            req.setResMsg(CodeEnum.ILLEGAL_PARAM.getResMsg() + "，平台信息有误");
            req.setTransStatus(Constants.FAIL);
            return false;
        }
        PlatformFeeReq platformFeeReq = new PlatformFeeReq();
        platformFeeReq.setFeeType(platformFee.getFeeType());
        platformFeeReq.setFixedFee(platformFee.getFixedFee());
        platformFeeReq.setRatioFee(platformFee.getRatioFee());

        String totalMerAmount = "0";
        String totalPlatAmount = "0";
        String totalAmount = "0";
        String totalFee = "0";

        String sysOrderNo = Globals.SELF_ORDER_PREFIX + IdGenerator.nextId();

        //预制订单
        List<BaseTransferOrder> refunds = new ArrayList<>();
        for (BaseTransferOrder payOrder : baseTransferOrders) {
            BaseTransferOrder refund = new BaseTransferOrder();
            refund.setOrderNo("RE" + IdGenerator.nextId());//退款流水号
            refund.setSourceOrderNo(payOrder.getSourceOrderNo());//原始交易单号
            refund.setSelfOrderNo(Globals.SELF_ORDER_PREFIX + IdGenerator.nextId());
            refund.setSysOrderNo(sysOrderNo);
            refund.setBatchNo(req.getOrderNo());
            refund.setProductNo(payOrder.getProductNo());
            refund.setPaymentStatus("0");
            refund.setBankRatio(payOrder.getBankRatio());
            refund.setGoodsNo(payOrder.getGoodsNo());
            refund.setTransType(Constants.TRANS_TYPE_REFUND);
            refund.setTrxType(payOrder.getTrxType());
            refund.setCustomerNo(payOrder.getCustomerNo());
            refund.setFunNo(payOrder.getFunNo());
            refund.setSubsidiaryNo(payOrder.getSubsidiaryNo());
            refund.setMerchantSubNo(payOrder.getMerchantSubNo());
            refund.setPlateSubNo(payOrder.getPlateSubNo());
            refund.setMerchantNo(payOrder.getMerchantNo());
            refund.setChannelNo(payOrder.getChannelNo());
            refund.setChannelFee("0");
            refund.setSubMerOrderNo(payOrder.getSubMerOrderNo());
            refund.setPlatformNo(req.getAgentNo());
            refund.setGroupNo(Constants.GROUP_N0);
            refund.setCreateTime(LocalDateTime.now());

            //-------老版本---------
//            refund.setTransAmount(payOrder.getTransAmount());
//            platformFeeReq.setOrderAmount(refund.getTransAmount());//退款要退交易手续费
//            refund.setTransFee(iAcContextService.calcFee(platformFeeReq));
//            refund.setMerchantAmount(iAcContextService.calcSplitAmt(payOrder.getTransAmount(), payOrder.getBankRatio()));//商户分账金额
//            refund.setPlatformAmount(iAcContextService.calcAcAmt(payOrder.getTransAmount(), payOrder.getMerchantAmount()));//平台分账金额
            //-------老版本--------

            //-------新版本 带试算平衡---------
            refund.setTransAmount(payOrder.getTransAmount());
            refund.setTransFee(payOrder.getTransFee());//退款交易手续费
            refund.setMerchantAmount(payOrder.getMerchantAmount());//商户分账金额
            refund.setPlatformAmount(payOrder.getPlatformAmount());//平台分账金额
            //-------新版本 带试算平衡---------


            if (StrUtil.equalsIgnoreCase("ONLINE001", req.getTrxType())) {
                refund.setResCode(CodeEnum.SUCCESS.getResCode());
                refund.setResMsg(CodeEnum.SUCCESS.getResMsg());
                refund.setTransStatus(Constants.ONLINEREFUNDSUCCESS);
                refund.setResultTime(refund.getCreateTime());
                refund.setIsRefund("1");
                payOrder.setIsRefund("1");
            } else {
                refund.setResCode(CodeEnum.BUS_TRUE.getResCode());
                refund.setResMsg(CodeEnum.BUS_TRUE.getResMsg());
                refund.setTransStatus(Constants.DOING);
                refund.setIsRefund("0");
            }
            refund.setUpdateTime(refund.getCreateTime());
            refund.setTransNotifyUrl(platform.getRefundNotifyUrl());
            refund.setReqKey(req.getPublicKey());
            totalFee = iAcContextService.calcExtraAmt(totalFee, refund.getTransFee());
            totalAmount = iAcContextService.calcExtraAmt(totalAmount, refund.getTransAmount());
            totalMerAmount = iAcContextService.calcExtraAmt(totalMerAmount, refund.getMerchantAmount());
            totalPlatAmount = iAcContextService.calcExtraAmt(totalPlatAmount, refund.getPlatformAmount());

            refunds.add(refund);
        }

        //8.处理批次表
        BaseBatchOrder baseBatchOrder = new BaseBatchOrder();
        baseBatchOrder.setBatchNo(req.getOrderNo());
        baseBatchOrder.setSourceBatchNo(baseTransferOrders.get(0).getBatchNo());
        baseBatchOrder.setSysOrderNo(sysOrderNo);
        baseBatchOrder.setChannelBatchNo(Globals.CH_ORDER_PREFIX + IdGenerator.nextId());
        baseBatchOrder.setChannelNo(baseTransferOrders.get(0).getChannelNo());
        baseBatchOrder.setFunNo(baseTransferOrders.get(0).getFunNo());
        baseBatchOrder.setSuperMerNo(req.getMerNo());
        baseBatchOrder.setPlatformNo(baseTransferOrders.get(0).getPlatformNo());
        baseBatchOrder.setPlateSubNo(baseTransferOrders.get(0).getPlateSubNo());
        baseBatchOrder.setSubsidiaryNo(baseTransferOrders.get(0).getSubsidiaryNo());
        baseBatchOrder.setGroupNo(Constants.GROUP_N0);
        baseBatchOrder.setProductNo(baseTransferOrders.get(0).getProductNo());
        baseBatchOrder.setTotalAmount(totalAmount);
        //交易手续配 一笔 平台承担
        baseBatchOrder.setTotalFee(totalFee);
        //平台分账手续费 一笔 多比子订单合并而成 平台承担
        baseBatchOrder.setTotalSplitFee("0");

        //订单记录金额是否扣减v0.1
        baseBatchOrder.setTotalPlatformAmount(totalPlatAmount);
        baseBatchOrder.setTotalMerAmount(totalMerAmount);
        baseBatchOrder.setChannelFee("0");
        baseBatchOrder.setTransNotifyUrl(platform.getRefundNotifyUrl());
        baseBatchOrder.setCreateTime(LocalDateTime.now());
        if (StrUtil.equalsIgnoreCase("ONLINE001", req.getTrxType())) {
            baseBatchOrder.setResCode(CodeEnum.SUCCESS.getResCode());
            baseBatchOrder.setResMsg(CodeEnum.SUCCESS.getResMsg());
            baseBatchOrder.setTransStatus(Constants.ONLINEREFUNDSUCCESS);
            baseBatchOrder.setResultTime(baseBatchOrder.getCreateTime());
        } else {
            baseBatchOrder.setResCode(CodeEnum.BUS_TRUE.getResCode());
            baseBatchOrder.setResMsg(CodeEnum.BUS_TRUE.getResMsg());
            baseBatchOrder.setTransStatus(Constants.DOING);
        }
        baseBatchOrder.setRemark("退款");
        baseBatchOrder.setTransType(Constants.TRANS_TYPE_REFUND);
        baseBatchOrder.setTrxType(baseTransferOrders.get(0).getTrxType());
        baseBatchOrder.setUpdateTime(baseBatchOrder.getCreateTime());
        baseBatchOrder.setReqKey(req.getPublicKey());

        req.setTotalAmount(totalAmount);
        req.setTotalFee(totalFee);
        req.setFunNo(baseBatchOrder.getFunNo());
        req.setCustomerNo(baseTransferOrders.get(0).getCustomerNo());

        req.setSerialNo(baseBatchOrder.getSysOrderNo());
        req.setRefundNotifyUrl(platform.getRefundNotifyUrl());
        req.setBaseBatchOrder(baseBatchOrder);
        req.setBaseTransferOrders(refunds);
        req.setChannelNo(baseTransferOrders.get(0).getChannelNo());
        req.setSourceOrders(baseTransferOrders);

        return true;
    }

    @Override
    public boolean checkNotice(EmaOnlineNoticeReq req, String reqStr) {

        //1.查询订单
        BaseTransferOrder order = iBaseTransferOrderService.getBaseMapper().selectOne(new LambdaQueryWrapper<BaseTransferOrder>()
                .eq(BaseTransferOrder::getOrderNo, req.getSubProdTradeNo()));
        if (ObjectUtil.isNull(order)) {
            log.error("[发货通知][前置检查未通过][子产品订单号 , 请求参数：{}]", req.getSubProdTradeNo());
            req.setResCode(CodeEnum.ILLEGAL_PARAM.getResCode());
            req.setResMsg(CodeEnum.ILLEGAL_PARAM.getResMsg() + " , 产品级子订单号有误");
            return false;
        }
        //状态 非 交易成功
        if (!StrUtil.equalsIgnoreCase(Constants.SUCCESS, order.getTransStatus())) {
            log.error("[发货通知][前置检查未通过][状态非成功 , 订单：{}]", order);
            req.setResCode(CodeEnum.ILLEGAL_ORDER_STATUS.getResCode());
            req.setResMsg(CodeEnum.ILLEGAL_ORDER_STATUS.getResMsg());
            return false;
        }

        //2.取代理商秘钥 进行验签
        Platform platform = iPlatformService.getBaseMapper().selectOne(new LambdaQueryWrapper<Platform>().eq(Platform::getPlatformNo, order.getPlatformNo()).eq(Platform::getStatus, "0"));
        if (ObjectUtil.isNull(platform)) {
            log.error("[发货通知][前置检查未通过][platform查询结果为空 , 请求参数：{}]", req);
            req.setResCode(CodeEnum.ILLEGAL_PARAM.getResCode());
            req.setResMsg(CodeEnum.ILLEGAL_PARAM.getResMsg() + "，平台信息有误");
            return false;
        }
        req.setPublicKey(platform.getReqKey());

        //3.验签
        if (!SignOlUtil.verify(reqStr, req.getPublicKey())) {
            log.error("[发货通知][前置检查未通过][签名验证失败 , 请求参数：{}]", req);
            req.setResCode(CodeEnum.SIGN_ERROR.getResCode());
            req.setResMsg(CodeEnum.SIGN_ERROR.getResMsg());
            return false;
        }

        req.setOrder(order);
        req.setResCode(CodeEnum.SUCCESS.getResCode());
        req.setResMsg(CodeEnum.SUCCESS.getResMsg());

        return true;
    }

    private Account getPAccNO(EmaOnlinePayReq req, Platform platform, String subsidiaryNo) {
        Account plateSubAccNo;

        if (StrUtil.equalsIgnoreCase("ONLINE001", req.getTrxType())) {
            //线上余额类
            plateSubAccNo = iAccountService.getBaseMapper().selectOne(new LambdaQueryWrapper<Account>()
                    .eq(Account::getPlatformNo, platform.getPlatformNo())
                    .eq(Account::getStatus, "0")
                    .eq(Account::getSubsidiaryNo, subsidiaryNo)
                    .eq(Account::getType, "13"));
        } else {
            //微信支付宝
            plateSubAccNo = iAccountService.getBaseMapper().selectOne(new LambdaQueryWrapper<Account>()
                    .eq(Account::getPlatformNo, platform.getPlatformNo()).eq(Account::getStatus, "0").eq(Account::getType, "12"));
        }

        return plateSubAccNo;
    }

    private Account getMAccNO(String merNo, EmaOnlinePayReq req) {
        Account merSubAccNo;

        if (StrUtil.equalsIgnoreCase("ONLINE001", req.getTrxType())) {
            //线上余额类
            merSubAccNo = iAccountService.getBaseMapper().selectOne(new LambdaQueryWrapper<Account>()
                    .eq(Account::getMerchantNo, merNo)
                    .eq(Account::getStatus, "0")
                    .eq(Account::getSubsidiaryNo, req.getSubsidiaryNo())
                    .eq(Account::getType, "11"));

        } else {
            //微信支付宝
            merSubAccNo = iAccountService.getBaseMapper().selectOne(new LambdaQueryWrapper<Account>()
                    .eq(Account::getMerchantNo, merNo).eq(Account::getStatus, "0").eq(Account::getType, "10"));
        }
        return merSubAccNo;
    }

    private List<BaseTransferOrder> getTransferOrders(EmaOnlineRefundReq req) {

        LambdaQueryWrapper<BaseTransferOrder> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(BaseTransferOrder::getSourceOrderNo, req.getSubProdOrders());
        queryWrapper.eq(BaseTransferOrder::getTransStatus, Constants.SUCCESS);
        queryWrapper.eq(BaseTransferOrder::getIsRefund, "0");

        if (StrUtil.isNotEmpty(req.getOriOrderNo())) {
            queryWrapper.eq(BaseTransferOrder::getBatchNo, req.getOriOrderNo());
        }

        return iBaseTransferOrderService.getBaseMapper().selectList(queryWrapper);
    }

}
