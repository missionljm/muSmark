package com.mu.musmart.service.dic.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mu.musmart.cache.RedisClient;
import com.mu.musmart.dao.user.BaseDicDao;
import com.mu.musmart.domain.entity.common.dic.BaseDicDO;
import com.mu.musmart.service.dic.DicDealerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service("DicDealerService")
public class DicDealerServiceImpl implements DicDealerService {

    @Autowired
    private BaseDicDao baseDicDao;

    /**
     * 获取字典数据
     * @return
     */
    @Override
    public Map<String , BaseDicDO> getDicMap() {
        QueryWrapper<BaseDicDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted" , 0);
        Map dicMap = new HashMap<>();
        // 判断redis里面是否已经存在字典数据，存在则直接获取字典数据返回
        Map dict = RedisClient.hGetAll("dict" , List.class);
        if (CollectionUtil.isEmpty(dict)){
            List<BaseDicDO> baseDicList = baseDicDao.getBaseDicList(queryWrapper);
            dicMap = baseDicList.stream().collect(Collectors.groupingBy(BaseDicDO::getType));
            RedisClient.hMSet("dict" , dicMap);
            RedisClient.expire("dict" , 6000L);
        }else {
            dicMap = dict;
        }
        Map result = MapUtil.of("dict" , dicMap);
        return result;
    }
}
