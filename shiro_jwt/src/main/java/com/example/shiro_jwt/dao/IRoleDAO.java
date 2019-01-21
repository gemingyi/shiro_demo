package com.example.shiro_jwt.dao;

import java.util.Set;

/**
 * Created by Administrator on 2017/10/11.
 */
public interface IRoleDAO {

    public Set<String> listRole(String userName);
}
