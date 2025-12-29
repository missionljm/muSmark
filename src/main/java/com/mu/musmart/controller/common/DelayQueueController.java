package com.mu.musmart.controller.common;

import com.mu.musmart.domain.vo.ResVo;
import com.mu.musmart.enums.common.StatusEnum;
import com.mu.musmart.service.DelayQueueService;
import com.mu.musmart.service.impl.DelayQueueExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 延迟队列控制器
 * 
 * @author 
 * @date 2025/12/23
 */
@RestController
@RequestMapping("/delay-queue")
public class DelayQueueController {

    @Autowired
    private DelayQueueService delayQueueService;
    
    @Autowired
    private DelayQueueExample delayQueueExample;

    /**
     * 添加订单超时任务
     */
    @PostMapping("/order-timeout")
    public ResVo<String> addOrderTimeoutTask(@RequestParam String orderId, 
                                           @RequestParam long delayTime) {
        String messageId = delayQueueExample.addOrderTimeoutTask(orderId, delayTime);
        if (messageId != null) {
            return ResVo.ok(messageId);
        } else {
            return ResVo.fail(StatusEnum.valueOf("添加订单超时任务失败"));
        }
    }

    /**
     * 添加消息重试任务
     */
    @PostMapping("/message-retry")
    public ResVo<String> addMessageRetryTask(@RequestParam String messageId, 
                                           @RequestParam String retryData, 
                                           @RequestParam long delayTime) {
        String result = delayQueueExample.addMessageRetryTask(messageId, retryData, delayTime);
        if (result != null) {
            return ResVo.ok(result);
        } else {
            return ResVo.fail(StatusEnum.valueOf("添加消息重试任务失败"));
        }
    }

    /**
     * 添加通知任务
     */
    @PostMapping("/notification")
    public ResVo<String> addNotificationTask(@RequestParam String userId, 
                                           @RequestParam String content, 
                                           @RequestParam long notifyTime) {
        String result = delayQueueExample.addNotificationTask(userId, content, notifyTime);
        if (result != null) {
            return ResVo.ok(result);
        } else {
            return ResVo.fail(StatusEnum.valueOf("添加通知任务失败"));
        }
    }

    /**
     * 获取队列消息数量
     */
    @GetMapping("/count/{queueName}")
    public ResVo<Map<String, Object>> getQueueCount(@PathVariable String queueName) {
        long count = delayQueueExample.getQueueMessageCount(queueName);
        
        Map<String, Object> result = new HashMap<>();
        result.put("queueName", queueName);
        result.put("messageCount", count);
        
        return ResVo.ok(result);
    }

    /**
     * 手动添加通用延迟消息
     */
    @PostMapping("/message")
    public ResVo<String> addDelayMessage(@RequestParam String queueName,
                                       @RequestParam String payload,
                                       @RequestParam long delayTime) {
        String messageId = delayQueueService.addDelayMessage(queueName, payload, delayTime);
        if (messageId != null) {
            return ResVo.ok(messageId);
        } else {
            return ResVo.fail(StatusEnum.valueOf("添加延迟消息失败"));
        }
    }

    /**
     * 手动添加指定时间的延迟消息
     */
    @PostMapping("/message-at-time")
    public ResVo<String> addDelayMessageAtTime(@RequestParam String queueName,
                                             @RequestParam String payload,
                                             @RequestParam long executeTime) {
        String messageId = delayQueueService.addDelayMessageWithTime(queueName, payload, executeTime);
        if (messageId != null) {
            return ResVo.ok(messageId);
        } else {
            return ResVo.fail(StatusEnum.valueOf("添加延迟消息失败"));
        }
    }
}