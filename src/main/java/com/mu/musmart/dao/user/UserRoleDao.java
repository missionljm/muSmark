package com.mu.musmart.dao.user;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mu.musmart.domain.entity.user.UserRoleDO;
import com.mu.musmart.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRoleDao extends ServiceImpl<UserRoleMapper , UserRoleDO> {

    @Autowired
    private UserRoleMapper userRoleMapper;


}
