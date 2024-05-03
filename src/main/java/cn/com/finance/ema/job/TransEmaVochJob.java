package cn.com.finance.ema.job;


import cn.com.finance.ema.config.EmaProperties;
import cn.com.finance.ema.dao.IAccountService;
import cn.com.finance.ema.model.entity.Account;
import cn.com.finance.ema.model.entity.CmmPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * <p>
 * 定时任务处理期末余额
 * </p>
 *
 * @author zhang_sir
 * @version v1.0.0
 * @since 2021/12/09 12:42
 */

@Slf4j
@Component
@EnableScheduling
public class TransEmaVochJob {

    @Autowired
    EmaProperties properties;

    @Autowired
    IAccountService iAccountService;

    /**
     * 定时任务 每天凌晨执行一次
     */
    @Scheduled(cron = "0 0 0 * * ?")
    //@PostConstruct
    public void vochJob() {
        log.info("[期末余额作业开始执行] 当前线程编号：{} , 当前线程名称：{}", Thread.currentThread().getId(), Thread.currentThread().getName());
        int index = 1;
        int pageSize = 200;
        int retries = 1;
        long delay = 300;
        boolean hasNext = false;
        String acDt = LocalDate.now().plusDays(-7).format(DateTimeFormatter.ofPattern("yyyyMMdd")); // 会计日期7日前
        do {
            CmmPage<Account> accounts = iAccountService.queryByPage(index, pageSize, Arrays.asList(properties.getVochType().split(";")));
            log.info("账户数量:{}", accounts.getTotal());
            if (accounts != null && accounts.getDatas() != null) {
                accounts.getDatas().forEach(actInfo -> {
                    String acNo = actInfo.getAccountNo();
                    log.info("[7日终统计] acNo:{}, acDt:{}", acNo, acDt);
                    int temp = 1;
                    boolean success = iAccountService.statistic4Day(acNo, acDt);
                    while (temp <= retries && !success) {
                        success = iAccountService.statistic4Day(acNo, acDt);
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        temp++;
                    }
                });
                hasNext = index < accounts.getPages();
                if (hasNext) {
                    index++;
                }
            }
        } while (hasNext);
        log.info("[期末余额作业执行完毕] 当前线程编号：{} , 当前线程名称：{}", Thread.currentThread().getId(), Thread.currentThread().getName());
    }

}
