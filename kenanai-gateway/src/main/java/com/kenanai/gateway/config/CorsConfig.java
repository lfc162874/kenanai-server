package com.kenanai.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * 网关跨域配置
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许前端域名，这里需要修改成具体的域名，如果允许所有则设置为 * 但不能与 allowCredentials 同时使用
        config.addAllowedOriginPattern("*"); // 使用 pattern 替代固定的 origin
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 允许所有请求方法
        config.addAllowedMethod("*");
        // 允许携带Cookie
        config.setAllowCredentials(true);
        // 暴露响应头
        config.addExposedHeader("Authorization");
        // 预检请求的有效期，单位为秒
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config);
        
        return new CorsWebFilter(source);
    }
} 