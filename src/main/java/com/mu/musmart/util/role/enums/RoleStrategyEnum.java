package com.mu.musmart.util.role.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum RoleStrategyEnum {

    ROLE_USER_STRATEGY("ROLE_USER_STRATEGY", "用户权限验证"),

    ROLE_URL_STRATEGY("ROLE_URL_STRATEGY", "URL权限验证")
    ;


    private String code;

    private String context;


    /**
     * 根据code获取枚举
     * @param code
     * @return
     */
    public static RoleStrategyEnum getContextByCode(String code){
        for (RoleStrategyEnum value : RoleStrategyEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }




}
