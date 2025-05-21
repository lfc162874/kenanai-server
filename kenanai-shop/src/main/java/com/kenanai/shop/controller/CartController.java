package com.kenanai.shop.controller;

import com.kenanai.common.constant.CommonConstants;
import com.kenanai.common.entity.R;
import com.kenanai.shop.entity.CartItem;
import com.kenanai.shop.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 购物车控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/shop/cart")
public class CartController {

    private final CartService cartService;

    /**
     * 添加商品到购物车
     */
    @PostMapping("/add")
    public R<Boolean> addToCart(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            String userIdStr = request.getHeader(CommonConstants.USER_ID);
            if (userIdStr == null) {
                return R.failed("请先登录");
            }
            
            Long userId = Long.parseLong(userIdStr);
            Long productId = Long.parseLong(params.get("productId").toString());
            Integer quantity = Integer.parseInt(params.get("quantity").toString());
            String specifications = (String) params.get("specifications");
            
            boolean success = cartService.addToCart(userId, productId, quantity, specifications);
            return success ? R.ok(true, "添加购物车成功") : R.failed("添加购物车失败");
        } catch (Exception e) {
            log.error("添加购物车失败: {}", e.getMessage(), e);
            return R.failed(e.getMessage());
        }
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/quantity")
    public R<Boolean> updateQuantity(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            String userIdStr = request.getHeader(CommonConstants.USER_ID);
            if (userIdStr == null) {
                return R.failed("请先登录");
            }
            
            Long userId = Long.parseLong(userIdStr);
            Long cartItemId = Long.parseLong(params.get("cartItemId").toString());
            Integer quantity = Integer.parseInt(params.get("quantity").toString());
            
            boolean success = cartService.updateQuantity(userId, cartItemId, quantity);
            return success ? R.ok(true, "更新数量成功") : R.failed("更新数量失败");
        } catch (Exception e) {
            log.error("更新购物车数量失败: {}", e.getMessage(), e);
            return R.failed("更新购物车数量失败");
        }
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping
    public R<Boolean> deleteCartItems(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            String userIdStr = request.getHeader(CommonConstants.USER_ID);
            if (userIdStr == null) {
                return R.failed("请先登录");
            }
            
            Long userId = Long.parseLong(userIdStr);
            List<Long> cartItemIds = (List<Long>) params.get("cartItemIds");
            
            boolean success = cartService.deleteCartItems(userId, cartItemIds);
            return success ? R.ok(true, "删除购物车商品成功") : R.failed("删除购物车商品失败");
        } catch (Exception e) {
            log.error("删除购物车商品失败: {}", e.getMessage(), e);
            return R.failed("删除购物车商品失败");
        }
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public R<Boolean> clearCart(HttpServletRequest request) {
        try {
            String userIdStr = request.getHeader(CommonConstants.USER_ID);
            if (userIdStr == null) {
                return R.failed("请先登录");
            }
            
            Long userId = Long.parseLong(userIdStr);
            boolean success = cartService.clearCart(userId);
            return success ? R.ok(true, "清空购物车成功") : R.failed("清空购物车失败");
        } catch (Exception e) {
            log.error("清空购物车失败: {}", e.getMessage(), e);
            return R.failed("清空购物车失败");
        }
    }

    /**
     * 获取购物车列表
     */
    @GetMapping("/list")
    public R<List<CartItem>> getCartItems(HttpServletRequest request) {
        try {
            String userIdStr = request.getHeader(CommonConstants.USER_ID);
            if (userIdStr == null) {
                return R.failed("请先登录");
            }
            
            Long userId = Long.parseLong(userIdStr);
            List<CartItem> cartItems = cartService.getCartItems(userId);
            return R.ok(cartItems);
        } catch (Exception e) {
            log.error("获取购物车列表失败: {}", e.getMessage(), e);
            return R.failed("获取购物车列表失败");
        }
    }

    /**
     * 选中/取消选中购物车商品
     */
    @PutMapping("/checked")
    public R<Boolean> updateChecked(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            String userIdStr = request.getHeader(CommonConstants.USER_ID);
            if (userIdStr == null) {
                return R.failed("请先登录");
            }
            
            Long userId = Long.parseLong(userIdStr);
            Long cartItemId = Long.parseLong(params.get("cartItemId").toString());
            Integer checked = Integer.parseInt(params.get("checked").toString());
            
            boolean success = cartService.updateChecked(userId, cartItemId, checked);
            return success ? R.ok(true, "更新选中状态成功") : R.failed("更新选中状态失败");
        } catch (Exception e) {
            log.error("更新购物车选中状态失败: {}", e.getMessage(), e);
            return R.failed("更新购物车选中状态失败");
        }
    }

    /**
     * 获取购物车统计信息
     */
    @GetMapping("/stats")
    public R<Map<String, Object>> getCartStats(HttpServletRequest request) {
        try {
            String userIdStr = request.getHeader(CommonConstants.USER_ID);
            if (userIdStr == null) {
                return R.failed("请先登录");
            }
            
            Long userId = Long.parseLong(userIdStr);
            Map<String, Object> stats = cartService.getCartStats(userId);
            return R.ok(stats);
        } catch (Exception e) {
            log.error("获取购物车统计信息失败: {}", e.getMessage(), e);
            return R.failed("获取购物车统计信息失败");
        }
    }
} 