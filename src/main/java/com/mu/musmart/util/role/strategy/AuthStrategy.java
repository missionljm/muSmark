package com.mu.musmart.util.role.strategy;

/**
 * 都去实现这个策略
 */
@FunctionalInterface
public interface AuthStrategy {

    void verify(String requestValue);

}
