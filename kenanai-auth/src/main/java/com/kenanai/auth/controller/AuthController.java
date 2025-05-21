package com.kenanai.auth.controller;

import com.kenanai.auth.dto.AuthDTO;
import com.kenanai.auth.dto.LoginDTO;
import com.kenanai.auth.service.AuthService;
import com.kenanai.common.constant.CommonConstants;
import com.kenanai.common.entity.R;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public R<AuthDTO> login(@RequestBody @Valid LoginDTO loginDTO) {
        try {
            AuthDTO authInfo = authService.login(loginDTO);
            return R.ok(authInfo);
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage(), e);
            return R.failed(e.getMessage());
        }
    }

    /**
     * 验证Token
     */
    @GetMapping("/validate")
    public R<Boolean> validateToken(@RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String authorization) {
        try {
            String token = authorization.replace(CommonConstants.TOKEN_PREFIX, "").trim();
            boolean valid = authService.validateToken(token);
            return R.ok(valid);
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return R.failed(false, "无效的Token");
        }
    }
} 