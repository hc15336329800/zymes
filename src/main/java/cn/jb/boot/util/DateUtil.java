package cn.jb.boot.util;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * @author YX
 * @Description 日期工具类
 * @Date 2021/8/18 0:11
 */
public class DateUtil {

    /**
     * 日期格式yyyy-MM-dd
     */
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    public static final String yyyyMMdd = "yyyyMMdd";

    public static final String yyyy_MM = "yyyy-MM";

    /**
     * 日期时间格式yyyy-MM-dd HH:mm:ss
     */
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 到分
     */
    public static final String DATA_TIME_MIN = "yyyy-MM-dd HH:mm";
    /**
     * 全
     **/
    public static final String ALL_PATTERN = "yyyyMMddHHmmssSSS";

    /**
     * MDHS
     */
    public static final String MDHS = "MM-dd HH:mm:ss";

    public static final String HH = "HH";


    public static final String HHMMSS = "HH:mm:ss";
    /**
     * 北京时间
     */
    public static final ZoneOffset BJ = ZoneOffset.of("+8");

    /**
     * 构造函数
     */
    private DateUtil() {
        super();
    }

    /**
     * Date转LocalDateTime
     *
     * @param date Date对象
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * LocalDateTime转换为Date
     *
     * @param dateTime LocalDateTime对象
     * @return
     */
    public static Date localDateTimeToDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }


    /**
     * 格式化时间-默认yyyy-MM-dd HH:mm:ss格式
     *
     * @param dateTime LocalDateTime对象
     * @return
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return formatDateTime(dateTime, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 将字符串 转为时间
     *
     * @param input 字符串
     * @return
     */
    public static LocalDateTime parse(String input) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
        return LocalDateTime.parse(input, formatter);
    }

    /**
     * 将字符串 转为时间
     *
     * @param input   字符串
     * @param pattern 格式
     * @return
     */
    public static LocalDate parse(String input, String pattern) {
        if (StringUtils.isBlank(input)) {
            return null;
        }
        if (StringUtils.isBlank(pattern)) {
            pattern = DATE_PATTERN;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(input, formatter);
    }

    /**
     * 按pattern格式化时间-默认yyyy-MM-dd HH:mm:ss格式
     *
     * @param dateTime LocalDateTime对象
     * @param pattern  要格式化的字符串
     * @return
     */
    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        if (StringUtils.isBlank(pattern)) {
            pattern = YYYY_MM_DD_HH_MM_SS;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    public static String formatDate(LocalDate dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        if (StringUtils.isBlank(pattern)) {
            pattern = yyyyMMdd;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    public static String formatTime(LocalTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        if (StringUtils.isBlank(pattern)) {
            pattern = YYYY_MM_DD_HH_MM_SS;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }


    /**
     * 按pattern格式化时间-默认yyyy-MM-dd HH:mm:ss格式
     *
     * @param dateTime LocalDateTime对象
     * @param pattern  要格式化的字符串
     * @return
     */
    public static String formatDateTime(Date dateTime, String pattern) {
        return formatDateTime(dateToLocalDateTime(dateTime), pattern);

    }

    /**
     * 格式化当前时间
     *
     * @param pattern
     * @return
     */
    public static String nowDateTime(String pattern) {
        return formatDateTime(nowDateTime(), pattern);
    }

    /**
     * 当前时间
     *
     * @return
     */
    public static LocalDateTime nowDateTime() {
        return LocalDateTime.now();
    }

    /**
     * 获取今天的00:00:00
     *
     * @return
     */
    public static String getDayStart() {
        return getDayStart(nowDateTime());
    }

    /**
     * 获取今天的23:59:59
     *
     * @return
     */
    public static String getDayEnd() {
        return getDayEnd(nowDateTime());
    }

    /**
     * 获取某天的00:00:00
     *
     * @param dateTime
     * @return
     */
    public static String getDayStart(LocalDateTime dateTime) {
        return formatDateTime(dateTime.with(LocalTime.MIN));
    }

    /**
     * 获取某天的23:59:59
     *
     * @param dateTime
     * @return
     */
    public static String getDayEnd(LocalDateTime dateTime) {
        return formatDateTime(dateTime.with(LocalTime.MAX));
    }

    /**
     * 获取本月第一天的00:00:00
     *
     * @return
     */
    public static String getFirstDayOfMonth() {
        return getFirstDayOfMonth(nowDateTime());
    }

    /**
     * 获取本月最后一天的23:59:59
     *
     * @return
     */
    public static String getLastDayOfMonth() {
        return getLastDayOfMonth(nowDateTime());
    }

    /**
     * 获取某月第一天的00:00:00
     *
     * @param dateTime LocalDateTime对象
     * @return
     */
    public static String getFirstDayOfMonth(LocalDateTime dateTime) {
        return formatDateTime(dateTime.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN));
    }

    /**
     * 获取某月最后一天的23:59:59
     *
     * @param dateTime LocalDateTime对象
     * @return
     */
    public static String getLastDayOfMonth(LocalDateTime dateTime) {
        return formatDateTime(dateTime.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX));
    }

    public static long second(LocalDateTime ldt) {
        return ldt.toEpochSecond(BJ);
    }

}
