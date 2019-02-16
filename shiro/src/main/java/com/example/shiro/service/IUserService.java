package com.example.shiro.service;


import com.example.shiro.model.User;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/11.
 */
public interface IUserService {
    /**
     *
     * @param userName
     * @return
     */
    User getUserByUserName(String userName);

    /**
     *
     * @param userName
     * @return
     */
    Map<String, Object> getRolesAndPermissionsByUserName(String userName);

    /**
     *
     * @param sToken
     * @param textStr
     * @return
     */
    boolean checkCodeToken(String sToken, String textStr);

    /**
     *
     * @return
     * @throws Exception
     */
    Map<String, Object> generateVerificationCode() throws Exception;

    /**
     *
     * @return
     */
    List<User> listOnLineUser();

    /**
     *
     * @return
     */
    boolean removeSession(String userName);
}
