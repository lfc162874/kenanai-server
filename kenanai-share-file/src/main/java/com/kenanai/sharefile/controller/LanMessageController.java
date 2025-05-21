package com.kenanai.sharefile.controller;

import com.alibaba.nacos.api.model.v2.Result;
import com.kenanai.sharefile.model.LanMessage;
import com.kenanai.sharefile.service.LanMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/share/lan/messages")
@RequiredArgsConstructor
public class LanMessageController {
    private final LanMessageService lanMessageService;

    /**
     * 发送消息（HTTP接口）
     */
    @PostMapping("/send")
    public Result<Void> sendMessage(@RequestBody LanMessage message) {
        lanMessageService.sendMessage(message);
        return Result.success();
    }

    /**
     * 获取历史消息
     */
    @GetMapping("/history")
    public Result<List<LanMessage>> getHistory(@RequestParam String userId) {
        // 当前用户ID应通过认证获取，这里仅为结构演示
        String currentUserId = "mockUserId";
        return Result.success(lanMessageService.getHistory(currentUserId, userId));
    }

    /**
     * WebSocket消息端点
     */
    @MessageMapping("/lan.sendMessage")
    public void wsSendMessage(@Payload LanMessage message, SimpMessageHeaderAccessor headerAccessor) {
        // 可通过headerAccessor获取当前用户信息
        lanMessageService.sendMessage(message);
    }
} 