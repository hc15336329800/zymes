package cn.jb.boot.framework.com.cache;

import cn.jb.boot.framework.com.entity.TokenCacheObj;
import lombok.extern.slf4j.Slf4j;

/**
 * @author YX
 * @Description 储token的工具
 * @Date 2021/8/17 23:59
 */
@Slf4j
public class TokenCache {

    private static final BaseCacheMap<String, TokenCacheObj> CACHE = new BaseCacheMap<String, TokenCacheObj>(128);

    /**
     * openId
     *
     * @param key userId
     * @return openId
     */
    public static TokenCacheObj get(String key) {
        TokenCacheObj obj = CACHE.getCache(key);
        if (obj == null) {
            log.error("没有获取到token对应的数据-{}", key);
        }
        return obj;
    }

    /**
     * 放入到缓存
     *
     * @param key   userId
     * @param value openId
     */
    public static void put(String key, TokenCacheObj value) {
        log.info("设置token缓冲，{}，{}", key, value);
        CACHE.putCache(key, value);
    }

    public static void remove(String key) {
        CACHE.remove(key);

    }

}
