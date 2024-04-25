package cn.com.finance.ema.utils;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 * 基本计算工具类
 * 默认0精度，银行舍入法( 四舍六入五考虑，五后非零就进一，五后为零看奇偶，五前为偶应舍去，五前为奇要进一 )
 * </p>
 *
 * @author zhangsir
 * @version v1.0.0
 * @since 2021/12/19 14:43
 */
public class CalcUtil {

    /**
     * 进一法
     */
    public final static int ROUND_UP = 0;
    /**
     * 舍尾法
     */
    public final static int ROUND_DOWN = 1;
    /**
     * 正数进一，负数舍尾
     */
    public final static int ROUND_CEILING = 2;

    /**
     * 正数舍尾，负数进一
     */
    public final static int ROUND_FLOOR = 3;

    /**
     * 四舍五入法
     */
    public final static int ROUND_HALF_UP = 4;

    /**
     * 五舍六入法
     */
    public final static int ROUND_HALF_DOWN = 5;

    /**
     * 银行舍入法( 四舍六入五考虑，五后非零就进一，五后为零看奇偶，五前为偶应舍去，五前为奇要进一 )
     */
    public final static int ROUND_HALF_EVEN = 6;

    /**
     * 已经确定计算结果是精确的，比如1.0正常返回1，结果是1.1则抛出ArithmeticException异常
     */
    public final static int ROUND_UNNECESSARY = 7;

    /**
     * 默认精度，加减乘除的结果，不指定精度时，则使用此默认精度
     */
    private int defaultScaleLevel = 0;

    /**
     * 默认舍入法，加减乘除的结果，不指定舍入法时，则使用此默认舍入法
     */
    private int defaultRoundingMode = ROUND_HALF_UP;

    /**
     * 计算结果
     */
    private BigDecimal x;

    //一个对象被多个线程使用时，锁住阻塞
    ReentrantLock reentrantLock = new ReentrantLock();

    private CalcUtil(){}

    /**
     * 初始数值，基于此数加减乘除
     * @param srcNum
     */
    public CalcUtil(String srcNum ){
        x = new BigDecimal( srcNum );
    }

    /**
     * 初始化数值，基于此数加减乘除<br/>
     * 并指定默认精度和相应舍入法，在计算过程中未指定精度和舍入法时，则使用默认配置
     * @param srcNum
     * @param defaultScaleLevel
     * @param defaultRoundingMode
     */
    public CalcUtil(String srcNum, int defaultScaleLevel, int defaultRoundingMode ){
        x = new BigDecimal( srcNum );
        this.defaultScaleLevel = defaultScaleLevel;
        this.defaultRoundingMode = defaultRoundingMode;
    }

    /**
     * 设置默认精度和舍入法<br/>
     * 在计算过程中未指定精度和舍入法时，则使用此默认配置
     * @param defaultScaleLevel     默认精度
     * @param defaultRoundingMode   默认舍入法，参考CalcUtil.ROUND_XXXX
     */
    public void setDefaultScale(int defaultScaleLevel, int defaultRoundingMode){
        reentrantLock.lock();
        try {
            this.defaultScaleLevel = defaultScaleLevel;
            this.defaultRoundingMode = defaultRoundingMode;
        } finally {
            reentrantLock.unlock();
        }
    }


    /**
     *  加法
     * @param augend            被加数
     * @param scaleLevel        精度级别，例如2代表保留2位小数，0则不保留小数
     * @param roundingMode      舍入法，参考CalcUtil.ROUND_XXXX
     * @return
     */
    public CalcUtil add(String augend, int scaleLevel, int roundingMode){
        reentrantLock.lock();
        try {
            x = x.add( new BigDecimal( augend ) ).setScale( scaleLevel, roundingMode);
            return this;
        } finally {
            reentrantLock.unlock();
        }
    }

    /**
     *  加法
     * @param augend            被加数
     * @return
     */
    public CalcUtil add(String augend){
        reentrantLock.lock();
        try {
            x = x.add( new BigDecimal( augend ) ).setScale( defaultScaleLevel, defaultRoundingMode);
            return this;
        } finally {
            reentrantLock.unlock();
        }
    }

    /**
     * 减法
     * @param minuend           被减数
     * @param scaleLevel        精度级别，例如2代表保留2位小数，0则不保留小数
     * @param roundingMode      舍入法，参考CalcUtil.ROUND_XXXX
     * @return
     */
    public CalcUtil subtract(String minuend, int scaleLevel, int roundingMode){
        reentrantLock.lock();
        try {
            x = x.subtract( new BigDecimal( minuend ) ).setScale( scaleLevel, roundingMode );
            return this;
        } finally {
            reentrantLock.unlock();
        }
    }

    /**
     * 减法，使用默认精度和舍入法
     * @param minuend           被减数
     * @return
     */
    public CalcUtil subtract(String minuend){
        reentrantLock.lock();
        try {
            x = x.subtract( new BigDecimal( minuend ) ).setScale( defaultScaleLevel, defaultRoundingMode );
            return this;
        } finally {
            reentrantLock.unlock();
        }
    }

    /**
     * 乘法
     * @param multiplicand      被乘数
     * @param scaleLevel        精度级别，例如2代表保留2位小数，0则不保留小数
     * @param roundingMode      舍入法，参考CalcUtil.ROUND_XXXX
     * @return
     */
    public CalcUtil multiply(String multiplicand, int scaleLevel, int roundingMode){
        reentrantLock.lock();
        try {
            x = x.multiply( new BigDecimal( multiplicand ) ).setScale( scaleLevel, roundingMode );
            return this;
        } finally {
            reentrantLock.unlock();
        }
    }

    /**
     * 乘法
     * @param multiplicand      被乘数
     * @return
     */
    public CalcUtil multiply(String multiplicand){
        reentrantLock.lock();
        try {
            x = x.multiply( new BigDecimal( multiplicand ) ).setScale( defaultScaleLevel, defaultRoundingMode );
            return this;
        } finally {
            reentrantLock.unlock();
        }
    }

    /**
     * 除法
     * @param divisor           被除数
     * @param scaleLevel        精度级别，例如2代表保留2位小数，0则不保留小数
     * @param roundingMode      舍入法，参考CalcUtil.ROUND_XXXX
     * @return
     */
    public CalcUtil divide(String divisor, int scaleLevel, int roundingMode){
        reentrantLock.lock();
        try {
            x = x.divide( new BigDecimal( divisor ), scaleLevel, roundingMode );
            return this;
        } finally {
            reentrantLock.unlock();
        }
    }

    /**
     * 除法
     * @param divisor           被除数
     * @return
     */
    public CalcUtil divide(String divisor){
        reentrantLock.lock();
        try {
            x = x.divide( new BigDecimal( divisor ), defaultScaleLevel, defaultRoundingMode );
            return this;
        } finally {
            reentrantLock.unlock();
        }
    }

    /**
     * 获取计算结果，按计算过程中定义的精度和舍入法
     * @return
     */
    public BigDecimal getResult(){
        reentrantLock.lock();
        try {
            return x;
        } finally {
            reentrantLock.unlock();
        }
    }

    /**
     * 获取最终结果，指定精度和舍入法
     * @param scaleLevel        精度级别，例如2代表保留2位小数，0则不保留小数
     * @param roundingMode      舍入法，参考CalcUtil.ROUND_XXXX
     * @return
     */
    public BigDecimal getResult( int scaleLevel, int roundingMode ){
        reentrantLock.lock();
        try {
            return x.setScale(scaleLevel, roundingMode);
        } finally {
            reentrantLock.unlock();
        }
    }

}
