package com.mu.musmart.config;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.mu.musmart.cache.RedisClient;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
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

    /**
     * MyBatis Plus 分页插件配置
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }


}
