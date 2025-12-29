package com.mu.musmart.cache;

import com.mu.musmart.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Redis延迟队列实现
 * 使用Redis的ZSet数据结构实现延迟队列
 * score为执行时间戳，当当前时间大于等于score时，消息变为可消费状态
 * 
 * @author 
 * @date 2025/12/23
 */
public class RedisDelayQueue {
    private static final Logger logger = LoggerFactory.getLogger(RedisDelayQueue.class);

    // 延迟队列的Redis键
    private final String queueKey;
    
    // 消费者处理接口
    private final DelayQueueConsumer consumer;
    
    // 调度执行器，用于定时扫描队列
    private final ScheduledExecutorService scheduler;
    
    // 扫描间隔时间（毫秒）
    private final long scanInterval;
    
    // 是否正在运行
    private volatile boolean running = false;

    public RedisDelayQueue(String queueKey, DelayQueueConsumer consumer) {
        this(queueKey, consumer, 1000); // 默认每秒扫描一次
    }

    public RedisDelayQueue(String queueKey, DelayQueueConsumer consumer, long scanInterval) {
        this.queueKey = queueKey;
        this.consumer = consumer;
        this.scanInterval = scanInterval;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "RedisDelayQueue-" + queueKey);
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * 启动延迟队列消费者
     */
    public void start() {
        if (running) {
            logger.warn("延迟队列 {} 已经在运行中", queueKey);
            return;
        }
        
        running = true;
        logger.info("启动延迟队列: {}", queueKey);
        
        // 定时扫描延迟队列
        scheduler.scheduleWithFixedDelay(this::scanAndProcess, 0, scanInterval, TimeUnit.MILLISECONDS);
    }

    /**
     * 停止延迟队列消费者
     */
    public void stop() {
        if (!running) {
            logger.warn("延迟队列 {} 已经停止", queueKey);
            return;
        }
        
        running = false;
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        logger.info("停止延迟队列: {}", queueKey);
    }

    /**
     * 向延迟队列添加消息
     * 
     * @param payload 消息体
     * @param delayTime 延迟时间（毫秒）
     * @return 消息ID
     */
    public String addMessage(String payload, long delayTime) {
        return addMessageWithExecuteTime(payload, System.currentTimeMillis() + delayTime);
    }

    /**
     * 向延迟队列添加消息（指定执行时间）
     * 
     * @param payload 消息体
     * @param executeTime 执行时间戳（毫秒）
     * @return 消息ID
     */
    public String addMessageWithExecuteTime(String payload, long executeTime) {
        String messageId = generateMessageId();
        DelayQueueMessage message = new DelayQueueMessage(messageId, payload, executeTime, "DEFAULT");
        
        boolean result = RedisClient.zAdd(queueKey, JsonUtil.toStr(message), executeTime);
        if (result) {
            logger.debug("添加延迟消息成功: messageId={}, executeTime={}, queueKey={}", messageId, executeTime, queueKey);
            return messageId;
        } else {
            logger.error("添加延迟消息失败: messageId={}, executeTime={}, queueKey={}", messageId, executeTime, queueKey);
            return null;
        }
    }

    /**
     * 扫描并处理到期的消息
     */
    private void scanAndProcess() {
        try {
            if (!isRedisTemplateReady()) {
                logger.warn("RedisTemplate未初始化，跳过本次扫描: queueKey={}", queueKey);
                return;
            }
            logger.info("redis初始化成功，扫描开始");
            long currentTime = System.currentTimeMillis();
            
            // 获取所有到期的消息（score <= 当前时间）
            Set<String> expiredMessages = RedisClient.zRangeByScoreForString(queueKey, 0, currentTime);
            
            if (expiredMessages.isEmpty()) {

                return;
            }

            logger.debug("扫描到 {} 个到期消息", expiredMessages.size());

            for (String messageStr : expiredMessages) {
                try {
                    DelayQueueMessage message = JsonUtil.toObj(messageStr, DelayQueueMessage.class);
                    
                    // 检查消息是否仍然到期（防止并发问题）
                    if (message.getExecuteTime() <= currentTime) {
                        // 尝试从队列中移除消息
                        Long removedCount = RedisClient.zRem(queueKey, messageStr);
                        
                        if (removedCount > 0) {
                            // 处理消息
                            processMessage(message);
                        } else {
                            // 消息可能已经被其他消费者处理
                            logger.debug("消息可能已被其他消费者处理: messageId={}", message.getMessageId());
                        }
                    }
                } catch (Exception e) {
                    logger.error("处理延迟消息异常: messageStr={}", messageStr, e);
                }
            }
        } catch (Exception e) {
            logger.error("扫描延迟队列异常: queueKey={}", queueKey, e);
        }
    }

    /**
     * 检查RedisTemplate是否已准备就绪
     */
    private boolean isRedisTemplateReady() {
        // 检查RedisClient中的template是否为null
        return RedisClient.isTemplateAvailable();
    }

    /**
     * 处理单个消息
     */
    private void processMessage(DelayQueueMessage message) {
        try {
            logger.debug("开始处理延迟消息: messageId={}", message.getMessageId());
            
            // 调用消费者的处理方法
            boolean success = consumer.consume(message);
            
            if (success) {
                logger.debug("成功处理延迟消息: messageId={}", message.getMessageId());
            } else {
                logger.warn("处理延迟消息失败，将重新加入队列: messageId={}", message.getMessageId());
                handleFailedMessage(message);
            }
        } catch (Exception e) {
            logger.error("处理延迟消息异常: messageId={}", message.getMessageId(), e);
            handleFailedMessage(message);
        }
    }

    /**
     * 处理失败的消息（重试机制）
     */
    private void handleFailedMessage(DelayQueueMessage message) {
        int currentRetryCount = message.getRetryCount();
        
        if (currentRetryCount < message.getMaxRetryCount()) {
            // 增加重试次数
            message.setRetryCount(currentRetryCount + 1);
            
            // 重新计算执行时间（延迟更长时间后重试）
            long nextExecuteTime = System.currentTimeMillis() + calculateRetryDelay(currentRetryCount + 1);
            
            message.setExecuteTime(nextExecuteTime);
            
            // 重新添加到队列
            boolean result = RedisClient.zAdd(queueKey, JsonUtil.toStr(message), nextExecuteTime);
            if (result) {
                logger.info("消息重试已重新加入队列: messageId={}, retryCount={}", 
                           message.getMessageId(), message.getRetryCount());
            } else {
                logger.error("消息重试重新加入队列失败: messageId={}", message.getMessageId());
            }
        } else {
            logger.error("消息达到最大重试次数，丢弃: messageId={}, maxRetryCount={}", 
                        message.getMessageId(), message.getMaxRetryCount());
            // 可以考虑将失败的消息存入死信队列或其他处理
        }
    }

    /**
     * 计算重试延迟时间（指数退避算法）
     */
    private long calculateRetryDelay(int retryCount) {
        // 基础延迟1秒，每次重试延迟时间翻倍
        return (long) Math.pow(2, retryCount - 1) * 1000;
    }

    /**
     * 生成消息ID
     */
    private String generateMessageId() {
        return "delay_msg_" + UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取队列中消息的数量
     */
    public long getMessageCount() {
        return RedisClient.zCard(queueKey);
    }

    /**
     * 消费者接口
     */
    @FunctionalInterface
    public interface DelayQueueConsumer {
        /**
         * 消费延迟消息
         * @param message 延迟消息
         * @return true表示处理成功，false表示处理失败需要重试
         */
        boolean consume(DelayQueueMessage message);
    }
}