package com.kenanai.chat.controller;

import com.kenanai.chat.service.MemoryChatService;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.image.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RestController
@RequestMapping("/ai")
public class ChatController {

  private final ChatClient chatClient;

  private final ImageModel imageModel;

  @Resource
  private MemoryChatService memoryChatService;

  public ChatController(ChatClient.Builder builder, ImageModel imageModel) {
    this.chatClient = builder.defaultSystem("你是一个高智商的侦探，用江户川柯南的语气回答问题").build();
      this.imageModel = imageModel;
  }

  @GetMapping("/chat")
  public String chat(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
    return this.chatClient.prompt()
        .user(message)
        .call()
        .content();
  }

  @GetMapping("/stream/chat")
  public String streamChat(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
    Flux<String> content = this.chatClient.prompt()
            .user(message)
            .stream()
            .content();
    content.subscribe(System.out::println);
    return "";
  }

  /**
   * 带有记忆的对话
   * @param input
   * @return
   */
  @RequestMapping("/memory")
  public String memory(String input,String messageId) {
    if ("0".equals(messageId)){
      messageId = UUID.randomUUID().toString();
    }
    return memoryChatService.memory(input,messageId);
  }

}