package com.kenanai.shop.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kenanai.shop.entity.CartItem;

import java.util.List;
import java.util.Map;

/**
 * 购物车服务接口
 */
public interface CartService extends IService<CartItem> {

    /**
     * 添加商品到购物车
     *
     * @param userId        用户ID
     * @param productId     商品ID
     * @param quantity      数量
     * @param specifications 规格信息
     * @return 是否成功
     */
    boolean addToCart(Long userId, Long productId, Integer quantity, String specifications);

    /**
     * 更新购物车商品数量
     *
     * @param userId    用户ID
     * @param cartItemId 购物车项ID
     * @param quantity  新数量
     * @return 是否成功
     */
    boolean updateQuantity(Long userId, Long cartItemId, Integer quantity);

    /**
     * 删除购物车商品
     *
     * @param userId    用户ID
     * @param cartItemIds 购物车项ID列表
     * @return 是否成功
     */
    boolean deleteCartItems(Long userId, List<Long> cartItemIds);

    /**
     * 清空购物车
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean clearCart(Long userId);

    /**
     * 获取购物车列表
     *
     * @param userId 用户ID
     * @return 购物车商品列表
     */
    List<CartItem> getCartItems(Long userId);

    /**
     * 选中/取消选中购物车商品
     *
     * @param userId    用户ID
     * @param cartItemId 购物车项ID
     * @param checked   是否选中（1:选中，0:取消选中）
     * @return 是否成功
     */
    boolean updateChecked(Long userId, Long cartItemId, Integer checked);

    /**
     * 获取购物车统计信息
     *
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Object> getCartStats(Long userId);
    
    /**
     * 根据条件查询购物车项列表
     *
     * @param queryWrapper 查询条件
     * @return 购物车项列表
     */
//    List<CartItem> list(Wrapper<CartItem> queryWrapper);
}