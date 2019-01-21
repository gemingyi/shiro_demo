package com.example.shiro.commons;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by Administrator on 2018/11/13.
 */
public class ResponseEntity {
    /**
     * 统一接口调用成功 返回数据
     * @param data
     * @return
     */
    public static Map<String, Object> responseSuccess(Object data) {
        Map<String, Object> resultMap = new HashMap<>();
        if(null == data) {
            resultMap.putAll(CodeAndMsgEnum.INFO.result());
            resultMap.put("datas", new Object[0]);
        } else {
            resultMap.putAll(CodeAndMsgEnum.SUCCESS.result());
            resultMap.put("datas", data);
        }
        return resultMap;
    }

    /**
     * 统一接口调用失败 返回数据
     * @return
     */
    public static Map<String, Object> responseError() {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.putAll(CodeAndMsgEnum.ERROR.result());
        return resultMap;
    }
}
