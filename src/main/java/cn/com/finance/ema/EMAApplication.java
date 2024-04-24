package cn.com.finance.ema;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@MapperScan("cn.com.finance.ema.mapper")
public class EMAApplication {
    public static void main(String[] args) {
        SpringApplication.run(EMAApplication.class, args);
    }
}
