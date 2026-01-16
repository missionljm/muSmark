package com.mu.musmart.util.role.handler;

import com.mu.musmart.enums.common.StatusEnum;
import com.mu.musmart.exception.ExceptionUtil;
import com.mu.musmart.util.role.enums.RoleStrategyEnum;
import com.mu.musmart.util.role.strategy.AuthStrategy;
import com.mu.musmart.util.role.strategy.impl.DoRoleStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 权限控制处理器
 */
@Component("AbstractPermissionHandler")
public class AbstractPermissionHandler {

    @Autowired
    public Map<String , AuthStrategy> strategyMap;

    public AuthStrategy getStrategy(RoleStrategyEnum strategy){
        AuthStrategy strategy1 = strategyMap.get(strategy.getCode());
        if (strategy1 == null){
            throw ExceptionUtil.of(StatusEnum.UNEXPECT_ERROR , "权限验证策略不存在");
        }
        return strategy1;
    }

}
