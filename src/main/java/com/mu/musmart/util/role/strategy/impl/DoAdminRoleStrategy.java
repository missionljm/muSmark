package com.mu.musmart.util.role.strategy.impl;

import cn.hutool.json.JSONUtil;
import com.mu.musmart.context.ReqInfoContext;
import com.mu.musmart.enums.common.StatusEnum;
import com.mu.musmart.exception.ExceptionUtil;
import com.mu.musmart.service.user.RoleService;
import com.mu.musmart.util.role.strategy.AuthStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component("ROLE_ADMIN_STRATEGY")
@Slf4j
public class DoAdminRoleStrategy implements AuthStrategy {

    @Autowired
    private RoleService roleService;


    /**
     * 接口权限过滤
     * @param requestValue
     */
    @Override
    public void verify(String requestValue) {
        //判断该角色是否拥有该接口权限
        log.info(requestValue+"ROLE_ADMIN_STRATEGY");
        ReqInfoContext.ReqInfo reqInfo = ReqInfoContext.getReqInfo();
        List<String> roles = roleService.queryRoleListByUserId(reqInfo.getUserId());
        log.info("DoAdminRoleStrategy_verify_roles:{}" , JSONUtil.toJsonStr(roles));
        if (!roles.contains(requestValue)){
            throw ExceptionUtil.of(StatusEnum.FORBID_ERROR_MIXED , "用户角色无访问权限");
        }
    }
}
