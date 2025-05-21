package com.kenanai.sharefile.service.impl;

import com.kenanai.sharefile.model.LanMessage;
import com.kenanai.sharefile.service.LanMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class LanMessageServiceImpl implements LanMessageService {
    // 聊天历史，key为userId1_userId2（排序后）
    private final Map<String, List<LanMessage>> historyMap = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendMessage(LanMessage message) {
        // 存储历史
        String key = getChatKey(message.getSenderId(), message.getReceiverId());
        historyMap.computeIfAbsent(key, k -> new ArrayList<>()).add(message);
        // WebSocket推送到接收者
        messagingTemplate.convertAndSendToUser(
                message.getReceiverId(),
                "/queue/messages",
                message
        );
    }

    @Override
    public List<LanMessage> getHistory(String userId1, String userId2) {
        String key = getChatKey(userId1, userId2);
        return historyMap.getOrDefault(key, Collections.emptyList());
    }

    private String getChatKey(String userId1, String userId2) {
        return userId1.compareTo(userId2) < 0 ? userId1 + "_" + userId2 : userId2 + "_" + userId1;
    }
} 