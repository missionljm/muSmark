package com.mu.musmart.dao.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mu.musmart.dao.common.CommonDao;
import com.mu.musmart.domain.entity.user.RoleDO;
import com.mu.musmart.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleDao extends ServiceImpl<RoleMapper, RoleDO> {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private CommonDao commonDao;

    public RoleDO getRoleById(Long id){
        return roleMapper.selectById(id);
    }

    public RoleDO getRoleByRoleCode(String roleCode){
        return roleMapper.selectOne(new QueryWrapper<RoleDO>().eq("code",roleCode));
    };

    public List<String> getRoleByUserId(Long userId){
        return roleMapper.getRoleByUserId(userId);
    }

    public List<String> getRoleIdByUserId(Long userId){
        return roleMapper.getRoleIdByUserId(userId);
    }
}
