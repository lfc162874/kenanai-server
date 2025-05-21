package com.kenanai.shop.dto;

public class CartAddContext {
    private Long userId;
    private Long productId;
    private Integer quantity;
    private String specifications;

    public CartAddContext(Long userId, Long productId, Integer quantity, String specifications) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.specifications = specifications;
    }

    public Long getUserId() { return userId; }
    public Long getProductId() { return productId; }
    public Integer getQuantity() { return quantity; }
    public String getSpecifications() { return specifications; }
} 