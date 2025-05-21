package com.kenanai.shop.dto;

import java.util.List;

public class OrderCreateContext {
    private Long userId;
    private List<Long> cartItemIds;
    private Long addressId;
    private String note;

    // 可扩展更多下单相关参数

    public OrderCreateContext(Long userId, List<Long> cartItemIds, Long addressId, String note) {
        this.userId = userId;
        this.cartItemIds = cartItemIds;
        this.addressId = addressId;
        this.note = note;
    }

    public Long getUserId() {
        return userId;
    }

    public List<Long> getCartItemIds() {
        return cartItemIds;
    }

    public Long getAddressId() {
        return addressId;
    }

    public String getNote() {
        return note;
    }
} 