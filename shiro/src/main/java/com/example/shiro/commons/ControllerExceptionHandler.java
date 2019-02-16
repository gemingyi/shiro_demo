package com.example.shiro.commons;

import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * controller统一异常处理
 * Created by Administrator on 2019/1/26.
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    /**
     * 未认证异常处理
     *
     * @return
     */
    @ResponseBody
    @ExceptionHandler(UnauthenticatedException.class)
    public Map<String, Object> authenticationException() {
        return CodeAndMsgEnum.UNAUTHENTIC.result();
    }

    /**
     * 未授权异常处理
     *
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = UnauthorizedException.class)
    public Map<String, Object> authorizationException() {
        return CodeAndMsgEnum.UNAUTHORIZED.result();
    }
}
