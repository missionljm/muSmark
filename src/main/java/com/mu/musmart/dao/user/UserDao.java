package com.mu.musmart.dao.user;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mu.musmart.domain.entity.user.UserDO;
import com.mu.musmart.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends ServiceImpl<UserMapper,UserDO> {


    @Autowired
    private UserMapper userMapper;

    public UserDO getUserById(Integer id){
        return userMapper.selectById(id);
    }
}
