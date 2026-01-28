package com.mu.musmart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mu.musmart.domain.entity.user.RoleDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<RoleDO> {

}
