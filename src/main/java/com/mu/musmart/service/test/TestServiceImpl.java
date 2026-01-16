package com.mu.musmart.service.test;

import cn.hutool.core.util.ObjectUtil;
import com.mu.musmart.util.async.AsyncUtil;
import com.mu.musmart.util.role.enums.RoleStrategyEnum;
import com.mu.musmart.util.role.handler.AbstractPermissionHandler;
import com.mu.musmart.util.role.strategy.AuthStrategy;
import com.mu.musmart.util.role.strategy.impl.DoRoleStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service("TestServiceImpl")
@Slf4j
public class TestServiceImpl {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AbstractPermissionHandler abstractPermissionHandler;

    @PostConstruct
    public void init() {
        getStrategy();
    }

    public void getStrategy(){
        AsyncUtil.execute(()->{
            while (ObjectUtil.isEmpty(abstractPermissionHandler.strategyMap)){
                log.info("StrategyMap is null");
            }
            log.info("StrategyMap:{}" , abstractPermissionHandler.strategyMap);
        });
        DoRoleStrategy strategy = (DoRoleStrategy) abstractPermissionHandler.getStrategy(RoleStrategyEnum.ROLE_USER_STRATEGY);
        strategy.verify("");

    }

}
