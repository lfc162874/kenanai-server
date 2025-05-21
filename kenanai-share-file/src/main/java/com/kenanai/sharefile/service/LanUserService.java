package com.kenanai.sharefile.service;

import com.kenanai.sharefile.model.LanUser;
import java.util.List;

public interface LanUserService {
    /**
     * 用户上线（广播心跳）
     */
    void userOnline(String userId);

    /**
     * 用户下线
     */
    void userOffline(String userId);

    /**
     * 获取当前在线用户ID列表
     */
    List<String> getOnlineUserIds();

    /**
     * 获取当前在线用户详情（通过user-api）
     */
    List<LanUser> getOnlineUsers();
} 