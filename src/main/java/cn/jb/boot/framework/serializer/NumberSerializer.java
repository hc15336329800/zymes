package cn.jb.boot.framework.serializer;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * 标准数据转码
 *
 * @author xyb
 * @Date 2022年6月29日 下午8:09:14
 */
public class NumberSerializer extends JsonSerializer<BigDecimal> implements ContextualSerializer {


    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (Objects.nonNull(value)) {
            String s = value.stripTrailingZeros().toPlainString();
            gen.writeObject(new BigDecimal(s));
        } else {
            gen.writeObject(BigDecimal.ZERO);

        }

    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        return this;
    }


}
