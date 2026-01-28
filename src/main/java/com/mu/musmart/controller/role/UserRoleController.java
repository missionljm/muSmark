package com.mu.musmart.controller.role;


import com.mu.musmart.domain.vo.ResVo;
import com.mu.musmart.service.user.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/role/api")
@Slf4j
public class UserRoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/getMenuByRoleCode")
    public ResVo<Map> getMenuByRoleCode(@RequestParam("code") String code){
        return ResVo.ok(roleService.getMenuByRoleCode(code));
    }
}
