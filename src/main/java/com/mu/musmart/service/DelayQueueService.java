package com.mu.musmart.service;

import com.mu.musmart.cache.DelayQueueMessage;
import com.mu.musmart.cache.RedisClient;
import com.mu.musmart.cache.RedisDelayQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 延迟队列服务
 * 管理多个延迟队列实例
 * 
 * @author 
 * @date 2025/12/23
 */
@Service
@Slf4j
public class DelayQueueService {

    // 存储所有延迟队列实例
    private final Map<String, RedisDelayQueue> delayQueues = new ConcurrentHashMap<>();

    /**
     * 初始化方法
     */
    @PostConstruct
    public void init() {
        // 延迟队列将在需要时按需创建
        log.info("延迟队列服务初始化完成");
    }



    /**
     * 销毁方法
     */
    @PreDestroy
    public void destroy() {
        // 关闭所有延迟队列
        for (RedisDelayQueue queue : delayQueues.values()) {
            queue.stop();
        }
        delayQueues.clear();
    }

    /**
     * 创建并启动一个延迟队列
     * 
     * @param queueName 队列名称
     * @param consumer 消费者处理器
     * @return RedisDelayQueue实例
     */
    public RedisDelayQueue createDelayQueue(String queueName, RedisDelayQueue.DelayQueueConsumer consumer) {
        String key = "delay_queue:" + queueName;
        
        // 如果队列已存在，先停止
        RedisDelayQueue existingQueue = delayQueues.get(queueName);
        if (existingQueue != null) {
            existingQueue.stop();
        }

        // 创建新的延迟队列
        RedisDelayQueue delayQueue = new RedisDelayQueue(key, consumer);
        delayQueues.put(queueName, delayQueue);
        
        // 启动队列
        delayQueue.start();
        
        return delayQueue;
    }

    /**
     * 添加延迟消息
     * 
     * @param queueName 队列名称
     * @param payload 消息体
     * @param delayTime 延迟时间（毫秒）
     * @return 消息ID
     */
    public String addDelayMessage(String queueName, String payload, long delayTime) {
        RedisDelayQueue queue = delayQueues.get(queueName);
        if (queue == null) {
            throw new IllegalArgumentException("延迟队列不存在: " + queueName);
        }
        return queue.addMessage(payload, delayTime);
    }

    /**
     * 添加延迟消息（指定执行时间）
     * 
     * @param queueName 队列名称
     * @param payload 消息体
     * @param executeTime 执行时间戳（毫秒）
     * @return 消息ID
     */
    public String addDelayMessageWithTime(String queueName, String payload, long executeTime) {
        RedisDelayQueue queue = delayQueues.get(queueName);
        if (queue == null) {
            throw new IllegalArgumentException("延迟队列不存在: " + queueName);
        }
        return queue.addMessageWithExecuteTime(payload, executeTime);
    }

    /**
     * 获取延迟队列实例
     * 
     * @param queueName 队列名称
     * @return RedisDelayQueue实例
     */
    public RedisDelayQueue getDelayQueue(String queueName) {
        return delayQueues.get(queueName);
    }

    /**
     * 停止指定的延迟队列
     * 
     * @param queueName 队列名称
     */
    public void stopDelayQueue(String queueName) {
        RedisDelayQueue queue = delayQueues.remove(queueName);
        if (queue != null) {
            queue.stop();
        }
    }

    /**
     * 获取队列中的消息数量
     * 
     * @param queueName 队列名称
     * @return 消息数量
     */
    public long getMessageCount(String queueName) {
        RedisDelayQueue queue = delayQueues.get(queueName);
        if (queue == null) {
            return 0;
        }
        return queue.getMessageCount();
    }
}