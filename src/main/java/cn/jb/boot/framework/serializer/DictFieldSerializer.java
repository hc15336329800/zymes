package cn.jb.boot.framework.serializer;

import cn.jb.boot.framework.annotation.DictTrans;
import cn.jb.boot.util.DictUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;


/**
 * 标准数据转码
 *
 * @Author: xyb
 * @Description:
 * @Date: 2022-11-14 下午 09:43
 **/
public class DictFieldSerializer extends JsonSerializer<String> implements ContextualSerializer {

    /**
     * 标准数据字段名称
     */
    private String fieldName;

    /**
     * 标准数据字段类型
     */
    private DictTrans dict;

    /**
     * 序列化 标准数据
     *
     * @param value       当前值
     * @param gen         JsonGenerator
     * @param serializers 序列化对象
     * @throws IOException 异常
     */

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // 写原值
        gen.writeObject(value);
        // 以追加_desc后缀写翻译值
        gen.writeStringField(fieldName, DictUtil.dictTrans(value, dict));
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        if (property != null && String.class.isAssignableFrom(property.getType().getRawClass())) {
            DictTrans dict = property.getAnnotation(DictTrans.class);
            if (dict != null) {
                String name = dict.name();
                if (StringUtils.isNotEmpty(name)) {
                    this.fieldName = name;
                } else {
                    this.fieldName = property.getName() + "Desc";
                }
                this.dict = dict;
                return this;
            }
        }
        return null;
    }
}
