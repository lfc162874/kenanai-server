package com.kenanai.auth.config;

import com.kenanai.common.util.JwtUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JWT工具配置类
 */
@Configuration
public class JwtUtilConfig {

    @Bean
    @ConditionalOnMissingBean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }
} 