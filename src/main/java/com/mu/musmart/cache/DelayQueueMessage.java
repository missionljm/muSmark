package com.mu.musmart.cache;

import java.io.Serializable;

/**
 * 延迟队列消息实体
 * @author 
 * @date 2025/12/23
 */
public class DelayQueueMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 消息体
     */
    private String payload;

    /**
     * 延迟执行时间戳（毫秒）
     */
    private long executeTime;

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 重试次数
     */
    private int retryCount = 0;

    /**
     * 最大重试次数
     */
    private int maxRetryCount = 3;

    public DelayQueueMessage() {
    }

    public DelayQueueMessage(String messageId, String payload, long executeTime, String messageType) {
        this.messageId = messageId;
        this.payload = payload;
        this.executeTime = executeTime;
        this.messageType = messageType;
    }

    // Getters and Setters
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public long getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(long executeTime) {
        this.executeTime = executeTime;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    @Override
    public String toString() {
        return "DelayQueueMessage{" +
                "messageId='" + messageId + '\'' +
                ", payload='" + payload + '\'' +
                ", executeTime=" + executeTime +
                ", messageType='" + messageType + '\'' +
                ", retryCount=" + retryCount +
                ", maxRetryCount=" + maxRetryCount +
                '}';
    }
}