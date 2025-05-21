package com.kenanai.shop.checker;

import com.kenanai.shop.dto.OrderCreateContext;
import com.kenanai.shop.entity.CartItem;
import com.kenanai.shop.service.CartService;
import com.kenanai.shop.service.ProductService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 订单商品库存校验器。
 * 校验购物车中所有商品库存是否充足。
 */
@Component
public class ProductStockChecker extends AbstractOrderCreateChecker {
    private final CartService cartService;
    private final ProductService productService;

    public ProductStockChecker(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    @Override
    protected void doCheck(OrderCreateContext context) {
        List<CartItem> cartItems = cartService.listByIds(context.getCartItemIds());
        for (CartItem cartItem : cartItems) {
            Integer stock = productService.getStock(cartItem.getProductId());
            if (stock == null || stock < cartItem.getQuantity()) {
                throw new RuntimeException("商品库存不足: " + cartItem.getProductId());
            }
        }
    }
} 