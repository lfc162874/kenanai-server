package com.kenanai.chat.service;

public interface MemoryChatService {

    /**
     * 根据记忆回答内容
     * @param message 消息
     * @param messageId 消息ID
     * @return 回答内容
     */
    String memory(String message,  String messageId);
}
