package com.kenanai.shop.checker;

import com.kenanai.shop.dto.CartAddContext;

/**
 * 加入购物车责任链校验器接口
 */
public interface CartAddChecker {
    /**
     * 校验方法，抛出异常表示校验不通过
     */
    void check(CartAddContext context);
} 