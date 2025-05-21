package com.kenanai.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtUtil {

    /**
     * JWT密钥
     */
    private String secret;

    /**
     * JWT过期时间（秒）
     */
    private Long expire;

    /**
     * JWT头部
     */
    private String header;

    /**
     * 生成JWT令牌
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return JWT令牌
     */
    public String generateToken(String userId, String username) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expire * 1000);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        Key key = Keys.hmacShaKeyFor(secret.getBytes());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析JWT令牌
     *
     * @param token JWT令牌
     * @return 解析结果
     */
    public Claims parseToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(secret.getBytes());
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证JWT令牌
     *
     * @param token JWT令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        return parseToken(token) != null;
    }

    /**
     * 从JWT令牌中获取用户ID
     *
     * @param token JWT令牌
     * @return 用户ID
     */
    public String getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.get("userId", String.class) : null;
    }

    /**
     * 从JWT令牌中获取用户名
     *
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.get("username", String.class) : null;
    }

    /**
     * 获取过期时间（秒）
     *
     * @return 过期时间
     */
    public long getExpireTime() {
        return expire;
    }
} 