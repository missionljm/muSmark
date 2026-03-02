package com.mu.musmart.service.dic;

import com.mu.musmart.domain.entity.common.dic.BaseDicDO;

import java.util.List;
import java.util.Map;

/**
 * 字典处理业务
 */
public interface DicDealerService {

    /**
     * 获取字典数据
     * @return
     */
    Map<String , BaseDicDO> getDicMap();
}
