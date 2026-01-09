package com.mu.musmart;

import com.mu.musmart.cache.DelayQueueMessage;
import com.mu.musmart.cache.RedisDelayQueue;
import com.mu.musmart.service.DelayQueueService;
import com.mu.musmart.service.impl.DelayQueueExample;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 延迟队列测试类
 * 
 * @author 
 * @date 2025/12/23
 */
@SpringBootTest
public class DelayQueueTest {

    @Autowired
    private DelayQueueService delayQueueService;
    
    @Autowired
    private DelayQueueExample delayQueueExample;

    @Test
    public void testDelayQueue() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        
        // 创建一个简单的延迟队列进行测试
        RedisDelayQueue delayQueue = delayQueueService.createDelayQueue("test_queue", new RedisDelayQueue.DelayQueueConsumer() {
            @Override
            public boolean consume(DelayQueueMessage message) {
                System.out.println("收到延迟消息: " + message.getPayload() + " at " + System.currentTimeMillis());
                latch.countDown();
                return true;
            }
        });
        
        // 添加一个5秒后执行的消息
        String payload = "Hello, this is a delayed message!";
        delayQueue.addMessage(payload, 1000); // 5秒后执行
        
        System.out.println("添加延迟消息，等待5秒后处理...");
        
        // 等待消息被处理
        latch.await(10, TimeUnit.SECONDS);
        
        // 停止队列
//        delayQueueService.stopDelayQueue("test_queue");
    }

    @Test
    public void testOrderTimeout() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        
        // 创建订单超时处理队列
        String queueName = "test_order_timeout";
        delayQueueService.createDelayQueue(queueName, this::consumeTry);
        
        // 添加订单超时任务
        String orderId = "ORDER_001";
        String payload = "{\"orderId\": \"" + orderId + "\", \"action\": \"cancel_if_not_paid\"}";
        delayQueueService.addDelayMessage(queueName, payload, 3000); // 3秒后执行
        
        System.out.println("添加订单超时任务，等待3秒后处理...");
        
        // 等待消息被处理
        latch.await(8, TimeUnit.SECONDS);
        
        // 停止队列
        delayQueueService.stopDelayQueue(queueName);
    }

    public boolean consumeTry(DelayQueueMessage message){
        CountDownLatch latch = new CountDownLatch(1);
        System.out.println("处理订单超时: " + message.getPayload() + " at " + System.currentTimeMillis());
        latch.countDown();
        return true;
    }
}