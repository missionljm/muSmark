package com.mu.musmart.dao.user;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mu.musmart.domain.entity.user.UserInfoDO;
import com.mu.musmart.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserInfoDao extends ServiceImpl<UserInfoMapper, UserInfoDO> {

    @Autowired
    private UserInfoMapper userInfoMapper;

    public UserInfoDO getUserInfoById(Long id){
        return userInfoMapper.selectById(id);
    }
}
