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
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/9/28.
 */
@RestController
@RequestMapping
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
        boolean flag = userService.checkSessionToken(userInfo.getsToken(), userInfo.getTextStr());
        if(!flag) {
            result.put("code", CodeAndMsgEnum.ERROR.getcode());
            result.put("msg", "验证码错误！");
            return result;
        }
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
     * @return
     */
    @RequestMapping("/captcha")
    @ResponseBody
    public Map captcha() throws IOException {
        // 生成文字验证码
        String text = producer.createText();
        // 生成图片验证码
        ByteArrayOutputStream outputStream = null;
        BufferedImage image = producer.createImage(text);

        outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        // 生成captcha的token
        Map<String, Object> map = userService.createSessionToken(text);
        map.put("img", encoder.encode(outputStream.toByteArray()));
        return map;
    }

    /**
     * 未登录，shiro应重定向到登录界面，此处返回未登录状态信息由前端控制跳转页面
     *
     * @return
     */
    @RequestMapping(value = "/login")
    public Object login() {
        Map result = new HashMap<>();
        result.put("code",  CodeAndMsgEnum.UNAUTHENTIC.getcode());
        result.put("msg", CodeAndMsgEnum.UNAUTHENTIC.getMsg());
        return result;
    }

    /**
     * 未授权
     *
     * @return
     */
    @RequestMapping(value = "/unAuth")
    public Object unauth() {
        Map result = new HashMap<>();
        result.put("code",  CodeAndMsgEnum.UNAUTHORIZED.getcode());
        result.put("msg", CodeAndMsgEnum.UNAUTHORIZED.getMsg());
        return result;
    }

}
