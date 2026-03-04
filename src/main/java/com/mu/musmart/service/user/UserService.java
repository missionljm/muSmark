package com.mu.musmart.service.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mu.musmart.context.ReqInfoContext;
import com.mu.musmart.domain.dto.user.BaseUserInfoDTO;
import com.mu.musmart.domain.entity.user.UserDO;

import java.util.List;
import java.util.Map;

public interface UserService {

    BaseUserInfoDTO getAndUpdateUserIpInfoBySessionId(String sessionId , String clientIp);

    List<Map> getUserPermissions();

    List<Map> queryUserPermissionList(List<Map> userPermissionList ,Long userId ,  Integer level);

    IPage<Map> getPageUserList(Map params);
}
