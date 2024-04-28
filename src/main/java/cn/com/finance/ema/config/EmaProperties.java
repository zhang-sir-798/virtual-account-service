package cn.com.finance.ema.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * 配置信息
 * </p>
 *
 * @author zhang_sir
 * @version v1.0.0
 * @since 2022/06/21 15:13
 */
@Data
@Configuration
public class EmaProperties {

    @Value("${ema.sign.publicKey}")
    private String publicKey;

    @Value("${ema.sign.signIgnore}")
    private String signIgnore;

    @Value("${ema.voch.type}")
    private String vochType;

    //QB
    @Value("${ema.qbs.notify.trade}")
    private String qbsTradeNotifyUrl;

    @Value("${ema.qbs.notify.refund}")
    private String qbsRefundNotifyUrl;

    @Value("${ema.qbs.baseUrl}")
    private String qbsBaseUrl;

    @Value("${ema.qbs.publicKey}")
    private String qbsPublicKey;

    @Value("${ema.qbs.privateKey}")
    private String qbsPrivateKey;

    @Value("${ema.cups.zxyUrl}")
    private String zxyUrl;

    @Value("${ema.cups.zxyRefundUrl}")
    private String zxyRefundUrl;




}
