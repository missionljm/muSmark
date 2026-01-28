package com.mu.musmart.util.role.strategy.impl;

import com.mu.musmart.util.role.strategy.AuthStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component("ROLE_ADMIN_STRATEGY")
@Slf4j
public class DoAdminRoleStrategy implements AuthStrategy {
    @Override
    public void verify(String requestValue) {
        log.info(requestValue+"ROLE_ADMIN_STRATEGY");
    }
}
