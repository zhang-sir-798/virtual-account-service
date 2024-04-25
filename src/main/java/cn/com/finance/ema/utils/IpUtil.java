package cn.com.finance.ema.utils;

import cn.hutool.core.util.StrUtil;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * <p>
 * IpUtil
 * </p>
 *
 * @author zhangsir
 * @version v1.0.0
 * @since 2022/04/19 15:03
 */
public class IpUtil {

    /**
     * 获取Ip地址
     *
     * @param request request
     * @return ip地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        String[] headerArray = new String[]{"X-Real-IP", "X-Forwarded-For", "Proxy-Client-IP"
                , "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
        String ip = null;
        for (String header : headerArray) {
            ip = request.getHeader(header);
            if (StrUtil.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
                if (ip.contains(",")) {
                    ip = ip.split(",")[0];
                }
                break;
            }
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    /**
     * 获取本机IP地址
     *
     * @return ip地址
     */
    public static String getLocalhostIP() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            return addr.getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }
}
