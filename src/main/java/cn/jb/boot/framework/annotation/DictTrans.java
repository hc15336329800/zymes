package cn.jb.boot.framework.annotation;


import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.framework.serializer.DictFieldSerializer;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据字典转义
 *
 * @Author: xyb
 * @Description:
 * @Date: 2022-11-14 下午 09:27
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = DictFieldSerializer.class)
public @interface DictTrans {

    /**
     * 根据数据库的数据转义
     **/
    DictType type() default DictType.ROOT_TYPE;

    /**
     * 自定义的数值键值对 如 {"01:男", "02:女"}
     */
    String[] pairs() default {};

    String name() default StringUtils.EMPTY;
}
