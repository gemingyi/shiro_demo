package com.example.shiro_jwt.core.shiro;

import com.example.shiro_jwt.commons.CodeAndMsgEnum;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Map;

/**
 * controller层 shiro异常处理类
 * Created by Administrator on 2018/10/13.
 */
@ControllerAdvice
public class ShiroExceptionHandler {

    /**
     * 未认证异常处理
     * @return
     */
    @ResponseBody
    @ExceptionHandler(UnauthenticatedException.class)
    public Map<String, Object> authenticationException() {
        return CodeAndMsgEnum.UNAUTHENTIC.result();
    }

    /**
     * 未授权异常处理
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = UnauthorizedException.class)
    public Map<String, Object> authorizationException() {
        return CodeAndMsgEnum.UNAUTHORIZED.result();
    }

    /**
     * 捕捉404异常
     * @return
     */
    @ResponseBody
    @ExceptionHandler(NoHandlerFoundException.class)
    public Map<String, Object> handle404(NoHandlerFoundException e) {
        return CodeAndMsgEnum.NOTFOUND.result();
    }


}
