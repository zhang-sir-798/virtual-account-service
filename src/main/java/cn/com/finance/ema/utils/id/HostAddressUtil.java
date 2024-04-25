package cn.com.finance.ema.utils.id;


import cn.hutool.core.util.StrUtil;

import java.net.InetAddress;

/**
 * @author zhang_sir
 * @version v1.0
 * @since 2019/7/24
 */
public class HostAddressUtil {

    /**
     * ip地址
     */
    private static String ipAddress;

    /**
     * 当前机器IP
     *
     * @return
     */
    public static String ipAddrdss() {
        if (StrUtil.isBlank(ipAddress)) {
            InetAddress addr = null;

            try {
                addr = InetAddress.getLocalHost();
                ipAddress = addr.getHostAddress();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ipAddress;
    }


}

