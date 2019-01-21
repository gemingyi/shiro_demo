package com.example.shiro.dao;

import java.util.Set;

/**
 * Created by Administrator on 2017/10/11.
 */
public interface IPermissionDAO {

    public Set<String> listPermission(String userName);
}
