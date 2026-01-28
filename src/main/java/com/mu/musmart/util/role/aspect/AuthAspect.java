package com.mu.musmart.util.role.aspect;

import com.mu.musmart.util.role.enums.RoleStrategyEnum;
import com.mu.musmart.util.role.handler.AbstractPermissionHandler;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class AuthAspect {

    @Autowired AbstractPermissionHandler abstractPermissionHandler;

    @Pointcut("@annotation(RequestPermission)")
    public void doBefore(){
    }

    @Before("doBefore()")
    private void doPermission(JoinPoint joinPoint){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequestPermission permission = method.getAnnotation(RequestPermission.class);
        if (permission == null){
            return;
        }
        // 权限
        String strategy = permission.type();
        //
        String permissionValue = permission.value();
        RoleStrategyEnum contextByCode = RoleStrategyEnum.getContextByCode(strategy);
        abstractPermissionHandler.getStrategy(contextByCode).verify(permissionValue);
    }
}
