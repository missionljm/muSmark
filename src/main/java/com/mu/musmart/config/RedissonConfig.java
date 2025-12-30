package com.mu.musmart.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置类
 * 配置RedissonClient以连接Redis服务器
 * 
 * @author 
 * @date 2025/12/29
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String host;

    @Value("${spring.data.redis.port:6379}")
    private int port;

    @Value("${spring.data.redis.password:}")
    private String password;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        
        // 根据application.yml中的配置设置Redis连接
        String address = "redis://" + host + ":" + port;
        config.useSingleServer()
              .setAddress(address)
              .setPassword(password.isEmpty() ? null : password)
              .setConnectionPoolSize(50)
              .setConnectionMinimumIdleSize(10);

        return Redisson.create(config);
    }
}