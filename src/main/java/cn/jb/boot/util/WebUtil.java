package cn.jb.boot.util;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author xyb
 * @Description
 * @Date 2021/9/12 12:40
 */
@Slf4j
public class WebUtil {

    public static HttpServletRequest request() {
        return attributes().getRequest();
    }

    public static HttpServletResponse response() {
        return attributes().getResponse();
    }

    public static <T> T getAttribute(String name) {
        return (T) request().getAttribute(name);
    }

    public static void setAttribute(String name, Object o) {
        request().setAttribute(name, o);
    }

    private static ServletRequestAttributes attributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    public static String getUserAgent(HttpServletRequest request) {
        return null;
    }

    /**
     * 获取ip地址
     *
     * @return IP
     */
    public static String getIpAddr() {
        return getIpAddr(request());
    }

    /**
     * 获取ip地址
     *
     * @param request
     * @return IP
     * @Description
     * @Date 2020年1月14日 上午9:08:51
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = null;
        if (request == null) {
            return ip;
        }
        String[] headers = {"x-forwarded-for", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_Client_IP", "HTTP_X_FORWARDED_FOR"};
        String unknown = "unknown";
        int max = headers.length + 1;
        for (int i = 0; i < max; i++) {
            if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
                if (i == headers.length) {
                    ip = request.getRemoteAddr();
                } else {
                    ip = request.getHeader(headers[i]);
                }
            } else {
                break;
            }
        }
        // 使用代理，则获取第一个IP地址
        int maxLen = 15;
        if (StringUtils.isNotEmpty(ip) && ip.length() > maxLen) {
            ip = StringUtils.substringBefore(ip, StringUtil.COMMA);
        }
        String[] ips = {"127.0.0.1", "0:0:0:0:0:0:0:1"};
        if (StringUtils.equalsAny(ip, ips)) {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                log.error("获取ip地址失败:{}", e.getMessage());
            }
        }
        return ip;
    }

    public static void main(String[] args) {
        System.out.println(IdUtil.fastSimpleUUID());
    }
}
