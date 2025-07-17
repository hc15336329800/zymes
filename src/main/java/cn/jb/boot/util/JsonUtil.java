package cn.jb.boot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author YX
 * @Description json 工具类
 * @Date 2021/8/18 0:07
 */
public class JsonUtil {

    private final static ObjectMapper OM = new ObjectMapper();

    private final static Logger LOG = LoggerFactory.getLogger(JsonUtil.class);

    /**
     * 美化json
     *
     * @param obj
     * @return
     * @throws Exception
     */
    public static String printerJson(Object obj) {
        try {
            return OM.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOG.error("对象转json错误，{}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 转换为 JSON 字符串
     *
     * @param obj
     * @return
     * @throws Exception
     */
    public static String toJson(Object obj) {
        try {
            return OM.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOG.error("对象转json错误，{}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 转换为 JavaBean
     *
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T toPojo(String json, Class<T> clazz) {
        try {
            OM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return OM.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            LOG.error("json转bean错误，{}", e.getMessage());
        }
        return null;
    }

    /**
     * 转换为 JavaBean 集合
     *
     * @param json
     * @param clazz
     * @return
     */
    public static <T> List<T> toPojos(String json, Class<T> clazz) {
        try {
            JavaType javaType = getCollectionType(ArrayList.class, clazz);
            return OM.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            LOG.error("json转bean错误，{}", e.getMessage());
        }
        return null;
    }

    /**
     * 获取泛型的 Collection Type
     *
     * @param collectionClass 泛型的Collection
     * @param elementClasses  元素类
     * @return JavaType Java类型
     * @since 1.0
     */
    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return OM.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    /**
     * 获取json节点的值
     *
     * @param json
     * @param key
     * @return
     */
    public static String key2Val(String json, String key) {
        try {
            JsonNode node = OM.readTree(json);
            if (node != null) {
                return node.findValue(key).asText();
            }
        } catch (JsonProcessingException e) {
            LOG.error("获取json节点的值错误，{}", e.getMessage());
        }
        return null;
    }

    /***
     *  对象转 JsonNode
     * @author: xyb
     * @date: 2020-08-24 21:42:55
     * @param t
     * @return: com.fasterxml.jackson.databind.JsonNode
     */
    public static <T extends Serializable> JsonNode toNode(T t) {
        return OM.valueToTree(t);
    }

}
