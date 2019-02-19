package com.example.shiro.service.impl;

import com.example.shiro.commons.Constant;
import com.example.shiro.dao.IUserDAO;
import com.example.shiro.model.Permission;
import com.example.shiro.model.Role;
import com.example.shiro.model.User;
import com.example.shiro.service.IUserService;
import com.example.shiro.service.abs.AbstractService;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.apache.shiro.session.mgt.SimpleSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/10/11.
 */
@Service("userService")
public class UserServiceImpl extends AbstractService implements IUserService {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    DefaultKaptcha producer;

    @Autowired
    private IUserDAO userDAO;

    @Override
    public User getUserByUserName(String userName) {
        return this.userDAO.findByName(userName);
    }

    @Override
    public Map<String, Object> getRolesAndPermissionsByUserName(String userName) {
        Role role;
        Permission permission;
        Set<String> roles = new HashSet<String>();
        Set<String> permissions = new HashSet<>();
        Map<String, Object> map = new HashMap<>();
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
    public boolean checkCodeToken(String sToken, String textStr) {
        Object value = redisTemplate.opsForValue().get(sToken);
        if (value != null && textStr.equals(value)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Map<String, Object> generateVerificationCode() throws Exception {
        Map<String, Object> map = new HashMap<>();
        // 生成文字验证码
        String text = producer.createText();
        // 生成图片验证码
        ByteArrayOutputStream outputStream = null;
        BufferedImage image = producer.createImage(text);
        outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);
        //
        map.put("img", Base64.getEncoder().encodeToString(outputStream.toByteArray()));
        //生成验证码对应的token  以token为key  验证码为value存在redis中
        String codeToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(codeToken, text, Constant.VERIFICATION__CODE_TIME, TimeUnit.MINUTES);
        map.put("cToken", codeToken);
        return map;
    }

    @Override
    public List<User> listOnLineUser() {
        Set setNames = redisTemplate.keys(Constant.SESSION_PREFIX + "*");
        List list = new ArrayList<User>(setNames.size());
        Iterator<String> iter = setNames.iterator();
        while (iter.hasNext()) {
            String temp = iter.next();
            SimpleSession session = (SimpleSession) redisTemplate.opsForValue().get(temp);
            User user = (User) session.getAttribute("currentUser");
            System.out.println(session.getAttribute("currentUser"));
            list.add(session.getAttribute("currentUser"));
        }
        return list;
    }

    @Override
    public boolean removeSession(String userName) {
        String key = Constant.SESSION_PREFIX + userName;
        return redisTemplate.delete(key);
    }


}
