package com.example.shiro.service;


import com.example.shiro.model.User;

import java.util.Map;

/**
 * Created by Administrator on 2017/10/11.
 */
public interface IUserService {
    User getUserByUserName(String userName);

    Map<String, Object> getRolesAndPermissionsByUserName(String userName);

    Map<String, Object> createSessionToken(String textStr);

    boolean checkSessionToken(String sToken, String textStr);

}
