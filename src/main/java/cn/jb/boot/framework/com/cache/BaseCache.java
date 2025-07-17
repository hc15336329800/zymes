package cn.jb.boot.framework.com.cache;

import java.time.LocalDateTime;

/**
 * 用户id缓存对象
 *
 * @param <V>
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2019
 * @Date 2019年12月9日 下午3:37:45
 */
public class BaseCache<V> {

    /**
     * 时间
     */
    private final LocalDateTime dateTime;
    /**
     * 数据
     **/
    private final V data;

    /**
     * 构造器
     */
    public BaseCache(V data, LocalDateTime dateTime) {
        this.data = data;
        this.dateTime = dateTime;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public V getData() {
        return data;
    }

}
