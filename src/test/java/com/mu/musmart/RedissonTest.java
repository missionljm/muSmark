package com.mu.musmart;

import com.mu.musmart.cache.RedisClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

@SpringBootTest
public class RedissonTest {

    private static final Logger logger = LoggerFactory.getLogger(RedissonTest.class);

    @Resource
    private RedissonClient redissonClient;

    @Test
    public void testRedissonConnection() {
        // 测试RedissonClient是否注入成功
        assert redissonClient != null : "RedissonClient should not be null";
        logger.info("RedissonClient注入成功: {}", redissonClient != null);

        // 测试RedisClient是否可用
        boolean isRedissonAvailable = RedisClient.isRedissonAvailable();
        logger.info("RedisClient Redisson可用性: {}", isRedissonAvailable);
        assert isRedissonAvailable : "RedisClient should be available";

        // 测试基本的Redis操作
        String testKey = "test_key_" + System.currentTimeMillis();
        String testValue = "test_value";

        // 设置值
        RedisClient.setStr(testKey, testValue);
        logger.info("设置值成功: {} = {}", testKey, testValue);

        // 获取值
        String result = RedisClient.getStr(testKey);
        logger.info("获取值: {} = {}", testKey, result);

        assert testValue.equals(result) : "获取的值应该与设置的值相同";
        logger.info("Redis基本操作测试通过");
    }
}