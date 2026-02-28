package com.mu.musmart.service.user.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import com.mu.musmart.dao.common.CommonDao;
import com.mu.musmart.dao.user.RoleDao;
import com.mu.musmart.dao.user.UserRoleDao;
import com.mu.musmart.domain.entity.user.RoleDO;
import com.mu.musmart.domain.entity.user.UserRoleDO;
import com.mu.musmart.domain.vo.ResVo;
import com.mu.musmart.enums.common.StatusEnum;
import com.mu.musmart.exception.ExceptionUtil;
import com.mu.musmart.service.user.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("RoleService")
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private CommonDao commonDao;

    @Autowired
    private UserRoleDao userRoleDao;

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
    public List<String> queryRoleListByUserId(Long userId) {
        List<String> roleByUserId = roleDao.getRoleByUserId(userId);
        return roleByUserId;
    }

    /**
     * @author lijinmu
     * @date 2026/2/28
     * 授权
     * @param params
     */
    @Override
    public void toGrantAuthorization(Map params) {
        List<String> userList = (List<String>) params.get("userMap");
        userList.forEach(u ->{
            List<String> roleList = (List<String>) params.get("roleMap");
            List<String> roleIdByUserId = roleDao.getRoleIdByUserId(Long.valueOf(u));
            List<String> roleListSec = roleList.stream().filter(e -> roleIdByUserId.contains(e)).collect(Collectors.toList());
            List<UserRoleDO> userRoleDOList = new ArrayList<>();
            roleListSec.stream().forEach(e ->{
                UserRoleDO userRoleDO = new UserRoleDO();
                userRoleDO.setUserId(Long.valueOf(u));
                userRoleDO.setRoleId(Long.valueOf(e));
                userRoleDOList.add(userRoleDO);
            });
            userRoleDao.saveBatch(userRoleDOList);
        });
    }
}
