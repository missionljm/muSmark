package com.mu.musmart.controller.common;

import com.mu.musmart.domain.entity.common.dic.BaseDicDO;
import com.mu.musmart.domain.vo.ResVo;
import com.mu.musmart.service.dic.DicDealerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 处理数据字典功能处理器
 * @author lijinmu
 * @data 2026-03-12
 */
@RestController
@RequestMapping("/dealerDic/api")
public class DicDealerController {

    @Autowired
    private DicDealerService dicDealerService;

    /**
     * 获取系统字典数据
     * @return
     */
    @GetMapping("/getDicMap")
    public ResVo<Map<String , BaseDicDO>> getDicMap(){
        Map<String , BaseDicDO> dicMap = dicDealerService.getDicMap();
        return ResVo.ok(dicMap);
    }
}
