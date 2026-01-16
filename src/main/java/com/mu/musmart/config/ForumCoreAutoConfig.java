package com.mu.musmart.config;


import com.mu.musmart.cache.RedisClient;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.redisson.api.RedissonClient;

/**
 * @author missionLjm
 * @date 2025/9/4
 */
@Configuration
@ComponentScan(basePackages = "com.mu.musmart")
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class ForumCoreAutoConfig {

    public ForumCoreAutoConfig(RedissonClient redissonClient) {
        RedisClient.register(redissonClient);
    }


}
