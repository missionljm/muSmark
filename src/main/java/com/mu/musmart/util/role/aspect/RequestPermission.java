package com.mu.musmart.util.role.aspect;

import com.mu.musmart.util.role.enums.RoleStrategyEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author missionljm
 * 权限过滤注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestPermission {

    String value();

    String type() default "ROLE_USER_STRATEGY";
}
