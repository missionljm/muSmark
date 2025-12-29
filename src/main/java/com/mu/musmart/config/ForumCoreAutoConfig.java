package com.mu.musmart.config;


import com.mu.musmart.cache.RedisClient;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author YiHui
 * @date 2022/9/4
 */
@Configuration
@ComponentScan(basePackages = "com.mu.musmart")
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class ForumCoreAutoConfig {

    public ForumCoreAutoConfig(RedisTemplate<String, String> redisTemplate) {
        RedisClient.register(redisTemplate);
    }


}
