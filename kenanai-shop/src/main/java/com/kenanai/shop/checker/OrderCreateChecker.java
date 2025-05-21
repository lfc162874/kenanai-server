package com.kenanai.shop.checker;

import com.kenanai.shop.dto.OrderCreateContext;

/**
 * 订单创建责任链接口。
 * 实现类需实现 check 方法，抛出异常即中断责任链。
 */
public interface OrderCreateChecker {
    /**
     * 校验方法，抛出异常表示校验不通过
     */
    void check(OrderCreateContext context);
} 