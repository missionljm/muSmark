package com.mu.musmart.service.impl;

import com.mu.musmart.cache.DelayQueueMessage;
import com.mu.musmart.cache.RedisDelayQueue;
import com.mu.musmart.service.DelayQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 延迟队列使用示例
 *
 * @author
 * @date 2025/12/23
 */
@Service
public class DelayQueueExample {

    @Autowired
    private DelayQueueService delayQueueService;

    @PostConstruct
    public void init() {
        // 初始化示例延迟队列
        initExampleQueues();
    }

    /**
     * 初始化示例队列
     */
    private void initExampleQueues() {
        // 创建订单超时取消队列
        delayQueueService.createDelayQueue("order_timeout", this::handleOrderTimeout);

        // 创建消息重试队列
        delayQueueService.createDelayQueue("message_retry", this::handleMessageRetry);

        // 创建通知提醒队列
        delayQueueService.createDelayQueue("notification", this::handleNotification);

        System.out.println("延迟队列示例初始化完成");
    }

    /**
     * 处理订单超时
     */
    private boolean handleOrderTimeout(DelayQueueMessage message) {
        try {
            System.out.println("处理订单超时: " + message.getPayload() + ", 执行时间: " + message.getExecuteTime());
            // 这里可以实现具体的订单超时处理逻辑
            // 例如：更新订单状态为已取消、释放库存等
            return true; // 返回true表示处理成功
        } catch (Exception e) {
            System.err.println("处理订单超时失败: " + e.getMessage());
            return false; // 返回false表示处理失败，会触发重试
        }
    }

    /**
     * 处理消息重试
     */
    private boolean handleMessageRetry(DelayQueueMessage message) {
        try {
            System.out.println("处理消息重试: " + message.getPayload());
            // 这里可以实现消息重试逻辑
            // 例如：重新发送失败的消息
            return true;
        } catch (Exception e) {
            System.err.println("处理消息重试失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 处理通知
     */
    private boolean handleNotification(DelayQueueMessage message) {
        try {
            System.out.println("发送通知: " + message.getPayload());
            // 这里可以实现通知发送逻辑
            // 例如：发送邮件、短信、站内信等
            return true;
        } catch (Exception e) {
            System.err.println("发送通知失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 添加订单超时任务
     *
     * @param orderId 订单ID
     * @param delayTime 延迟时间（毫秒）
     */
    public String addOrderTimeoutTask(String orderId, long delayTime) {
        String payload = "{\"orderId\": \"" + orderId + "\", \"action\": \"cancel_if_not_paid\"}";
        return delayQueueService.addDelayMessage("order_timeout", payload, delayTime);
    }

    /**
     * 添加消息重试任务
     *
     * @param messageId 消息ID
     * @param retryData 重试数据
     * @param delayTime 延迟时间（毫秒）
     */
    public String addMessageRetryTask(String messageId, String retryData, long delayTime) {
        String payload = "{\"messageId\": \"" + messageId + "\", \"retryData\": \"" + retryData + "\"}";
        return delayQueueService.addDelayMessage("message_retry", payload, delayTime);
    }

    /**
     * 添加通知任务
     *
     * @param userId 用户ID
     * @param notificationContent 通知内容
     * @param notifyTime 通知时间戳
     */
    public String addNotificationTask(String userId, String notificationContent, long notifyTime) {
        String payload = "{\"userId\": \"" + userId + "\", \"content\": \"" + notificationContent + "\"}";
        return delayQueueService.addDelayMessageWithTime("notification", payload, notifyTime);
    }

    /**
     * 获取队列消息数量
     */
    public long getQueueMessageCount(String queueName) {
        return delayQueueService.getMessageCount(queueName);
    }
}