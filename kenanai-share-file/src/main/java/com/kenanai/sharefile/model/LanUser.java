package com.kenanai.sharefile.model;

import lombok.Data;

@Data
public class LanUser {
    private String userId;
    private String username;
    private String avatar;
    private String ip;
    private Integer port;
    private boolean online;
} 