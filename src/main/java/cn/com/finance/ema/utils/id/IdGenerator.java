package cn.com.finance.ema.utils.id;


import cn.hutool.core.util.StrUtil;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author zhang_sir
 * @version v1.0
 * @since 2019/7/24
 */
public final class IdGenerator {
    private static Map<String, SnowflakeIdWorker> idWorkerMap = new HashMap();

    private static String hostAddress = HostAddressUtil.ipAddrdss();

    private IdGenerator() {
    }

    private static final int getProcessID() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return Integer.valueOf(runtimeMXBean.getName().split("@")[0])
                .intValue();
    }

    public static String next() {
        return nextId().toString();
    }

    public static Long nextId() {
        return nextId(getWorkId(), getWorkId());
    }

    public static Long nextId(long workerId, long datacenterId) {
        String key = workerId + "_" + datacenterId;
        SnowflakeIdWorker idWorker = idWorkerMap.get(key);
        if (null == idWorker) {
            idWorker = new SnowflakeIdWorker(workerId, datacenterId);
            idWorkerMap.put(key, idWorker);
        }

        return idWorker.nextId();
    }

    /**
     * 使用服务器hostName生成workId
     *
     * @return
     */
    private static Long getWorkId() {
        try {
            int[] ints = stringConvertInt(hostAddress);
            int sums = getProcessID();
            for (int b : ints) {
                sums += b;
            }

            return (long) (sums % 32);
        } catch (Exception e) {
            // 如果获取失败，则使用随机数备用
            long rangeLong = 1 + (((long) (new Random().nextDouble() * (31))));
            return rangeLong;
        }
    }

    private static int[] stringConvertInt(String value) {
        int[] intArr = new int[0];
        if (StrUtil.isBlank(value)) {
            intArr = new int[0];
        } else {
            String[] valueArr = value.split(",");
            intArr = new int[valueArr.length];
            for (int i = 0; i < valueArr.length; i++) {
                intArr[i] = Integer.parseInt(valueArr[i]);
            }
        }
        return intArr;
    }


}
