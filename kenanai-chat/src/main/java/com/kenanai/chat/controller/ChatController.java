package com.kenanai.chat.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.image.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class ChatController {

  private final ChatClient chatClient;

  private final ImageModel imageModel;

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

  @RequestMapping("/image")
  public String image(String input) {
    ImageOptions options = ImageOptionsBuilder.builder()
            .withModel("wanx-v1")
            .build();

    ImagePrompt imagePrompt = new ImagePrompt(input, options);
    ImageResponse response = imageModel.call(imagePrompt);
    String imageUrl = response.getResult().getOutput().getUrl();

    return "redirect:" + imageUrl;
  }
}