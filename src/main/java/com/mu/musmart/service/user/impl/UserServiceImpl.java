package com.mu.musmart.service.user.impl;


import com.mu.musmart.dao.user.UserDao;
import com.mu.musmart.domain.dto.user.BaseUserInfoDTO;
import com.mu.musmart.domain.entity.user.UserDO;
import com.mu.musmart.service.help.UserSessionHelper;
import com.mu.musmart.service.user.UserService;
import com.mu.musmart.util.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户Service
 *
 * @author louzai
 * @date 2022-07-20
 */
@Service("UserService")
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserSessionHelper userSessionHelper;

    @Override
    public BaseUserInfoDTO getAndUpdateUserIpInfoBySessionId(String sessionId , String clientIp) {
        Long userId = userSessionHelper.getUserIdBySession(sessionId);
        UserDO userDo = userDao.getUserById(userId);
        BaseUserInfoDTO baseUserInfoDTO = new BaseUserInfoDTO();
        baseUserInfoDTO.setId(userDo.getId());
        baseUserInfoDTO.setUserName(userDo.getUserName());
        baseUserInfoDTO.setRegion(clientIp);
        return baseUserInfoDTO;
    }
}
