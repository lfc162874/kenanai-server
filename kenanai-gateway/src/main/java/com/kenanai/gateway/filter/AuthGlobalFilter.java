package com.kenanai.gateway.filter;

import com.kenanai.common.constant.CommonConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * JWT配置属性
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
class JwtProperties {
    private String secret;
    private List<String> ignoreUrls = new ArrayList<>();
}

/**
 * 认证全局过滤器
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtProperties jwtProperties;

    public AuthGlobalFilter(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 跳过不需要验证的路径
        if (shouldIgnore(path)) {
            return chain.filter(exchange);
        }

        // 获取token
        String token = getToken(request);
        if (!StringUtils.hasText(token)) {
            // 如果没有token但是是公开的API，则放行
            if (isPublicApi(path)) {
                return chain.filter(exchange);
            }
            log.debug("Token is empty for path: {}", path);
            return chain.filter(exchange);
        }

        try {
            // 解析token
            Claims claims = parseToken(token);
            if (claims != null) {
                // 将用户信息传递到下游服务
                ServerHttpRequest newRequest = request.mutate()
                        .header(CommonConstants.USER_ID, String.valueOf(claims.get("userId")))
                        .header(CommonConstants.USERNAME, String.valueOf(claims.get("username")))
                        .build();

                log.debug("Token validated, forwarding userId: {} for path: {}", claims.get("userId"), path);
                return chain.filter(exchange.mutate().request(newRequest).build());
            }
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
        }

        // Token无效但如果是公开的API，依然放行
        if (isPublicApi(path)) {
            return chain.filter(exchange);
        }

        return chain.filter(exchange);
    }

    /**
     * 获取请求中的token
     */
    private String getToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(CommonConstants.AUTHORIZATION_HEADER);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(CommonConstants.TOKEN_PREFIX)) {
            return authHeader.substring(CommonConstants.TOKEN_PREFIX.length()).trim();
        }
        return null;
    }

    /**
     * 解析JWT令牌
     */
    private Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Failed to parse token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 是否跳过验证
     */
    private boolean shouldIgnore(String path) {
        List<String> ignoreUrls = jwtProperties.getIgnoreUrls();
        return ignoreUrls.stream().anyMatch(url -> {
            if (url.endsWith("/**")) {
                String basePattern = url.substring(0, url.length() - 3);
                return path.startsWith(basePattern);
            }
            return path.equals(url);
        });
    }

    /**
     * 是否是公开的API
     */
    private boolean isPublicApi(String path) {
        // 所有 /user 下的API都是公开的
        return path.startsWith("/user");
    }

    @Override
    public int getOrder() {
        return -100; // 确保在其他过滤器之前执行
    }
} 