package cn.com.finance.ema.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 * 异步线程池配置类
 * </p>
 *
 * @author zhang_sir
 * @version v1.0.0
 * @since 2022/04/19 15:25
 */
@Configuration
public class ThreadPoolTaskConfig {

    private static final int corePoolSize = Runtime.getRuntime().availableProcessors() * 2;

    private static final int maxPoolSize = Runtime.getRuntime().availableProcessors() * 4;

    private static final int keepAliveTime = 20;

    private static final int queueCapacity = 500;

    private static final String threadNamePrefix = "Async-Service-";

    @Bean("taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveTime);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

}
