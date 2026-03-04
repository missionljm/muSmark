package com.mu.musmart.controller.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mu.musmart.context.ReqInfoContext;
import com.mu.musmart.dao.user.UserDao;
import com.mu.musmart.domain.dto.user.BaseUserInfoDTO;
import com.mu.musmart.domain.entity.user.UserDO;
import com.mu.musmart.domain.vo.ResVo;
import com.mu.musmart.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/getUser/api")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("/getUserInfo")
    public ResVo<ReqInfoContext.ReqInfo> getUserInfo(){
        ReqInfoContext.ReqInfo reqInfo = ReqInfoContext.getReqInfo();
        return ResVo.ok(reqInfo);
    }

    @GetMapping("/getUserPermissions")
    public ResVo<List<Map>> getUserPermissions(){
        return ResVo.ok(userService.getUserPermissions());
    }

    @GetMapping("/pageQueryUserList")
    public ResVo<IPage<Map>> getUserPageList(@RequestParam(required = false) Map params){
        return ResVo.ok(userService.getPageUserList(params));
    }
}
