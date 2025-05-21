package com.kenanai.sharefile.service;

import com.kenanai.sharefile.model.LanMessage;
import java.util.List;

public interface LanMessageService {
    /**
     * 发送消息
     */
    void sendMessage(LanMessage message);

    /**
     * 获取与某用户的历史消息
     */
    List<LanMessage> getHistory(String userId1, String userId2);
} 