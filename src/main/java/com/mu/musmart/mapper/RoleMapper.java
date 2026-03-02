package com.mu.musmart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mu.musmart.domain.entity.user.RoleDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface RoleMapper extends BaseMapper<RoleDO> {

    @Select("select T2.code from user T0\n" +
            "left join base_role_user T1 on T0.id = T1.user_id\n" +
            "left join base_role T2 on T1.role_id = T2.id\n" +
            "where T0.id = #{userId} and T0.deleted = 0")
    List<String> getRoleByUserId(@Param("userId") Long userId);

    @Select("select T2.id from user T0\n" +
            "left join base_role_user T1 on T0.id = T1.user_id\n" +
            "left join base_role T2 on T1.role_id = T2.id\n" +
            "where T0.id = #{userId} and T0.deleted = 0")
    List<String> getRoleIdByUserId(@Param("userId") Long userId);

}
