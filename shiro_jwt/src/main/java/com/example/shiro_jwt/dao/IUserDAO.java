package com.example.shiro_jwt.dao;


import com.example.shiro_jwt.model.User;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/11.
 */
public interface IUserDAO {
    public User findByName(String userName);

    public List<User> getAllUser(Map<String, Object> param);

    public Integer getUserTotal(Map<String, Object> param);

    public User listRolesAndPermissions(String userName);

    public boolean doCreate(User vo);

    public boolean doUpdate(User vo);

    public boolean doRemove(Object[] ids);

    public List<User> listUserByNams(Object[] names);


}
