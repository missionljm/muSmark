package com.mu.musmart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommonMapper extends BaseMapper<Object> {

    @Select("select T1.* from base_role_menu T0\n" +
            "left join base_menu T1 on T0.menu_id = T1.id\n" +
            "where T0.role_id = #{roleId} and T0.deleted = 0")
    Map<String , String> getMenuByRoleId(@Param("roleId") String roleId);

    @Select("select T3.path , T3.component , T3.redirect , T3.name , T3.title , T3.icon , T3.id , T3.hidden  from user T0\n" +
            "left join base_role_user T1 on T0.id = T1.user_id\n" +
            "left join base_role_menu T2 on T1.role_id = T2.role_id\n" +
            "left join base_menu T3 on T2.menu_id = T3.id\n" +
            "where T0.id = #{userId} and T0.deleted = 0 and level = #{level}")
    List<Map<String , Object>> getUserPermission(@Param("userId") Long userId , @Param("level") Integer level);

    @Select("select\n" +
            "\tbm.component ,\n" +
            "\tbm.redirect ,\n" +
            "\tbm.name ,\n" +
            "\tbm.title ,\n" +
            "\tbm.icon ,\n" +
            "\tbm.id ,\n" +
            "\tbm.path\n" +
            "from\n" +
            "\tbase_menu bm\n" +
            "\tinner join base_role_menu brm on bm.id = brm.menu_id\n" +
            "\tinner join base_role_user bru on bru.role_id = brm.role_id\n" +
            "where\n" +
            "\tbm.parents_id =\n" +
            "\t#{parentId}\n" +
            "\tand bru.user_id = #{userId}")
    List<Map<String , Object>> getUserPermissionByParentsId(@Param("parentId") Long parentId , @Param("userId") Long userId);
}
