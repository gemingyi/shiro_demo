package com.example.shiro.core.shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 自定义sessionDAO
 * Created by Administrator on 2018/10/7.
 */
public class RedisSessionDAO extends AbstractSessionDAO {
    private long sessionLive;
    private String sessionKeyPrefix;
    private RedisTemplate redisTemplate;

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        redisTemplate.opsForValue().set(sessionKeyPrefix + sessionId, session, sessionLive, TimeUnit.MINUTES);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        return (Session) redisTemplate.opsForValue().get(sessionKeyPrefix + sessionId);
    }

    @Override
    public void update(Session session) {
        this.redisTemplate.opsForValue().set(sessionKeyPrefix + session.getId(), session, sessionLive, TimeUnit.MINUTES);
    }

    @Override
    public void delete(Session session) {
        if (session == null || session.getId() == null) {
            return;
        }
        this.redisTemplate.delete(sessionKeyPrefix + session.getId());
    }

    @Override
    public Collection<Session> getActiveSessions() {
        Set<Session> sessions = new HashSet<Session>();
        Set<Serializable> keys = redisTemplate.keys(sessionKeyPrefix + "*");
        if (keys != null && keys.size() > 0) {
            for (Serializable key : keys) {
                Session s = (Session) redisTemplate.opsForValue().get(key);
                sessions.add(s);
            }
        }
        return sessions;
    }


    public void setSessionLive(long sessionLive) {
        this.sessionLive = sessionLive;
    }

    public void setSessionKeyPrefix(String sessionKeyPrefix) {
        this.sessionKeyPrefix = sessionKeyPrefix;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
