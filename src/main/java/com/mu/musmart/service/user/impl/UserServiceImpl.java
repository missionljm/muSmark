package com.mu.musmart.service.user.impl;


import com.mu.musmart.domain.dto.user.BaseUserInfoDTO;
import com.mu.musmart.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public BaseUserInfoDTO getAndUpdateUserIpInfoBySessionId(String sessionId , String clientIp) {
        return null;
    }
}
