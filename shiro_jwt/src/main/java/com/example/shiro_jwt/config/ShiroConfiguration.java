package com.example.shiro_jwt.config;

import com.example.shiro_jwt.core.shiro.MyRealm;
import com.example.shiro_jwt.core.shiro.StatelessDefaultSubjectFactory;
import com.example.shiro_jwt.core.shiro.filter.JWTFilter;
import org.apache.commons.collections.map.HashedMap;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * Created by Administrator on 2018/9/28.
 */
@Configuration
public class ShiroConfiguration {

    @Bean(name = "myRealm")
    public MyRealm myRealm() {
        MyRealm myShiroRealm = new MyRealm();
//        myShiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        myShiroRealm.setCachingEnabled(true);
//        myShiroRealm.setCacheManager(redisCacheManager());
        return myShiroRealm;
    }

    @Bean(name = "subjectFactory")
    public StatelessDefaultSubjectFactory subjectFactory() {
        StatelessDefaultSubjectFactory statelessDefaultSubjectFactory = new StatelessDefaultSubjectFactory();
        return statelessDefaultSubjectFactory;
    }

    @Bean(name = "sessionManager")
    public DefaultSessionManager sessionManager() {
        DefaultSessionManager sessionManager = new DefaultSessionManager();
        sessionManager.setSessionValidationSchedulerEnabled(false);
        return sessionManager;
    }

    @Bean(name = "defaultSessionStorageEvaluator")
    public DefaultSessionStorageEvaluator defaultSessionStorageEvaluator () {
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        return defaultSessionStorageEvaluator;
    }

    @Bean(name = "subjectDAO")
    public DefaultSubjectDAO subjectDAO(@Qualifier("defaultSessionStorageEvaluator")DefaultSessionStorageEvaluator defaultSessionStorageEvaluator) {
        DefaultSubjectDAO defaultSubjectDAO = new DefaultSubjectDAO();
        defaultSubjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        return defaultSubjectDAO;
    }



    @Bean(name = "securityManager")
    public SecurityManager securityManager(@Qualifier("myRealm")MyRealm myRealm, @Qualifier("subjectDAO")DefaultSubjectDAO
            subjectDAO, @Qualifier("sessionManager")DefaultSessionManager sessionManager, @Qualifier("subjectFactory")StatelessDefaultSubjectFactory subjectFactory) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(myRealm);
        securityManager.setSubjectDAO(subjectDAO);
        securityManager.setSubjectFactory(subjectFactory);
        securityManager.setSessionManager(sessionManager);
        return securityManager;
    }

    @Bean(name = "jwtFilter")
    public JWTFilter jwtFilter() {
        return new JWTFilter();
    }

    @Bean
    public FilterRegistrationBean delegatingFilterProxy(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("shiroFilter");
        filterRegistrationBean.setFilter(proxy);
        return filterRegistrationBean;
    }

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(@Qualifier("securityManager")SecurityManager securityManager, @Qualifier("jwtFilter")JWTFilter jwtFilter) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //
        Map<String, Filter> filters = new HashedMap(2);
        filters.put("jwtFilter", jwtFilter);
        shiroFilterFactoryBean.setFilters(filters);
        //拦截链
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap();
        filterChainDefinitionMap.put("/**", "jwtFilter");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }


    /**
     * 加入下面2个 可以在controller层使用shiro注解
     * @return
     */
    @Bean(name = "advisorAutoProxyCreator")
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean(name = "authorizationAttributeSourceAdvisor")
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
