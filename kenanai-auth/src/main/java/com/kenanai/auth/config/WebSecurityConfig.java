package com.kenanai.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Web安全配置
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 禁用CSRF
        http.csrf(csrf -> csrf.disable())
                // 禁用HTTP Basic认证
                .httpBasic(basic -> basic.disable())
                // 设置无状态会话
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 配置请求授权
                .authorizeHttpRequests(auth -> auth
                        // 允许登录和验证端点匿名访问
                        .requestMatchers("/auth/login", "/auth/validate").permitAll()
                        // 其它请求需要认证
                        .anyRequest().authenticated()
                );

        return http.build();
    }
} 