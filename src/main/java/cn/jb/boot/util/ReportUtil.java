package cn.jb.boot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;

/**
 * @author xyb
 * @Description
 * @Date 2022/5/20 20:54
 */
public class ReportUtil {

    public static final String DEVICE_NO = "deviceNo";

    public static String toJson(Object data) {
        ObjectMapper om = new ObjectMapper();
        try {
            om.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static Map<String, Object> result(Object data){
//        Map<String, Object> result = new HashMap<>();
//        result.put("data", data);
//        return result;
//    }

    private static HttpSession session() {
        HttpSession session = attributes().getRequest().getSession();
        return session;
    }

    public static void setDeviceNo(String deviceNo) {
        if (StringUtils.isNotEmpty(deviceNo)) {
            session().setAttribute(DEVICE_NO, deviceNo);
        }
    }

    private static ServletRequestAttributes attributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }
}
