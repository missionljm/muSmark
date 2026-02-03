package com.mu.musmart.controller.user;

import com.mu.musmart.context.ReqInfoContext;
import com.mu.musmart.dao.user.UserDao;
import com.mu.musmart.domain.dto.user.BaseUserInfoDTO;
import com.mu.musmart.domain.vo.ResVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/getUser/api")
@Slf4j
public class UserController {

    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("/getUserInfo")
    public ResVo<ReqInfoContext.ReqInfo> getUserInfo(){
        ReqInfoContext.ReqInfo reqInfo = ReqInfoContext.getReqInfo();
        return ResVo.ok(reqInfo);
    }
}
