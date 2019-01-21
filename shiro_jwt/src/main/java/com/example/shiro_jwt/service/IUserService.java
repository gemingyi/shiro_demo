package com.example.shiro_jwt.service;


import com.example.shiro_jwt.model.User;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/11.
 */
public interface IUserService {
    User getUserByUserName(String userName);

    Map<String, Object> getRolesAndPermissionsByUserName(String userName);

    Map<String, Object> createRandomToken(String textStr);

    boolean checkRandomToken(String sToken, String textStr);

    void addTokenToRedis(String userName, String jwtTokenStr);

    boolean removeJWTToken(String userName);

    List<User> listOnLineUser();
}
