package cn.com.finance.ema.interceptor;

import cn.com.finance.ema.dao.ISysIpWhitesService;
import cn.com.finance.ema.enums.CodeEnum;
import cn.com.finance.ema.utils.IpUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * ip白名单拦截器
 *
 * @date: 2020/05/09 17:02
 * @author: zhang_sir
 * @version: 1.0
 */
@Slf4j
@Component
@EnableScheduling
public class IpInterceptor implements HandlerInterceptor {

    private static Set<String> whites = new HashSet<>();

    private final ISysIpWhitesService iSysIpWhitesService;

    public IpInterceptor(ISysIpWhitesService iSysIpWhitesService) {
        this.iSysIpWhitesService = iSysIpWhitesService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String ip = IpUtil.getIpAddress(request);

        if (whites.isEmpty()) {
            log.info("[白名单拦截器] 检查到白名单列表为空");
            return false;
        }

        if (whites.contains(ip) == false) {
            log.info("[白名单拦截器] 当前请求IP=：{}  注意！白名单检查未通过 , 非法ip！", ip);
            error(response);
            return false;
        }

        log.info("[白名单拦截器] 白名单检查通过 , 当前请求IP=：{} ", ip);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }

    private void error(HttpServletResponse response) {

        response.setHeader("Access-Control-Allow-Origin", "-");
        response.setHeader("Access-Control-Allow-Credentials", "false");

        JSONObject result = new JSONObject();
        result.put("respCode", CodeEnum.ILLEGAL_IP.getResCode());
        result.put("respMsg", CodeEnum.ILLEGAL_IP.getResMsg());

        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");

        try {
            writer = response.getWriter();
            writer.write(result.toJSONString());
            writer.flush();
        } catch (IOException e) {
            log.error("返回异常：", e);
        } finally {
            if (writer != null)
                writer.close();
        }

    }

    /**
     * 刷新ip列表到jvm
     * 定时任务 每30分钟执行一次
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void SyncIPJob() {
        log.info("[定时同步白名单IP开始执行] 当前线程编号：{} , 当前线程名称：{}", Thread.currentThread().getId(), Thread.currentThread().getName());
        whites = iSysIpWhitesService.queryAll();
        log.info("[定时同步白名单IP开始执行]  , IP池：{}", ArrayUtil.toString(whites));

    }

    /**
     * 初始化ip列表
     */
    @PostConstruct
    private void initIP() {
        log.info("[初始化IP白名单] 当前线程编号：{} , 当前线程名称：{}", Thread.currentThread().getId(), Thread.currentThread().getName());
        whites = iSysIpWhitesService.queryAll();
        log.info("[初始化IP白名单]  , IP池：{}", ArrayUtil.toString(whites));
    }

}
