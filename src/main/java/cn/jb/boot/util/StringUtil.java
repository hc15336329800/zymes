package cn.jb.boot.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;

import java.security.SecureRandom;
import java.util.Optional;

/**
 * @author YX
 * @Description 字符串工具类
 * @Date 2021/8/18 0:11
 */
public class StringUtil {

    private static final String ALL_STR = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final SecureRandom RANDOM = new SecureRandom();

    public static final String UTF8 = "UTF-8";

    public static final String MD5 = "MD5";

    public static final String HMACSHA256 = "HMACSHA256";

    public static final String ISO_1 = "ISO8859-1";

    public static final String COMMA = ",";

    private static final String FIELD_SIGN = "sign";

    private static final String ORDER_PREFIX = "E";

    private static final String REFUND_PREFIX = "R";

    public static final String WORK_CENTER_PREFIX = "wc_";
    public static final String BOM_PREFIX = "bom";


    private static StringBuffer randNo;


    private static final Logger LOG = LoggerFactory.getLogger(StringUtil.class);

    /**
     * 获取随机数 大小写字母数字
     *
     * @param len
     * @return
     */
    public static String randomStr(int len) {
        StringBuffer s = new StringBuffer();
        int l = ALL_STR.length();
        int n = 0;
        for (int i = 0; i < len; i++) {
            n = RANDOM.nextInt(l);
            s.append(ALL_STR.charAt(n));
        }
        return s.toString();
    }

    /**
     * 获取随机数 数字
     *
     * @param len
     * @return
     */
    public static String randomNum(int len) {
        StringBuffer s = new StringBuffer();
        int strLen = 10;
        int n;
        for (int i = 0; i < len; i++) {
            n = RANDOM.nextInt(strLen);
            s.append(ALL_STR.substring(0, strLen).charAt(n));
        }
        return s.toString();
    }

    /**
     * 获取随机对象
     *
     * @return
     */
    public static SecureRandom random() {
        return RANDOM;
    }

    /**
     * 获取随机整数
     *
     * @param bound
     * @return
     */
    public static int randomInt(int bound) {
        return RANDOM.nextInt(bound);
    }

    /**
     * Object转String
     *
     * @param object
     * @return String
     */
    public static String getString(Object object) {
        return object == null ? null : object.toString();
    }

    /**
     * 右边补全
     *
     * @param str
     * @param size
     * @param padStr
     * @return
     */
    public static String rightPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (StringUtils.isBlank(padStr)) {
            padStr = StringUtils.SPACE;
        }
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        StringBuilder strb = new StringBuilder(str);
        for (int i = 0; i < pads; i++) {
            strb.append(padStr);
        }
        return strb.toString();

    }

    /**
     * 转驼峰
     *
     * @param input
     * @param regex
     * @return
     */
    public static String hump(String input, String regex) {
        StringBuilder builder = new StringBuilder();
        String[] arrs = StringUtils.split(input, regex);
        for (int i = 0, j = arrs.length; i < j; i++) {
            if (i == 0) {
                builder.append(arrs[i]);
            } else {
                builder.append(StringUtils.capitalize(arrs[i]));
            }
        }
        return builder.toString();
    }

    public static String createToken(String uid) {
       //TODO
        return "12`3" + uid;
    }

    public static String mediaType(String fileName) {
        Optional<MediaType> optional = MediaTypeFactory.getMediaType(fileName);
        String contentType = optional.orElse(MediaType.TEXT_PLAIN).toString();
        return contentType;
    }

    public static String getItemProcKey(String itemNo, String procCode) {
        return itemNo + "|" + procCode;
    }


}
