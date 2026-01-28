package com.mu.musmart.service.user.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import com.mu.musmart.dao.common.CommonDao;
import com.mu.musmart.dao.user.RoleDao;
import com.mu.musmart.domain.entity.user.RoleDO;
import com.mu.musmart.domain.vo.ResVo;
import com.mu.musmart.enums.common.StatusEnum;
import com.mu.musmart.exception.ExceptionUtil;
import com.mu.musmart.service.user.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service("RoleService")
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private CommonDao commonDao;

    @Override
    public Map getMenuByRoleCode(String code) {
        Optional<RoleDO> role = Optional.ofNullable(roleDao.getRoleByRoleCode(code));
        if (!role.isPresent()) {
            throw ExceptionUtil.of(StatusEnum.UNEXPECT_ERROR , "角色不存在");
        }
        Map<String, String> menuMap = commonDao.getMenuByRoleId(role.get().getId().toString());
        if (MapUtil.isEmpty(menuMap)){
            throw ExceptionUtil.of(StatusEnum.UNEXPECT_ERROR , "角色未分配菜单权限");
        }
        return menuMap;
    }

    @Override
    public ResVo<RoleDO> queryRoleList() {
        return null;
    }
}
