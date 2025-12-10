package com.mu.musmart.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.mu.musmart.dao.user.UserDao;
import com.mu.musmart.domain.entity.user.UserDO;
import com.mu.musmart.enums.common.StatusEnum;
import com.mu.musmart.exception.ExceptionUtil;
import com.mu.musmart.exception.ForumException;
import com.mu.musmart.service.LoginService;
import com.mu.musmart.service.help.UserSessionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service("LoginService")
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserDao userDao;
    
    @Autowired
    private UserSessionHelper userSessionHelper;


    @Override
    public void logout(String session) {

    }

//    @Override
//    public String loginByWx(Long userId) {
//        return "";
//    }
    @Override
    public String loginByUserPwd(Map loginPar) {
        // 获取账号密码
        Optional<String> username = Optional.ofNullable(loginPar.get("username")).map(Object::toString);
        Optional<String> password = Optional.ofNullable(loginPar.get("password")).map(Object::toString);
        UserDO userDo = userDao.getUserByUserAccount(username.get());
        // 判断用户是否存在
        if (ObjectUtil.isNotEmpty(userDo)){
            if (!userDo.getPassword().equals(password.get())){
                throw ExceptionUtil.of(StatusEnum.USER_PWD_ERROR);
            }
            // 生成系统token
            String token = userSessionHelper.getSession(userDo.getId());
            return token;
        }else {
            throw ExceptionUtil.of(StatusEnum.USER_NOT_EXISTS);
        }
    }
}
