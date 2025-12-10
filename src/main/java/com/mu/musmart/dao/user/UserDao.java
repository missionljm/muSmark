package com.mu.musmart.dao.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mu.musmart.domain.entity.user.UserDO;
import com.mu.musmart.mapper.UserMapper;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends ServiceImpl<UserMapper,UserDO> {


    @Autowired
    private UserMapper userMapper;

    public UserDO getUserById(Integer id){
        return userMapper.selectById(id);
    }

    public UserDO getUserByUserAccount(String userAccount){
        return userMapper.selectOne(new QueryWrapper<UserDO>().eq("user_name",userAccount));
    };

}
