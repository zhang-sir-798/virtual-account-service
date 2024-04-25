package cn.com.finance.ema.utils.channel;

import cn.com.finance.ema.constants.Constants;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class LoadProperties {

    private static final Map<String, String> keyMap = MapUtil.newHashMap();


    /**
     * 使用java.util.Properties类的load()方法加载properties文件
     */
    public static String initKeys(String name, String key) {
        String value = "";

        try {

            if (StrUtil.equalsIgnoreCase("privateKey", key)) {

                if (keyMap.get("qbs:source:private:key:" + name) == null) {
                    InputStream inputStream = new BufferedInputStream(new FileInputStream(new File(Constants.QB_RSA_PATH + name + ".properties"))); // 方法1

                    Properties prop = new Properties();

                    prop.load(new InputStreamReader(inputStream, "UTF-8")); // 加载格式化后的流

                    value = prop.getProperty(key);
                    keyMap.put("qbs:source:private:key:" + name, value);
                } else {
                    value = keyMap.get("qbs:source:private:key:" + name);
                }

            } else {
                if (keyMap.get("qbs:source:public:key:" + name) == null) {
                    InputStream inputStream = new BufferedInputStream(new FileInputStream(new File(Constants.QB_RSA_PATH + name + ".properties"))); // 方法1

                    Properties prop = new Properties();

                    prop.load(new InputStreamReader(inputStream, "UTF-8")); // 加载格式化后的流

                    value = prop.getProperty(key);
                    keyMap.put("qbs:source:public:key:" + name, value);
                } else {
                    value = keyMap.get("qbs:source:public:key:" + name);
                }
            }

        } catch (FileNotFoundException e) {
            log.info("properties文件路径有误！");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

}