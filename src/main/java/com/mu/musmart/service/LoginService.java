package com.mu.musmart.service;


import java.util.Map;

/**
 * @author YiHui
 * @date 2022/8/15
 */
public interface LoginService {
    String SESSION_KEY = "f-session";
    String USER_DEVICE_KEY = "f-device";


//    /**
//     * 适用于微信公众号登录场景下，自动注册一个用户
//     *
//     * @param uuid 微信唯一标识
//     * @return userId 用户主键
//     */
//    Long autoRegisterWxUserInfo(String uuid);

    /**
     * 登出
     *
     * @param session 用户会话
     */
    void logout(String session);

//    /**
//     * 给微信公众号的用户生成一个用于登录的会话
//     *
//     * @param userId 用户主键id
//     * @return
//     */
//    String loginByWx(Long userId);

    /**
     * 用户名密码方式登录
     *
     * @return
     */
    String loginByUserPwd(Map loginPar);

}
