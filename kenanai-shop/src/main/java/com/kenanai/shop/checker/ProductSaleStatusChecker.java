package com.kenanai.shop.checker;

import com.kenanai.shop.dto.OrderCreateContext;
import com.kenanai.shop.entity.CartItem;
import com.kenanai.shop.service.CartService;
import com.kenanai.shop.service.ProductService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 订单商品可售状态校验器。
 * 校验购物车中所有商品是否可售。
 */
@Component
public class ProductSaleStatusChecker extends AbstractOrderCreateChecker {
    private final CartService cartService;
    private final ProductService productService;

    public ProductSaleStatusChecker(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    @Override
    protected void doCheck(OrderCreateContext context) {
        List<CartItem> cartItems = cartService.listByIds(context.getCartItemIds());
        for (CartItem cartItem : cartItems) {
            Boolean saleStatus = productService.isProductOnSale(cartItem.getProductId());
            if (saleStatus == null || !saleStatus) {
                throw new RuntimeException("商品不可售: " + cartItem.getProductId());
            }
        }
    }
} 