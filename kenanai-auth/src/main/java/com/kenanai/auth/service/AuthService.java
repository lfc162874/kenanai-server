package com.kenanai.auth.service;

import com.kenanai.auth.dto.AuthDTO;
import com.kenanai.auth.dto.LoginDTO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 认证信息
     */
    AuthDTO login(LoginDTO loginDTO);

    /**
     * 验证Token
     *
     * @param token JWT token
     * @return 是否有效
     */
    boolean validateToken(String token);
} 