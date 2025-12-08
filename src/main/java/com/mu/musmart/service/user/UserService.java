package com.mu.musmart.service.user;

import com.mu.musmart.domain.dto.user.BaseUserInfoDTO;

public interface UserService {

    BaseUserInfoDTO getAndUpdateUserIpInfoBySessionId(String sessionId , String clientIp);
}
