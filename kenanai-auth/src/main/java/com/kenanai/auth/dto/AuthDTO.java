package com.kenanai.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 认证返回对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 过期时间（秒）
     */
    private Long expiresIn;
} 