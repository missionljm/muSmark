package com.mu.musmart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mu.musmart.domain.entity.user.UserInfoDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfoDO> {
}
