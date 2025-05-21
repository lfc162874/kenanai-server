package com.kenanai.sharefile.service.impl;

import com.kenanai.sharefile.model.LanUser;
import com.kenanai.sharefile.service.LanUserService;
import com.kenanai.user.api.UserService;
import com.kenanai.user.api.dto.UserDTO;
import com.kenanai.common.entity.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LanUserServiceImpl implements LanUserService {
    // 维护在线用户ID列表
    private final Set<String> onlineUserIds = ConcurrentHashMap.newKeySet();
    @DubboReference
    private  UserService userService;
    private static final int PORT = 8888;
    private static final int HEARTBEAT_INTERVAL = 5000; // ms
    private static final int TIMEOUT = 15000; // ms
    private DatagramSocket socket;
    private volatile boolean running = true;
    private final Map<String, Long> lastHeartbeatMap = new ConcurrentHashMap<>();
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostConstruct
    public void startUdpListener() {
        // 启动UDP监听线程，接收其他用户的心跳包
        new Thread(() -> {
            try {
                socket = new DatagramSocket(PORT);
                byte[] buf = new byte[256];
                while (running) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    String userId = new String(packet.getData(), 0, packet.getLength());
                    boolean isNew = onlineUserIds.add(userId);
                    lastHeartbeatMap.put(userId, System.currentTimeMillis());
                    if (isNew) {
                        notifyOnlineUsers();
                        notifyUserStatus(userId, true);
                    }
                }
            } catch (Exception e) {
                log.error("UDP监听异常", e);
            }
        }, "LanUser-UDP-Listener").start();
    }

    @PreDestroy
    public void stopUdpListener() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    @Scheduled(fixedRate = HEARTBEAT_INTERVAL)
    public void sendHeartbeat() {
        // 定时广播本机用户ID
        try (DatagramSocket ds = new DatagramSocket()) {
            String userId = getCurrentUserId(); // TODO: 通过认证体系获取
            byte[] data = userId.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("255.255.255.255"), PORT);
            ds.setBroadcast(true);
            ds.send(packet);
        } catch (Exception e) {
            log.error("UDP心跳广播异常", e);
        }
    }

    @Scheduled(fixedRate = HEARTBEAT_INTERVAL)
    public void checkTimeout() {
        // 定时检测超时未收到心跳的用户，自动下线
        long now = System.currentTimeMillis();
        List<String> removed = new ArrayList<>();
        onlineUserIds.removeIf(userId -> {
            Long last = lastHeartbeatMap.get(userId);
            boolean timeout = last == null || now - last > TIMEOUT;
            if (timeout) removed.add(userId);
            return timeout;
        });
        if (!removed.isEmpty()) {
            notifyOnlineUsers();
            for (String userId : removed) {
                notifyUserStatus(userId, false);
            }
        }
    }

    private void notifyOnlineUsers() {
        List<LanUser> users = getOnlineUsers();
        messagingTemplate.convertAndSend("/topic/lan/onlineUsers", users);
    }

    private void notifyUserStatus(String userId, boolean online) {
        // 推送单个用户上下线事件
        R<UserDTO> r = userService.findByUsername(userId);
        if (r != null && r.getData() != null) {
            LanUser lanUser = new LanUser();
            lanUser.setUserId(String.valueOf(r.getData().getId()));
            lanUser.setUsername(r.getData().getUsername());
            lanUser.setAvatar(r.getData().getAvatar());
            lanUser.setOnline(online);
            messagingTemplate.convertAndSend("/topic/lan/userStatus", lanUser);
        }
    }

    private String getCurrentUserId() {
        // TODO: 通过认证体系获取当前用户ID
        return "mockUserId";
    }

    @Override
    public void userOnline(String userId) {
        onlineUserIds.add(userId);
        // TODO: 广播上线心跳
    }

    @Override
    public void userOffline(String userId) {
        onlineUserIds.remove(userId);
        // TODO: 广播下线心跳
    }

    @Override
    public List<String> getOnlineUserIds() {
        return new ArrayList<>(onlineUserIds);
    }

    @Override
    public List<LanUser> getOnlineUsers() {
        // 目前 userService 只支持单个查找，批量查建议扩展接口
        List<LanUser> result = new ArrayList<>();
        for (String userId : onlineUserIds) {
            // 这里假设 userId 就是 username，实际应根据业务调整
            R<UserDTO> r = userService.findByUsername(userId);
            if (r != null && r.getData() != null) {
                UserDTO user = r.getData();
                LanUser lanUser = new LanUser();
                lanUser.setUserId(String.valueOf(user.getId()));
                lanUser.setUsername(user.getUsername());
                lanUser.setAvatar(user.getAvatar());
                lanUser.setOnline(true);
                result.add(lanUser);
            }
        }
        return result;
    }
} 