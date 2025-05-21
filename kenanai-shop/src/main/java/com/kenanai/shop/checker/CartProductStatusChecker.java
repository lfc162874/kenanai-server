package com.kenanai.shop.checker;

import com.kenanai.shop.dto.CartAddContext;
import com.kenanai.shop.entity.Product;
import com.kenanai.shop.service.ProductService;
import org.springframework.stereotype.Component;

/**
 * 加入购物车商品状态校验器。
 * 校验商品是否存在、是否上架。
 */
@Component
public class CartProductStatusChecker extends AbstractCartAddChecker {
    private final ProductService productService;

    public CartProductStatusChecker(ProductService productService) {
        this.productService = productService;
    }

    @Override
    protected void doCheck(CartAddContext context) {
        Product product = productService.getById(context.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (product.getStatus() == null || product.getStatus() != 1) {
            throw new RuntimeException("商品未上架");
        }
    }
} 