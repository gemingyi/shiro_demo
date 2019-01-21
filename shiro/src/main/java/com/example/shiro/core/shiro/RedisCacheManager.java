package com.example.shiro.core.shiro;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 自定义cache管理
 * Created by Administrator on
 */
public class RedisCacheManager implements CacheManager {
    private static final long DEFAULT_CACHE_LIVE = 3600L;
    private static final String DEFAULT_CACHE_KEY_PREFIX = "shiro_redis_cache:";

    private long cacheLive = DEFAULT_CACHE_LIVE;
    private String cacheKeyPrefix = DEFAULT_CACHE_KEY_PREFIX;

    private JedisConnectionFactory jedisConnectionFactory;


    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<String, Cache>();

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        Cache cache = this.caches.get(name);
        if (cache == null) {
            cache = new ShiroRedisCache<K, V>(jedisConnectionFactory, cacheLive, cacheKeyPrefix);
            this.caches.put(name, cache);
        }
        return cache;
    }


    public void setCacheLive(long cacheLive) {
        this.cacheLive = cacheLive;
    }
    public void setCacheKeyPrefix(String cacheKeyPrefix) {
        this.cacheKeyPrefix = cacheKeyPrefix;
    }
    public void setJedisConnectionFactory(JedisConnectionFactory jedisConnectionFactory) {
        this.jedisConnectionFactory = jedisConnectionFactory;
    }
}
