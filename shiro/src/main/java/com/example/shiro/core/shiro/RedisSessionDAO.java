package com.example.shiro.core.shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.SerializationUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 自定义sessionDAO
 * Created by Administrator on 2018/10/7.
 */
public class RedisSessionDAO extends AbstractSessionDAO {

    private static final long DEFAULT_SESSION_LIVE  = 3600L;
    private static final String DEFAULT_SESSION_KEY_PREFIX  = "shiro_redis_session:";

    private long sessionLive = DEFAULT_SESSION_LIVE;
    private String sessionKeyPrefix = DEFAULT_SESSION_KEY_PREFIX;

    private JedisConnectionFactory jedisConnectionFactory;

    private byte[] StringToByte(String string) {
        return string == null?null:string.getBytes();
    }
    //使用spring自带的序列工具类
    private byte[] objectToByteArray(Object obj) {
        return SerializationUtils.serialize(obj);
    }
    private Object byteArrayToObject(byte data[]) {
        return SerializationUtils.deserialize(data);
    }


    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        this.saveSession(session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        RedisConnection connection = this.jedisConnectionFactory.getConnection();
        Session session = null;
        try {
            session = (Session) this.byteArrayToObject(connection.get(this.getRedisSessionKey(sessionId)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection.close();
        return session;
    }

    @Override
    public void update(Session session) {
        this.saveSession(session);
    }

    @Override
    public void delete(Session session) {
        if (session == null || session.getId() == null) {
            return;
        }
        RedisConnection connection = this.jedisConnectionFactory.getConnection();
        try{
            connection.del(this.getRedisSessionKey(session.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection.close();
    }

    @Override
    public Collection<Session> getActiveSessions() {
        Set<Session> allSessions = new HashSet<>();
        RedisConnection connection = this.jedisConnectionFactory.getConnection();
        try {
            Set<byte[]> keys = connection.keys(this.objectToByteArray(this.sessionKeyPrefix + "*"));
            if(keys != null && keys.size() > 0) {
                for (byte[] key : keys) {
                    allSessions.add((Session) this.byteArrayToObject(connection.get(key)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection.close();
        return allSessions;
    }


    private void saveSession(Session session) {
        RedisConnection connection = this.jedisConnectionFactory.getConnection();
        try {
            byte[] key = this.getRedisSessionKey(session.getId());
            byte[] value = this.objectToByteArray(session);
            connection.setEx(key, sessionLive, value);
        } catch (Exception e){
            e.printStackTrace();
        }
        connection.close();
    }

    private byte[] getRedisSessionKey(Serializable sessionId) {
        return this.StringToByte((this.sessionKeyPrefix + sessionId));
    }


    public void setSessionLive(long sessionLive) {
        this.sessionLive = sessionLive;
    }
    public void setSessionKeyPrefix(String sessionKeyPrefix) {
        this.sessionKeyPrefix = sessionKeyPrefix;
    }
    public void setJedisConnectionFactory(JedisConnectionFactory jedisConnectionFactory) {
        this.jedisConnectionFactory = jedisConnectionFactory;
    }
}
