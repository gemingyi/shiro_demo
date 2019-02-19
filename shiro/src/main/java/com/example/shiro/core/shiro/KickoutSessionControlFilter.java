package com.example.shiro.core.shiro;

import com.example.shiro.commons.CodeAndMsgEnum;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Administrator on 2019/2/16.
 */
public class KickoutSessionControlFilter extends AccessControlFilter {
    private final String kickOutKey = "kickout";
    private String kickoutPrefix;
    private RedisTemplate redisTemplate;
    private SessionManager sessionManager;

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        Subject subject = getSubject(servletRequest, servletResponse);
        //如果没有登录，不进行多出登录判断
        if (!subject.isAuthenticated() && !subject.isRemembered()) {
            return true;
        }
        Session session = subject.getSession();
        String username = (String) subject.getPrincipal();
        Serializable sessionId = session.getId();
        //获取redis中数据
        ArrayList<Serializable> deque = (ArrayList<Serializable>) redisTemplate.opsForList().range(kickoutPrefix + username, 0, -1);
        if (deque == null || deque.size() == 0) {
            deque = new ArrayList<>();
        }
        //如果队列里没有此sessionId，且用户没有被踢出,当前session放入队列
        if (!deque.contains(sessionId) && session.getAttribute(kickOutKey) == null) {
            deque.add(sessionId);
            redisTemplate.opsForList().leftPush(kickoutPrefix + username, sessionId);
        }
        //如果队列里的sessionId数大于1，开始踢人
        while (deque.size() > 1) {
            //获取第一个sessionId（arrayList方法有限转成LinkedList）
            Serializable kickoutSessionId = (Serializable) new LinkedList(deque).removeFirst();
            deque.remove(kickoutSessionId);
            redisTemplate.opsForList().remove(kickoutPrefix + username, 1, kickoutSessionId);
            try {
                Session kickoutSession = sessionManager.getSession(new DefaultSessionKey(kickoutSessionId));
                //设置会话的kickout属性表示踢出了
                if (kickoutSession != null) {
                    kickoutSession.setAttribute(kickOutKey, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //session包含kickout属性，T出
        if (session.getAttribute(kickOutKey) != null) {
            try {
                subject.logout();
            } catch (Exception e) {
                e.printStackTrace();
            }
            saveRequest(servletRequest);
            //返回401
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.setStatus(HttpStatus.OK.value());
            httpResponse.setContentType("application/json;charset=utf-8");
            httpResponse.getWriter().write("{\"code\":" + CodeAndMsgEnum.UNAUTHENTIC.getcode() + ", \"msg\":\"" + "当前帐号在其他地方登录，您已被强制下载！" + "\"}");
            return false;
        }
        return true;
    }

    public void setKickoutPrefix(String kickoutPrefix) {
        this.kickoutPrefix = kickoutPrefix;
    }
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
