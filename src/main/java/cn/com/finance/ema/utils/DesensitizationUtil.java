package cn.com.finance.ema.utils;

import cn.hutool.core.util.StrUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 脱敏工具类
 * </p>
 *
 * @author zhang_sir
 * @version v1.0.0
 * @since 2022/06/21 15:13
 */
public class DesensitizationUtil {
    //手机号中间4位*处理
    public static String mobile(String mobile) {
        if (StrUtil.isEmpty(mobile) || (mobile.length() != 11)) {
            return mobile;
        }
        return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }
    //第一位字符显示，其他的***处理

    public static String employeeName(String employeeName) {
        Pattern p = Pattern.compile(".{1}");
        if (StrUtil.isEmpty(employeeName)) {
            return employeeName;
        }
        if (employeeName.length() > 1) {
            StringBuffer sb = new StringBuffer();
            Matcher m = p.matcher(employeeName);
            int i = 0;
            while (m.find()) {
                i++;
                if (i == 1)
                    continue;
                m.appendReplacement(sb, "*");
            }
            m.appendTail(sb);
            return sb.toString();
        } else {
            return employeeName;
        }
    }
    //前两位字符，后一位字符显示，中间****处理

    public static String userName(String userName) {
        Pattern p = Pattern.compile(".{1}");
        if (StrUtil.isEmpty(userName)) {
            return userName;
        }
        StringBuffer sb = new StringBuffer();
        Matcher m = p.matcher(userName);
        int i = 0;
        while (m.find()) {
            i++;
            if (i < 3 || i == userName.length())
                continue;
            m.appendReplacement(sb, "*");
        }
        m.appendTail(sb);
        return sb.toString();
    }

}
