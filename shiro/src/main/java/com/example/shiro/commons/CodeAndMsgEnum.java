package com.example.shiro.commons;

import java.util.HashMap;
import java.util.Map;

/**
 * restful返回code、msg类
 * Created by Administrator on 2018/11/13.
 */
public enum CodeAndMsgEnum {
    SUCCESS(100200, "请求受理成功！"),
    INFO(100204, "请求受理成功，响应数据为空！"),
    UNAUTHENTIC(100401, "未认证，请先登录！"),
    TOKENERROR(100402, "未认证，Token错误！"),
    UNAUTHORIZED(100403, "未授权，权限不足！"),
    NOTFOUND(100404, "服务器未找到资源"),
    ERROR(100500, "服务器发生错误！");


    private int code;
    private String msg;

    CodeAndMsgEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Map<String, Object> result() {
        Map result = new HashMap<String, Object>(3);
        result.put("code", this.code);
        result.put("msg", this.msg);
        return result;
    }

    public int getcode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
