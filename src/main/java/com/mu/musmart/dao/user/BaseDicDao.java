package com.mu.musmart.dao.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mu.musmart.domain.entity.common.dic.BaseDicDO;
import com.mu.musmart.domain.entity.user.RoleDO;
import com.mu.musmart.mapper.BaseDicMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 字典表数据访问层
 */
@Repository
public class BaseDicDao extends ServiceImpl<BaseDicMapper, BaseDicDO> {

    @Autowired
    private BaseDicMapper baseDicMapper;

    /**
     * 根据id查询字典表数据
     * @param id
     * @return
     */
    public BaseDicDO getBaseDicById(Long id){
        return baseDicMapper.selectById(id);
    }

    /**
     * 查询字典表数据
     * @return
     */
    public List<BaseDicDO> getBaseDicList(QueryWrapper queryWrapper){
        return baseDicMapper.selectList(queryWrapper);
    }
}
