package cn.com.finance.ema.config;

import cn.com.finance.ema.interceptor.IpInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.nio.charset.Charset;
import java.util.List;


/**
 * <p>
 * 注册器配置类
 * </p>
 *
 * @author zhang_sir
 * @version v1.0.0
 * @since 2022/04/19 15:25
 */
@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport {

    private final IpInterceptor ipInterceptor;

    public InterceptorConfig(IpInterceptor ipInterceptor) {
        this.ipInterceptor = ipInterceptor;
    }

    /**
     * 配置ip拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //白名单拦截器url配置
        registry.addInterceptor(ipInterceptor)

                //添加需要验证登录用户操作权限的请求
                //"/**"是对所有的访问拦截
                .addPathPatterns("/**")

                //排除不需要验证登录用户操作权限的请求
                .excludePathPatterns("/css/**")
                .excludePathPatterns("/js/**")
                .excludePathPatterns("/images/**");

    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter converter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        converters.add(converter);
    }
}
