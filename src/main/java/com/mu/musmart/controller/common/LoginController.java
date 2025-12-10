package com.mu.musmart.controller.common;

import cn.hutool.json.JSONUtil;
import com.mu.musmart.domain.vo.ResVo;
import com.mu.musmart.service.LoginService;
import com.mu.musmart.service.impl.LoginServiceImpl;
import com.mu.musmart.util.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/commonLogin-Api")
@Slf4j
public class LoginController {

    @Autowired
    private LoginServiceImpl loginService;

    @PostMapping("/login")
    public ResVo<String> login(@RequestBody Map loginPar , HttpServletRequest req , HttpServletResponse resp) {
        String token = loginService.loginByUserPwd(loginPar);
        resp.addCookie(SessionUtil.newCookie(LoginService.SESSION_KEY , token));
        log.info("login successful");
        return ResVo.ok();
    }

    @PostMapping("/test")
    public ResVo<String> test(@RequestBody Map map) {
        log.info("test: {}" , JSONUtil.toJsonStr(map));
        log.info("test successful");
        return ResVo.ok();
    }
}
