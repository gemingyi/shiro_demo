package com.example.shiro.service.impl;

import com.example.shiro.dao.IUserDAO;
import com.example.shiro.model.Permission;
import com.example.shiro.model.Role;
import com.example.shiro.model.User;
import com.example.shiro.service.IUserService;
import com.example.shiro.service.abs.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/10/11.
 */
@Service("userService")
public class UserServiceImpl extends AbstractService implements IUserService {

    @Value("${shiro.redis.sessionLive}")
    private long sessionLive;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private IUserDAO userDAO;

    @Override
    public User getUserByUserName(String userName) {
        return this.userDAO.findByName(userName);
    }

    @Override
    public Map<String, Object> getRolesAndPermissionsByUserName(String userName) {
        Role role = null;
        Permission permission = null;
        Set<String> roles = new HashSet<String>();
        Set<String > permissions = new HashSet<String>();
        Map<String, Object> map = new HashMap<String, Object>();
        User vo = this.userDAO.listRolesAndPermissions(userName);
        for (int i = 0; i < vo.getRoles().size(); i++) {
            role = vo.getRoles().get(i);
            roles.add(role.getRoleName());
            for (int j = 0; j < role.getPermissions().size(); j++) {
                permission = role.getPermissions().get(i);
                permissions.add(permission.getPermissionName());
            }
        }
        map.put("allRoles", roles);
        map.put("allPermissions", permissions);
        return map;
    }

    @Override
    public Map<String, Object> createSessionToken(String textStr) {
        //生成一个token
        String sToken = UUID.randomUUID().toString();
        //生成验证码对应的token  以token为key  验证码为value存在redis中
        redisTemplate.opsForValue().set(sToken, textStr, sessionLive, TimeUnit.MINUTES);
        Map<String, Object> map = new HashMap<>();
        map.put("cToken", sToken);
        return map;
    }

    @Override
    public boolean checkSessionToken(String sToken, String textStr) {
        Object value = redisTemplate.opsForValue().get(sToken);
        if(value != null) {
            if(textStr.equals(value)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
