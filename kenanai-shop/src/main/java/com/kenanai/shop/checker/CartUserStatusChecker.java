package com.kenanai.shop.checker;

import com.kenanai.shop.dto.CartAddContext;
import org.springframework.stereotype.Component;

/**
 * 加入购物车用户状态校验器。
 * 校验用户是否登录、是否黑名单等。
 */
@Component
public class CartUserStatusChecker extends AbstractCartAddChecker {
    @Override
    protected void doCheck(CartAddContext context) {
        if (context.getUserId() == null || context.getUserId() <= 0) {
            throw new RuntimeException("用户未登录或用户ID非法");
        }
        // 可扩展更多用户状态校验
    }
} 