package com.mu.musmart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mu.musmart.domain.entity.user.UserDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<UserDO> {

    List<Map> queryUserList();

    @Select("<script>" +
            "SELECT " +
            "   u.id, " +
            "   u.third_account_id thirdAccountId, " +
            "   u.user_name userAccount, " +
            "   u.create_time createTime, " +
            "   u.deleted , " +
            "   u.update_time updateTime, " +
            "   ui.user_name as userName, " +
            "   ui.avatar, " +
            "   ui.photo, " +
            "   ui.position, " +
            "   ui.company, " +
            "   ui.profile, " +
            "   ui.email, " +
            "   ui.id_card idCard, " +
            "   ui.phone " +
            "FROM user u " +
            "LEFT JOIN user_info ui ON u.id = ui.user_id " +
            "WHERE u.deleted = 0 " +
            "<if test='params.userName != null and params.userName != \"\"'>" +
            "   AND ui.user_name LIKE CONCAT('%', #{params.userName}, '%') " +
            "</if>" +
            "<if test='params.email != null and params.email != \"\"'>" +
            "   AND ui.email LIKE CONCAT('%', #{params.email}, '%') " +
            "</if>" +
            "<if test='params.phone != null and params.phone != \"\"'>" +
            "   AND ui.phone = #{params.phone} " +
            "</if>" +
            "ORDER BY u.create_time DESC" +
            "</script>")
    IPage<Map>  pageQueryUserList(Page<UserDO>  page ,  @Param("params") Map params);

}
