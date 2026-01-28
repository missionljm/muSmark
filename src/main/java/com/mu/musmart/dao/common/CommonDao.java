package com.mu.musmart.dao.common;

import com.mu.musmart.mapper.CommonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class CommonDao{

    @Autowired
    private CommonMapper commonMapper;
    public Map<String, String> getMenuByRoleId(String code){
        return commonMapper.getMenuByRoleId(code);
    }
}
