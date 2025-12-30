package com.mu.musmart.config;

import com.mu.musmart.cache.DelayQueueMessage;
import com.mu.musmart.cache.RedisDelayQueue;

public class TestCountConsumer implements RedisDelayQueue.DelayQueueConsumer {

    @Override
    public boolean consume(DelayQueueMessage message) {

        return false;
    }
}
