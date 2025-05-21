package com.kenanai.shop.checker;

import com.kenanai.shop.dto.OrderCreateContext;
import org.springframework.stereotype.Component;

/**
 * 订单用户状态校验器。
 * 校验用户是否登录、是否黑名单等。
 */
@Component
public class UserStatusChecker extends AbstractOrderCreateChecker {
    @Override
    protected void doCheck(OrderCreateContext context) {
        if (context.getUserId() == null || context.getUserId() <= 0) {
            throw new RuntimeException("用户未登录或用户ID非法");
        }
        // 可扩展更多用户状态校验
    }
} 