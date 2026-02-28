package com.mu.musmart.controller.role;


import cn.hutool.core.map.MapUtil;
import com.mu.musmart.context.ReqInfoContext;
import com.mu.musmart.domain.vo.ResVo;
import com.mu.musmart.enums.common.StatusEnum;
import com.mu.musmart.exception.ExceptionUtil;
import com.mu.musmart.service.user.RoleService;
import com.mu.musmart.util.role.aspect.RequestPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/role/api")
@Slf4j
public class UserRoleController {

    @Autowired
    private RoleService roleService;


    @GetMapping("/getMenuByRoleCode")
    @RequestPermission(value = "admin")
    public ResVo<Map> getMenuByRoleCode(@RequestParam("code") String code){
        return ResVo.ok(roleService.getMenuByRoleCode(code));
    }

    /**
     * 页面授权
     * @return
     */
    @PostMapping("/setRoles")
    @RequestPermission(value = "admin")
    public ResVo<String> setRoles(@RequestBody Map params){
        if (MapUtil.isEmpty(params)){
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS);
        }
        if (!params.containsKey("roleMap")){
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED , "请选择需要授权的角色");
        }
        if (!params.containsKey("userMap")){
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED , "请选择需要授权的用户");
        }
        try {
            roleService.toGrantAuthorization(params);
        } catch (Exception e) {
            throw ExceptionUtil.of(StatusEnum.UNEXPECT_ERROR , e.getMessage());
        }
        return ResVo.ok();
    }
}
