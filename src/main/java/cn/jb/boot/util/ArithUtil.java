package cn.jb.boot.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

/**
 * @author xyb
 * @Description
 * @Date 2022/8/13 21:49
 */
public class ArithUtil {
    /**
     * 0
     */
    private static final int ZERO = 0;

    /**
     * 目的： 提供精确的加法运算。
     *
     * @param ds 需要求和的多个参数
     * @return 多个参数相加后的和
     */
    public static BigDecimal add(BigDecimal... ds) {
        BigDecimal bd = new BigDecimal(ZERO);
        int t = ds.length;
        for (int i = ZERO; i < t; i++) {
            bd = bd.add(ds[i]);
        }
        return bd;
    }
    public static BigDecimal add(List<BigDecimal> ds){
        return add(ds.toArray(new BigDecimal[0]));
    }


    /**
     * 提供精确的减法运算。
     *
     * @param ds 需要相减的参数，多参数
     * @return 多个参数相减后的差
     */
    public static BigDecimal sub(BigDecimal... ds) {
        BigDecimal bd = new BigDecimal(String.valueOf(ds[ZERO]));
        int t = ds.length;
        for (int i = 1; i < t; i++) {
            bd = bd.subtract(ds[i]);
        }
        return bd;
    }

    /**
     * 目的：提供精确的乘法运算。
     *
     * @param ds 需要求积德多参数
     * @return 相乘后的积
     */
    public static BigDecimal mul(BigDecimal... ds) {
        int t = ds.length;
        BigDecimal bd = new BigDecimal("1");
        for (int i = ZERO; i < t; i++) {
            bd = bd.multiply(ds[i]);
        }
        return bd.setScale(3, RoundingMode.DOWN);
    }

    public static double mul_v1(BigDecimal... ds) {
        int t = ds.length;
        BigDecimal bd = new BigDecimal("1");
        for (int i = ZERO; i < t; i++) {
            bd = bd.multiply(ds[i]);
        }

        return bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static boolean lessZero(BigDecimal... ds) {
        return sub(ds).doubleValue() < ZERO;
    }

    public static boolean isInteger(BigDecimal bd) {
        try {
            bd.toBigIntegerExact();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static int upInt(BigDecimal bd) {
        return bd.setScale(ZERO, BigDecimal.ROUND_UP).intValue();
    }

    public static boolean less(BigDecimal source, BigDecimal target) {
        if (Objects.isNull(source)) {
            source = BigDecimal.ZERO;
        }
        if (Objects.isNull(target)) {
            target = BigDecimal.ZERO;
        }
        return source.compareTo(target) < 0;
    }

    public static boolean gt(BigDecimal source, BigDecimal target) {
        if (Objects.isNull(source)) {
            source = BigDecimal.ZERO;
        }
        if (Objects.isNull(target)) {
            target = BigDecimal.ZERO;
        }
        return source.compareTo(target) > 0;
    }

    public static boolean ge(BigDecimal source, BigDecimal target) {
        if (Objects.isNull(source)) {
            source = BigDecimal.ZERO;
        }
        if (Objects.isNull(target)) {
            target = BigDecimal.ZERO;
        }
        return source.compareTo(target) >= 0;
    }

    public static void main(String[] args) {

    }


}
