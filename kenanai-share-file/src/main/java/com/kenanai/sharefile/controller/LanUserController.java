package com.kenanai.sharefile.controller;

import com.alibaba.nacos.api.model.v2.Result;
import com.kenanai.sharefile.model.LanUser;
import com.kenanai.sharefile.service.LanUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/share/lan/users")
@RequiredArgsConstructor
public class LanUserController {
    private final LanUserService lanUserService;

    /**
     * 获取当前在线用户详情列表
     */
    @GetMapping("/online")
    public Result<List<LanUser>> getOnlineUsers() {
        return Result.success(lanUserService.getOnlineUsers());
    }
} 