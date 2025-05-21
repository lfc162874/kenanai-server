package com.kenanai.sharefile.model;

import lombok.Data;

@Data
public class LanFile {
    private String fileId;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private String senderId;
    private String receiverId;
    private String url;
} 