package com.mu.musmart.util.role.handler;

/**
 * 权限验证处理接口
 */
public interface PermissionHandler {

    void handle();

    void next(PermissionHandler  handler);
}
