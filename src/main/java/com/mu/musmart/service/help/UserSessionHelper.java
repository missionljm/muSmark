package com.mu.musmart.service.help;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mu.musmart.cache.RedisClient;
import com.mu.musmart.mdc.SelfTraceIdGenerator;
import com.mu.musmart.util.JsonUtil;
import com.mu.musmart.util.MapUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.util.Date;

/**
 * 用户登录JWT核心类
 */
@Slf4j
@Component
public class UserSessionHelper {


    @Component
    @Data
    @ConfigurationProperties("muSmart.jwt")
    public static class JwtProperties {
        /**
         * 签发人
         */
        private String issuer;
        /**
         * 密钥
         */
        private String secret;
        /**
         * 有效期，毫秒时间戳
         */
        private Long expire;
    }

    private final JwtProperties jwtProperties;

    private Algorithm algorithm;

    private JWTVerifier verifier;

    public UserSessionHelper(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        algorithm = Algorithm.HMAC256(jwtProperties.getSecret());
        verifier = JWT.require(algorithm).withIssuer(jwtProperties.getIssuer()).build();
    }

    /**
     * 获取用户token
     * @param userId
     * @return
     */
    public String getSession(Long userId){
        String session = JsonUtil.toStr(MapUtils.create("s" , SelfTraceIdGenerator.generate() , "v" , userId));
        String token = JWT.create().withIssuer(jwtProperties.getIssuer()).withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpire()))
                .withPayload(session)
                .sign(algorithm);
        RedisClient.setStrWithExpire(token , String.valueOf(userId) , jwtProperties.getExpire()/100);
        return token;
    }

    /**
     * 注销时调用，移除登录状态
     * @param token
     */
    public void removeSession(String token){
        RedisClient.del(token);
    }

    /**
     * 通过session获取用户id
     * @param session
     * @return
     */
    public Long getUserIdBySession(String session){
        try {
            DecodedJWT decodedJWT = verifier.verify(session);
            String userId = String.valueOf(Base64Utils.decodeFromString(decodedJWT.getPayload()));

            String user = RedisClient.getStr(userId);
            if (StrUtil.isEmpty(user) || !StrUtil.equals(userId , user)){
                return null;
            }
            return Long.parseLong(userId);
        } catch (JWTVerificationException e) {
            log.info("jwt token校验失败! token: {}, msg: {}", session, e.getMessage());
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            log.info("jwt token校验失败! token: {}, msg: {}", session, e.getMessage());
            throw new RuntimeException(e);
        }

    }


}
