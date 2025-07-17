package cn.jb.boot.framework.com.cache;

import cn.jb.boot.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 弱引用map 缓存 实现4个小时自动清除 或者由GC回收
 *
 * @param <K>
 * @param <V>
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2019
 * @Date 2019年12月9日 下午3:35:20
 */
@Slf4j
public class BaseCacheMap<K, V> extends WeakHashMap<K, BaseCache<V>> {

    /**
     * 过期时间
     */
    private final long timeout;

    /**
     * 默认 90 天 过期
     */
    public BaseCacheMap(int initialCapacity) {
        super(initialCapacity);
        timeout = TimeUnit.DAYS.toSeconds(90);
    }

    /**
     * 加入到缓存
     *
     * @param key
     * @param value
     * @return
     * @Description
     * @Date 2019年12月9日 下午3:37:25
     */
    public void putCache(K key, V value) {
        BaseCache<V> cache = new BaseCache<>(value, DateUtil.nowDateTime());
        super.put(key, cache);
    }

    /**
     * 获取缓存
     *
     * @param key
     * @return
     * @Description
     * @Date 2019年12月9日 下午3:37:36
     */
    public V getCache(K key) {
        BaseCache<V> cache = super.get(key);
        if (cache != null) {
//			log.info("进入 getCache");
            long seconds = Duration.between(cache.getDateTime(), DateUtil.nowDateTime()).getSeconds();
            if (seconds > timeout) {
                remove(key);
            } else {
                return cache.getData();
            }
        }
        return null;
    }

}
