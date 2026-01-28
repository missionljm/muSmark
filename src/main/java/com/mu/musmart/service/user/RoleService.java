package com.mu.musmart.service.user;

import com.mu.musmart.domain.entity.user.RoleDO;
import com.mu.musmart.domain.vo.ResVo;

import java.util.Map;

/**
 * 角色服务
 * @author lijinmu
 * @date 2026-01-22
 */
public interface RoleService {

    Map getMenuByRoleCode(String code);

    ResVo<RoleDO> queryRoleList();
}
