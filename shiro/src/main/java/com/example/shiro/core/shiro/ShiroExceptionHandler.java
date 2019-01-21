package com.example.shiro.core.shiro;

import com.example.shiro.commons.CodeAndMsgEnum;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

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

//    /**
//     *
//     * @return
//     */
//    @ResponseBody
//    @ExceptionHandler(value = Exception.class)
//    public Map<String, Object> exception() {
//        return CodeAndMsgEnum.ERROR.result();
//    }

}
