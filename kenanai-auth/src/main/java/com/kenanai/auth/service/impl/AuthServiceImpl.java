package com.kenanai.auth.service.impl;

import com.kenanai.auth.dto.AuthDTO;
import com.kenanai.auth.dto.LoginDTO;
import com.kenanai.auth.service.AuthService;
import com.kenanai.common.entity.R;
import com.kenanai.common.util.JwtUtil;
import com.kenanai.user.api.UserService;
import com.kenanai.user.api.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private JwtUtil jwtUtil;
    
    @DubboReference
    private UserService userService;

    @Override
    public AuthDTO login(LoginDTO loginDTO) {
        // 验证用户名和密码
        R<UserDTO> result = userService.validatePassword(loginDTO.getUsername(), loginDTO.getPassword());
        if (result.getCode() != 0 || result.getData() == null) {
            throw new IllegalArgumentException(result.getMsg());
        }
        
        UserDTO user = result.getData();
        
        // 生成JWT令牌
        String accessToken = jwtUtil.generateToken(user.getId().toString(), user.getUsername());
        
        // 构建认证信息
        AuthDTO authDTO = new AuthDTO();
        authDTO.setUserId(user.getId());
        authDTO.setUsername(user.getUsername());
        authDTO.setNickname(user.getNickname());
        authDTO.setAccessToken(accessToken);
        authDTO.setExpiresIn(jwtUtil.getExpireTime());
        
        return authDTO;
    }

    @Override
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
} 