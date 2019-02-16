package com.example.shiro.controller;

import com.example.shiro.commons.CodeAndMsgEnum;
import com.example.shiro.commons.ResponseEntity;
import com.example.shiro.model.User;
import com.example.shiro.service.IUserService;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/9/28.
 */
@RestController
public class ShiroController {

    @Autowired
    DefaultKaptcha producer;
    @Autowired
    private IUserService userService;

    /**
     * 登录
     *
     * @param userInfo
     * @return
     */
    @RequestMapping(value = "/userLogin", method = RequestMethod.POST)
    public Map ajaxLogin(User userInfo) {
        Map result = new HashMap<>();
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(userInfo.getUserName(), userInfo.getPassword());
        //验证码
//        String sToken = (String) params.get("sToken");
//        String textStr = (String) params.get("textStr");
//        boolean flag = userService.checkCodeToken(userInfo.getsToken(), userInfo.getTextStr());
//        if(!flag) {
//            result.put("code", CodeAndMsgEnum.ERROR.getcode());
//            result.put("msg", "验证码错误、或已失效！");
//            return result;
//        }
        try {
            subject.login(token);
            result.putAll(ResponseEntity.responseSuccess(subject.getSession().getId()));
        } catch (Exception e) {
            result.put("code", CodeAndMsgEnum.ERROR.getcode());
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 退出
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/logout")
    public Map logout() throws Exception {
        SecurityUtils.getSubject().logout();
        return ResponseEntity.responseSuccess(null);
    }

    /**
     * 生成验证码
     *
     * @return
     */
    @RequestMapping("/captcha")
    public Map captcha() throws IOException {
        Map result;
        try {
            result = userService.generateVerificationCode();
        } catch (Exception e) {
            result = ResponseEntity.responseError();
        }
        return result;
    }

    /**
     * 获取在线用户
     * @return
     * @throws IOException
     */
    @RequestMapping("/listOnLine")
    public Map listOnLine() throws IOException {
        Map result;
        try {
            List<User> vo = userService.listOnLineUser();
            result = ResponseEntity.responseSuccess(vo);
        } catch (Exception e) {
            e.printStackTrace();
            result = ResponseEntity.responseError();
        }
        return result;
    }

    /**
     * 踢出用户
     * @param userName
     * @return
     */
    @RequestMapping("/kickOutUser")
    @ResponseBody
    public Map kickOutUser(String userName) {
        Map result;
        try {
            boolean flag = userService.removeSession(userName);
            result = ResponseEntity.responseSuccess(flag);
        } catch (Exception e) {
            e.printStackTrace();
            result = ResponseEntity.responseError();
        }
        return result;
    }


    /**
     * 未登录，shiro应重定向到登录界面，此处返回未登录状态信息由前端控制跳转页面
     *
     * @return
     */
    @RequestMapping(value = "/unAuthen")
    public Map unAuthen() {
        Map result = new HashMap<>();
        result.put("code", CodeAndMsgEnum.UNAUTHENTIC.getcode());
        result.put("msg", CodeAndMsgEnum.UNAUTHENTIC.getMsg());
        return result;
    }

    /**
     * 未授权
     *
     * @return
     */
    @RequestMapping(value = "/unAuthor")
    public Map unAuthor() {
        Map result = new HashMap<>();
        result.put("code", CodeAndMsgEnum.UNAUTHORIZED.getcode());
        result.put("msg", CodeAndMsgEnum.UNAUTHORIZED.getMsg());
        return result;
    }

}
