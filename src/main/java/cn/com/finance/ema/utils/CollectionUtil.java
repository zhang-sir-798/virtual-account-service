package cn.com.finance.ema.utils;

import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 集合工具类
 *
 * @author zhang_sir
 * @version 1.0
 * @date 2018/9/11 11:10
 */
public class CollectionUtil {

    private CollectionUtil() {
    }

    public static <T> boolean removeListNullElements(List<T> originList) {
        return originList.removeAll(Collections.singleton(null));
    }

    /**
     * 转换List中的数据类型，只对相同名称的基本类型字段进行复制
     *
     * @param needChangeList 需要转换的list
     * @param clazz          转换的类型
     * @return {@link List<R>}
     * @author zhangsir
     * @date 2019/4/4 16:02
     **/
    public static <T, R> List<R> listTypeChange(List<T> needChangeList, Class<R> clazz) {
        if (CollectionUtils.isNotEmpty(needChangeList)) {
            return needChangeList.stream().map(source -> {
                try {
                    R target = clazz.newInstance();
                    copyPropertiesWithoutNullValue(source, target);
                    return target;
                } catch (Exception e) {
                    return null;
                }
            }).collect(Collectors.toList());
        }
        return ListUtil.empty();
    }

    /**
     * 忽略null值的属性复制，修改的Spring的BeanUtils中的copyProperties方法
     *
     * @param source 源对象
     * @param target 目标对象
     * @author zhangsir
     * @date 2020/7/24 10:55
     **/
    public static void copyPropertiesWithoutNullValue(Object source, Object target) {
        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(target.getClass());
        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null) {
                PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null &&
                            ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                        try {
                            Object value = readMethod.invoke(source);
                            //复制的值为null,则忽略
                            if (value == null) {
                                continue;
                            }
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }

                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            writeMethod.invoke(target, value);
                        } catch (Throwable ex) {
                            throw new FatalBeanException(
                                    "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }
}
