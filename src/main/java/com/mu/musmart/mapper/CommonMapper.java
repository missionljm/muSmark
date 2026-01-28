package com.mu.musmart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface CommonMapper extends BaseMapper<Object> {

    @Select("select T1.* from base_role_menu T0\n" +
            "left join base_menu T1 on T0.menu_id = T1.id\n" +
            "where T0.role_id = #{roleId} and T0.deleted = 0")
    Map<String , String> getMenuByRoleId(@Param("roleId") String roleId);
}
