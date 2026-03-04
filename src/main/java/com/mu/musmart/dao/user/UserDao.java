package com.mu.musmart.dao.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mu.musmart.domain.entity.user.UserDO;
import com.mu.musmart.mapper.UserMapper;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class UserDao extends ServiceImpl<UserMapper,UserDO> {


    @Autowired
    private UserMapper userMapper;

    public UserDO getUserById(Long id){
        return userMapper.selectById(id);
    }

    public UserDO getUserByUserAccount(String userAccount){
        return userMapper.selectOne(new QueryWrapper<UserDO>().eq("user_name",userAccount));
    }

    public IPage<Map> getUserPageList(Page<UserDO> page , Map params){
        return userMapper.pageQueryUserList(page,params);
    }


}
