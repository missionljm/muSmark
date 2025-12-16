package com.mu.musmart.controller.common;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mu.musmart.cache.RedisClient;
import com.mu.musmart.context.ReqInfoContext;
import com.mu.musmart.domain.vo.ResVo;
import com.mu.musmart.enums.common.StatusEnum;
import com.mu.musmart.service.LoginService;
import com.mu.musmart.service.impl.LoginServiceImpl;
import com.mu.musmart.util.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.util.Map;

@RestController
@RequestMapping("/commonLogin-Api")
@Slf4j
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResVo<String> login(@RequestBody Map loginPar , HttpServletRequest req , HttpServletResponse resp) {
        String token = loginService.loginByUserPwd(loginPar);
        resp.addCookie(SessionUtil.newCookie(LoginService.SESSION_KEY , token));
        log.info("login successful");
        return ResVo.ok();
    }

    @GetMapping("/getVerificationCode")
    public ResVo<String> getVerificationCode(@RequestParam("userAccount") String userAccount , ServletRequest req){
        if (StrUtil.isEmpty(userAccount)){
            return ResVo.fail(StatusEnum.UNEXPECT_ERROR , "账号必填");
        }
        if(loginService.verifyUser(userAccount)){
            return ResVo.fail(StatusEnum.UNEXPECT_ERROR , "账号不存在，请联系管理员");
        }
        CircleCaptcha lineCaptcha =  CaptchaUtil.createCircleCaptcha(200 , 100 );
        String verificationCode = lineCaptcha.getImageBase64();
        lineCaptcha.setBackground(Color.BLACK);
        String code = lineCaptcha.getCode();
        RedisClient.setStr(userAccount , code);
        RedisClient.expire(userAccount , Long.valueOf(60 * 1000));
        return ResVo.ok(verificationCode);
    }

    @PostMapping("/test")
    public ResVo<String> test(@RequestBody Map map) {
        log.info("test: {}" , JSONUtil.toJsonStr(map));
        ReqInfoContext.ReqInfo reqInfo = ReqInfoContext.getReqInfo();
        log.info("test successful:{}" , JSONUtil.toJsonStr(reqInfo));
        return ResVo.ok();
    }

}
