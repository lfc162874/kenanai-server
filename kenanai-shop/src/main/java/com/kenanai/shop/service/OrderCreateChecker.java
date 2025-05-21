package com.kenanai.shop.service;

import com.kenanai.shop.dto.OrderCreateContext;

public interface OrderCreateChecker {
    void check(OrderCreateContext context);
} 