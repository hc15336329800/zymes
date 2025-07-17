package cn.jb.boot.util;


import cn.jb.boot.framework.annotation.DictTrans;
import cn.jb.boot.framework.com.Constants;
import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.system.vo.DictDataVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 字典工具类
 *
 * @Author: xyb
 * @Description:
 * @Date: 2022-11-14 下午 10:03
 **/
public class DictUtil {
    private static ConcurrentHashMap<String, List<DictDataVo>> CACHE = new ConcurrentHashMap<>();


    private DictUtil() {
    }

    /**
     * 设置字典缓存
     *
     * @param data 参数键 字典数据列表
     */
    public static void setDictCache(Map<String, List<DictDataVo>> data) {
        CACHE.putAll(data);
    }

    /**
     * 获取字典缓存
     *
     * @param key 参数键
     * @return 字典数据列表
     */
    public static List<DictDataVo> getDictCache(DictType key) {
        return CACHE.get(key.getCode());
    }

    public static DictDataVo getDictData(DictType key, String value) {
        List<DictDataVo> list = CACHE.get(key.getCode());
        if (CollectionUtils.isNotEmpty(list) && StringUtils.isNotBlank(value)) {
            Optional<DictDataVo> any = list.stream().filter(d -> d.getDictValue().equals(value)).findAny();
            if (any.isPresent()) {
                return any.get();
            }
        }
        return null;
    }

    public static String getDictName(DictType key, String value) {
        List<DictDataVo> list = CACHE.get(key.getCode());
        if (CollectionUtils.isNotEmpty(list) && StringUtils.isNotBlank(value)) {
            Optional<String> any = list.stream().filter(d -> d.getDictValue().equals(value)).map(DictDataVo::getName).findAny();
            if (any.isPresent()) {
                return any.get();
            }
        }
        return StringUtils.EMPTY;
    }

    public static String getDictLabel(DictType key, String value) {
        List<DictDataVo> list = CACHE.get(key.getCode());

        return getValue(value, list);
    }

    public static String dictTrans(String value, DictTrans dict) {
        List<DictDataVo> list = getDictCache(dict.type());
        return getValue(value, list);
    }

    private static String getValue(String value, List<DictDataVo> list) {
        if (CollectionUtils.isNotEmpty(list) && StringUtils.isNotBlank(value)) {
            Optional<DictDataVo> any = list.stream().filter(d -> d.getDictValue().equals(value)).findAny();

            if (any.isPresent()) {
                return any.get().getDictLabel();
            }
        }
        return StringUtils.EMPTY;
    }
}
