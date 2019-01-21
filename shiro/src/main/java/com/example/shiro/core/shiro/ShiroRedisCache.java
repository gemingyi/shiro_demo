package com.example.shiro.core.shiro;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.SerializationUtils;

import java.util.*;

/**
 * 自定义redisCache
 * Created by Administrator on 2018/2/12.
 */
public class ShiroRedisCache<K, V>  implements Cache<K, V> {

    private long cacheLive ;
    private String cacheKeyPrefix ;
    private JedisConnectionFactory jedisConnectionFactory;

    private byte[] StringToByte(String string) {
        return string == null?null:string.getBytes();
    }
    //使用spring自带的序列工具类
    public byte[] objectToByteArray(Object obj) {
        return SerializationUtils.serialize(obj);
    }
    public Object byteArrayToObject(byte data[]) {
        return SerializationUtils.deserialize(data);
    }

    @Override
    public V get(K k) throws CacheException {
        V obj = null;
        RedisConnection connection = this.jedisConnectionFactory.getConnection();
        try {
            obj = (V) this.byteArrayToObject(connection.get(this.getRedisCacheKey(k)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection.close();
        return obj;
    }

    @Override
    public V put(K k, V v) throws CacheException {
        RedisConnection connection = this.jedisConnectionFactory.getConnection();
        try {
            this.putEx(k, v, this.cacheLive);
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection.close();
        return v;
    }

    public V putEx(K k, V v, Long expire) throws CacheException {
        RedisConnection connection = this.jedisConnectionFactory.getConnection();
        try {
            connection.setEx((this.getRedisCacheKey(k)), expire, this.objectToByteArray(v));
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection.close();
        return v;
    }

    @Override
    public V remove(K k) throws CacheException {
        V obj = null;
        RedisConnection connection = this.jedisConnectionFactory.getConnection();
        try {
            obj = (V) this.byteArrayToObject(connection.get((this.getRedisCacheKey(k))));
            connection.del((this.getRedisCacheKey(k)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection.close();
        return obj;
    }

    @Override
    public void clear() throws CacheException {
        RedisConnection connection = this.jedisConnectionFactory.getConnection();
        try {
            Set keys = connection.keys(this.StringToByte(this.cacheKeyPrefix + "*"));
            if (null != keys && keys.size() > 0) {
                Iterator itera = keys.iterator();
                byte[] key;
                while (itera.hasNext()) {
                    key = (byte[]) itera.next();
                    connection.del(key);
                }
            }
//            connection.flushDb();
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection.close();
    }

    @Override
    public int size() {
        RedisConnection connection = this.jedisConnectionFactory.getConnection();
        Long size = new Long(connection.dbSize().longValue());
        return size.intValue();
//        int size = 0;
//        RedisConnection connection = this.jedisConnectionFactory.getConnection();
//        try {
//            Set<byte[]> keys = connection.keys((this.keyPrefix + "*").getBytes());
//            size = keys.size();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        connection.close();
//        return size;
    }

    @Override
    public Set<K> keys() {
        Set<K> allKeys = new HashSet<K>();
        RedisConnection connection = this.jedisConnectionFactory.getConnection();
        try {
            Set<byte[]> keys = connection.keys(this.StringToByte(this.cacheKeyPrefix + "*"));
            for (byte[] key : keys) {
                allKeys.add((K) this.byteArrayToObject(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection.close();
        return allKeys;
    }

    @Override
    public Collection<V> values() {
        Set<V> allValues = new HashSet<V>();
        RedisConnection connection = this.jedisConnectionFactory.getConnection();
        try {
            Set<byte[]> keys = connection.keys(this.StringToByte(this.cacheKeyPrefix + "*"));
            for (byte[] key : keys) {
                allValues.add((V) this.byteArrayToObject(connection.get(key)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection.close();
        return allValues;
    }

    private byte[] getRedisCacheKey(K key) {
        Object redisKey = this.getStringRedisKey(key);
        if(redisKey instanceof String) {
            return this.StringToByte((this.cacheKeyPrefix + redisKey));
        } else {
            return this.StringToByte(String.valueOf(redisKey));
        }
    }

    private Object getStringRedisKey(K key) {
        Object redisKey;
        if(key instanceof PrincipalCollection) {
            redisKey = this.getRedisKeyFromPrincipalCollection((PrincipalCollection)key);
        } else {
            redisKey = key.toString();
        }

        return redisKey;
    }

    private Object getRedisKeyFromPrincipalCollection(PrincipalCollection key) {
        List realmNames = this.getRealmNames(key);
        Collections.sort(realmNames);
        Object redisKey = this.joinRealmNames(realmNames);
        return redisKey;
    }

    private List<String> getRealmNames(PrincipalCollection key) {
        ArrayList realmArr = new ArrayList();
        Set realmNames = key.getRealmNames();
        Iterator i$ = realmNames.iterator();

        while(i$.hasNext()) {
            String realmName = (String)i$.next();
            realmArr.add(realmName);
        }

        return realmArr;
    }

    private Object joinRealmNames(List<String> realmArr) {
        StringBuilder redisKeyBuilder = new StringBuilder();

        for(int i = 0; i < realmArr.size(); ++i) {
            String s = realmArr.get(i);
            redisKeyBuilder.append(s);
        }

        String redisKey = redisKeyBuilder.toString();
        return redisKey;
    }


    public ShiroRedisCache(JedisConnectionFactory jedisConnectionFactory) {
        this.jedisConnectionFactory = jedisConnectionFactory;
    }
    public ShiroRedisCache(JedisConnectionFactory jedisConnectionFactory, long cacheLive, String cachePrefix) {
        this(jedisConnectionFactory);
        this.cacheLive = cacheLive;
        this.cacheKeyPrefix = cachePrefix;
    }
}
