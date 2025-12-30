package com.mu.musmart.service;

import com.mu.musmart.cache.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 延迟队列初始化服务
 * 确保延迟队列在Redis连接可用后再启动
 * 
 * @author 
 * @date 2025/12/29
 */
@Service
public class DelayQueueInitService {

    private static final Logger logger = LoggerFactory.getLogger(DelayQueueInitService.class);

    @Autowired
    private DelayQueueService delayQueueService;

    @PostConstruct
    public void init() {
        // 等待一段时间确保Redis连接已建立
        waitForRedisAvailability();
        
        // 初始化示例延迟队列
        initExampleQueues();
    }

    /**
     * 等待Redis可用
     */
    private void waitForRedisAvailability() {
        int maxRetries = 50; // 最多等待50次，每次100ms，总共5秒
        int retries = 0;

        while (retries < maxRetries) {
            try {
                // 检查RedisClient是否已准备好
                if (RedisClient.isRedissonAvailable()) {
                    // 尝试执行一个简单的Redis操作
                    String testKey = "delay_queue_test_" + System.currentTimeMillis();
                    RedisClient.setStr(testKey, "test");
                    String result = RedisClient.getStr(testKey);
                    
                    if ("test".equals(result)) {
                        logger.info("Redis连接可用，延迟队列初始化服务启动");
                        return;
                    }
                }
            } catch (Exception e) {
                logger.debug("Redis连接不可用，等待... (retry {})", retries + 1);
            }

            retries++;
            try {
                Thread.sleep(50); // 等待100ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (retries >= maxRetries) {
            logger.error("Redis连接超时，延迟队列可能无法正常工作");
        }
    }

    /**
     * 初始化示例队列
     */
    private void initExampleQueues() {
        try {
            // 创建订单超时取消队列
            delayQueueService.createDelayQueue("order_timeout", message -> {
                try {
                    System.out.println("处理订单超时: " + message.getPayload() + ", 执行时间: " + message.getExecuteTime());
                    // 这里可以实现具体的订单超时处理逻辑
                    // 例如：更新订单状态为已取消、释放库存等
                    return true; // 返回true表示处理成功
                } catch (Exception e) {
                    System.err.println("处理订单超时失败: " + e.getMessage());
                    return false; // 返回false表示处理失败，会触发重试
                }
            });
            
            // 创建消息重试队列
            delayQueueService.createDelayQueue("message_retry", message -> {
                try {
                    System.out.println("处理消息重试: " + message.getPayload());
                    // 这里可以实现消息重试逻辑
                    // 例如：重新发送失败的消息
                    return true;
                } catch (Exception e) {
                    System.err.println("处理消息重试失败: " + e.getMessage());
                    return false;
                }
            });
            
            // 创建通知提醒队列
            delayQueueService.createDelayQueue("notification", message -> {
                try {
                    System.out.println("发送通知: " + message.getPayload());
                    // 这里可以实现通知发送逻辑
                    // 例如：发送邮件、短信、站内信等
                    return true;
                } catch (Exception e) {
                    System.err.println("发送通知失败: " + e.getMessage());
                    return false;
                }
            });
            
            logger.info("延迟队列示例初始化完成");
        } catch (Exception e) {
            logger.error("延迟队列初始化失败", e);
        }
    }
}