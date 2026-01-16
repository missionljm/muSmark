package com.mu.musmart.service;

import com.mu.musmart.cache.RedisClient;
import com.mu.musmart.util.async.AsyncUtil;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Redis初始化服务
 * 确保Redis连接正常后再使用
 * 
 * @author 
 * @date 2025/12/29
 */
@Service
public class RedisInitService {

    private static final Logger logger = LoggerFactory.getLogger(RedisInitService.class);

    @Autowired(required = false)
    private RedissonClient redissonClient;



    @PostConstruct
    public void init() {
        // 等待RedisClient被正确初始化
        waitForRedisInitialization();
    }

    /**
     * 等待Redis初始化完成
     */
    private void waitForRedisInitialization() {
        AsyncUtil.execute(()->{
            int maxRetries = 100; // 最多等待30次，每次100ms，总共3秒
            int retries = 0;
            while (retries < maxRetries) {
                if (redissonClient != null && RedisClient.isRedissonAvailable()) {
                    logger.info("Redis初始化完成");
                    // 进行简单测试
                    try {
                        String testKey = "redis_init_test_" + System.currentTimeMillis();
                        RedisClient.setStr(testKey, "test");
                        String result = RedisClient.getStr(testKey);
                        if ("test".equals(result)) {
                            logger.info("Redis连接测试成功");
                            return;
                        }
                    } catch (Exception e) {
                        logger.warn("Redis连接测试失败，继续等待... (retry {})", retries + 1);
                    }
                }

                retries++;
                logger.info("次数:{}" , retries);
                try {
                    Thread.sleep(50); // 等待100ms
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            if (retries >= maxRetries) {
                logger.error("Redis初始化超时，请检查Redis服务是否正常运行");
            }
        });

    }
}