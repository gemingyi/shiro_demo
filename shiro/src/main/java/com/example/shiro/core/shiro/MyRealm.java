package com.example.shiro.core.shiro;

import com.example.shiro.model.User;
import com.example.shiro.service.IUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/10/11.
 */
public class MyRealm extends AuthorizingRealm {

    @Autowired
    private IUserService userService;

    /**
     * 授权
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String userName = (String) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo auth = new SimpleAuthorizationInfo();
        Map<String, Object> map = null;
        try {
            map = this.userService.getRolesAndPermissionsByUserName(userName);
            auth.setRoles((Set<String>) map.get("allRoles"));
            auth.setStringPermissions((Set<String>) map.get("allPermissions"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return auth;
    }

    /**
     * 认证
     *
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String userName = (String) token.getPrincipal();
        User vo = this.userService.getUserByUserName(userName);
        if (vo == null) {
            throw new UnknownAccountException("该用户名称不存在！");
        } else if (vo.getLock() == null || vo.getLock().equals(1)) {
            throw new UnknownAccountException("该用户已经被锁定了！");
        } else {
            String password = new String((char[]) token.getCredentials());
            if (vo.getPassword().equals(password)) {
                AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(vo.getUserName(), vo.getPassword(), vo.getUserName());
                SecurityUtils.getSubject().getSession().setAttribute("currentUser", vo);
                return authcInfo;
            } else {
                throw new IncorrectCredentialsException("密码错误！");
            }
        }
    }

}
