package com.example.shiro_jwt.service.impl;

import com.example.shiro_jwt.commons.CommonConstant;
import com.example.shiro_jwt.dao.IUserDAO;
import com.example.shiro_jwt.model.Permission;
import com.example.shiro_jwt.model.Role;
import com.example.shiro_jwt.model.User;
import com.example.shiro_jwt.service.IUserService;
import com.example.shiro_jwt.service.abs.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017/10/11.
 */
@Service("userService")
public class UserServiceImpl extends AbstractService implements IUserService {
    //
    @Value("${redis.identifyingTokenExpireTime}")
    private long identifyingTokenExpireTime;
    //redis中jwtToken过期时间
    @Value("${redis.refreshJwtTokenExpireTime}")
    private long refreshJwtTokenExpireTime;

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
    public Map<String, Object> createRandomToken(String textStr) {
        //生成一个token
        String sToken = UUID.randomUUID().toString();
        //生成验证码对应的token  以token为key  验证码为value存在redis中
        redisTemplate.opsForValue().set(CommonConstant.NO_REPEAT_PRE + sToken, textStr, identifyingTokenExpireTime, TimeUnit.MINUTES);
        Map<String, Object> map = new HashMap<>();
        map.put("cToken", sToken);
        return map;
    }

    @Override
    public boolean checkRandomToken(String sToken, String textStr) {
        Object value = redisTemplate.opsForValue().get(CommonConstant.NO_REPEAT_PRE  + sToken);
        if(value != null && textStr.equals(value)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void addTokenToRedis(String userName, String jwtTokenStr) {
        String key = CommonConstant.JWT_TOKEN + userName;
        redisTemplate.opsForValue().set(key, jwtTokenStr, refreshJwtTokenExpireTime, TimeUnit.MINUTES);
    }

    @Override
    public boolean removeJWTToken(String userName) {
        String key = CommonConstant.JWT_TOKEN + userName;
        return redisTemplate.delete(key);
    }

    @Override
    public List<User> listOnLineUser() {
        Set setNames = redisTemplate.keys(CommonConstant.JWT_TOKEN + "*");
        List list = new ArrayList<>(setNames.size());
        Iterator<String> iter = setNames.iterator();
        while (iter.hasNext()) {
            String temp = iter.next();
            list.add(temp.substring(temp.lastIndexOf("_") + 1));
        }
        return userDAO.listUserByNams(list.toArray());
    }
}
