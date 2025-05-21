package com.kenanai.sharefile.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LanMessage {
    private String messageId;
    private String senderId;
    private String receiverId;
    private String content;
    private LocalDateTime timestamp;
    private MessageType type;

    public enum MessageType {
        TEXT, // 文本消息
        FILE  // 文件消息
    }
} 