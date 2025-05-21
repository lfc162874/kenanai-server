package com.kenanai.chat.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Service
@Slf4j
public class MemoryChatServiceImpl implements MemoryChatService{

    private final ChatClient chatClient;

    private ChatMemory chatMemory;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public MemoryChatServiceImpl(ChatClient.Builder builder) {
        chatMemory = new InMemoryChatMemory();
        this.chatClient = builder.defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .defaultSystem("你现在是一名旅游导师，要为每一个旅游爱好者提供权威的信息")
                .build();
    }

    @Override
    public String memory(String message,String messageId) {
        log.info("messageId:{}",messageId);
        //根据messageId去查找看看有没有对话记录，也就是conversantId，
        String cachConversantId = (String) redisTemplate.opsForValue().get(messageId);
        String finalConversantId;
        if (cachConversantId != null){
            finalConversantId = cachConversantId;
        }else {
            finalConversantId = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(messageId,finalConversantId);
        }
        // 如果没有就创建一个对话记录，如果有就继续对话
        //对话记忆的唯一标识
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, finalConversantId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        return chatResponse.getResult().getOutput().getContent();
    }
}
