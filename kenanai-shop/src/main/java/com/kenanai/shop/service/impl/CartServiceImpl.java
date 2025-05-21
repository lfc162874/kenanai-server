package com.kenanai.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kenanai.shop.entity.CartItem;
import com.kenanai.shop.entity.Product;
import com.kenanai.shop.mapper.CartItemMapper;
import com.kenanai.shop.service.CartService;
import com.kenanai.shop.service.ProductService;
import com.kenanai.shop.dto.CartAddContext;
import com.kenanai.shop.checker.CartUserStatusChecker;
import com.kenanai.shop.checker.CartProductStatusChecker;
import com.kenanai.shop.checker.CartProductStockChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 购物车服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartItemMapper, CartItem> implements CartService {

    private final ProductService productService;
    @Autowired
    private  CartUserStatusChecker cartUserStatusChecker;
    @Autowired
    private  CartProductStatusChecker cartProductStatusChecker;
    @Autowired
    private  CartProductStockChecker cartProductStockChecker;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addToCart(Long userId, Long productId, Integer quantity, String specifications) {
        // 责任链校验
        CartAddContext context = new CartAddContext(userId, productId, quantity, specifications);
        cartUserStatusChecker.linkWith(cartProductStatusChecker).linkWith(cartProductStockChecker);
        cartUserStatusChecker.check(context);

        // 判断商品是否存在
        Product product = productService.getById(productId);
        // 检查购物车中是否已有该商品
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getUserId, userId)
                .eq(CartItem::getProductId, productId);
        CartItem existCartItem = getOne(queryWrapper);
        
        if (existCartItem != null) {
            // 更新数量
            existCartItem.setQuantity(existCartItem.getQuantity() + quantity);
            updateById(existCartItem);
        } else {
            // 创建新购物车项
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setProductName(product.getName());
            cartItem.setProductImage(product.getImageUrl());
            cartItem.setPrice(product.getPrice());
            cartItem.setQuantity(quantity);
            cartItem.setSpecifications(specifications);
            cartItem.setChecked(1);
            save(cartItem);
        }
        
        return true;
    }

    @Override
    public boolean updateQuantity(Long userId, Long cartItemId, Integer quantity) {
        CartItem cartItem = getById(cartItemId);
        if (cartItem == null || !cartItem.getUserId().equals(userId)) {
            return false;
        }
        
        // 判断库存是否足够
        Product product = productService.getById(cartItem.getProductId());
        if (product == null || product.getStock() < quantity) {
            return false;
        }
        
        cartItem.setQuantity(quantity);
        return updateById(cartItem);
    }

    @Override
    public boolean deleteCartItems(Long userId, List<Long> cartItemIds) {
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getUserId, userId)
                .in(CartItem::getId, cartItemIds);
        
        return remove(queryWrapper);
    }

    @Override
    public boolean clearCart(Long userId) {
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getUserId, userId);
        
        return remove(queryWrapper);
    }

    @Override
    public List<CartItem> getCartItems(Long userId) {
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getUserId, userId)
                .orderByDesc(CartItem::getCreateTime);
        
        return list(queryWrapper);
    }

    @Override
    public boolean updateChecked(Long userId, Long cartItemId, Integer checked) {
        LambdaUpdateWrapper<CartItem> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CartItem::getId, cartItemId)
                .eq(CartItem::getUserId, userId)
                .set(CartItem::getChecked, checked);
        
        return update(updateWrapper);
    }

    @Override
    public Map<String, Object> getCartStats(Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取购物车列表
        List<CartItem> cartItems = getCartItems(userId);
        
        // 计算选中商品的总价和数量
        List<CartItem> checkedItems = cartItems.stream()
                .filter(item -> item.getChecked() == 1)
                .collect(Collectors.toList());
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalQuantity = 0;
        
        for (CartItem item : checkedItems) {
            totalAmount = totalAmount.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
            totalQuantity += item.getQuantity();
        }
        
        result.put("totalAmount", totalAmount);
        result.put("totalQuantity", totalQuantity);
        result.put("checkedCount", checkedItems.size());
        result.put("totalCount", cartItems.size());
        
        return result;
    }
} 