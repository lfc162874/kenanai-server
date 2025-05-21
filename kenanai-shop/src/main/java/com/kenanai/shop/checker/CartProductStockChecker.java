package com.kenanai.shop.checker;

import com.kenanai.shop.dto.CartAddContext;
import com.kenanai.shop.service.ProductService;
import org.springframework.stereotype.Component;

/**
 * 加入购物车商品库存校验器。
 * 校验商品库存是否充足。
 */
@Component
public class CartProductStockChecker extends AbstractCartAddChecker {
    private final ProductService productService;

    public CartProductStockChecker(ProductService productService) {
        this.productService = productService;
    }

    @Override
    protected void doCheck(CartAddContext context) {
        Integer stock = productService.getStock(context.getProductId());
        if (stock == null || stock < context.getQuantity()) {
            throw new RuntimeException("商品库存不足");
        }
    }
} 