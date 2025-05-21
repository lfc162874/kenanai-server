package com.kenanai.shop.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kenanai.shop.entity.*;
import com.kenanai.shop.mapper.OrderItemMapper;
import com.kenanai.shop.mapper.OrderMapper;
import com.kenanai.shop.service.AddressService;
import com.kenanai.shop.service.CartService;
import com.kenanai.shop.service.OrderService;
import com.kenanai.shop.service.ProductService;
import com.kenanai.shop.dto.OrderCreateContext;
import com.kenanai.shop.checker.OrderCreateChecker;
import com.kenanai.shop.checker.UserStatusChecker;
import com.kenanai.shop.checker.ProductStockChecker;
import com.kenanai.shop.checker.ProductSaleStatusChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final CartService cartService;
    private final ProductService productService;
    private final AddressService addressService;
    private final UserStatusChecker userStatusChecker;
    private final ProductStockChecker productStockChecker;
    private final ProductSaleStatusChecker productSaleStatusChecker;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(Long userId, List<Long> cartItemIds, Long addressId, String note) {
        // 责任链校验
        OrderCreateContext context = new OrderCreateContext(userId, cartItemIds, addressId, note);
        userStatusChecker.linkWith(productStockChecker).linkWith(productSaleStatusChecker);
        userStatusChecker.check(context);

        // 1. 获取购物车商品
        LambdaQueryWrapper<CartItem> cartItemQueryWrapper = new LambdaQueryWrapper<>();
        cartItemQueryWrapper.eq(CartItem::getUserId, userId)
                .in(CartItem::getId, cartItemIds);
        List<CartItem> cartItems = cartService.list(cartItemQueryWrapper);
        
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("购物车为空");
        }
        
        // 2. 获取收货地址
        Address address = addressService.getById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new IllegalArgumentException("收货地址不存在");
        }
        
        // 3. 生成订单号
        String orderNo = generateOrderNo();
        
        // 4. 计算订单总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            totalAmount = totalAmount.add(cartItem.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
        }
        
        // 5. 计算运费（这里简单处理，实际可能需要更复杂的运费计算逻辑）
        BigDecimal freightAmount = new BigDecimal("10.00");
        
        // 6. 计算支付金额
        BigDecimal payAmount = totalAmount.add(freightAmount);
        
        // 7. 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(payAmount);
        order.setFreightAmount(freightAmount);
        order.setStatus(0); // 待付款
        order.setSourceType(1); // 假设来源为PC
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress());
        order.setNote(note);
        
        // 保存订单
        save(order);
        
        // 8. 创建订单商品
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setOrderNo(orderNo);
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setProductImage(cartItem.getProductImage());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setProductPrice(cartItem.getPrice());
            orderItem.setTotalPrice(cartItem.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
            
            orderItems.add(orderItem);
            
            // 9. 扣减库存
            productService.updateStock(cartItem.getProductId(), -cartItem.getQuantity());
        }
        
        // 批量保存订单商品
        orderItemMapper.insertBatchSomeColumn(orderItems);
        
        // 10. 删除购物车中对应的商品
        cartService.deleteCartItems(userId, cartItemIds);
        
        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payOrder(Long orderId, Long userId, Integer payType) {
        // 1. 查询订单
        Order order = getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        
        // 2. 检查订单状态，只有待付款的订单才能支付
        if (order.getStatus() != 0) {
            return false;
        }
        
        // 3. 更新订单状态
        order.setStatus(1); // 待发货
        order.setPayType(payType);
        order.setPaymentTime(LocalDateTime.now());
        boolean b = updateById(order);
        if (b){
            // 4. 付成功回调后，更新 ZSet 热度分，如果没有就设置为0，入果有就+1每次都+1.
            OrderItem orderItem = orderItemMapper.selectOne(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderNo, order.getOrderNo()));
            redisTemplate.opsForZSet().incrementScore("hot_products", orderItem.getProductId(), 1);
        }

        return b;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long orderId, Long userId) {
        // 1. 查询订单
        Order order = getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        
        // 2. 检查订单状态，只有待付款和待发货的订单可以取消
        if (order.getStatus() != 0 && order.getStatus() != 1) {
            return false;
        }
        
        // 3. 如果是已付款订单，需要退款（这里只是状态变更，实际应该有退款逻辑）
        
        // 4. 恢复库存
        if (order.getStatus() != 0) { // 如果不是待付款状态，说明已扣减库存，需要恢复
            // 查询订单商品
            LambdaQueryWrapper<OrderItem> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OrderItem::getOrderId, orderId);
            List<OrderItem> orderItems = orderItemMapper.selectList(queryWrapper);
            
            // 恢复库存
            for (OrderItem orderItem : orderItems) {
                productService.updateStock(orderItem.getProductId(), orderItem.getQuantity());
            }
        }
        
        // 5. 更新订单状态
        order.setStatus(5); // 已取消
        
        return updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean shipOrder(Long orderId, String deliveryCompany, String deliveryNo) {
        // 1. 查询订单
        Order order = getById(orderId);
        if (order == null) {
            return false;
        }
        
        // 2. 检查订单状态，只有待发货的订单可以发货
        if (order.getStatus() != 1) {
            return false;
        }
        
        // 3. 更新订单状态
        order.setStatus(2); // 已发货
        order.setDeliveryTime(LocalDateTime.now());
        // 这里可以添加物流信息字段来保存物流公司和单号
        
        return updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmReceive(Long orderId, Long userId) {
        // 1. 查询订单
        Order order = getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        
        // 2. 检查订单状态，只有已发货的订单可以确认收货
        if (order.getStatus() != 2) {
            return false;
        }
        
        // 3. 更新订单状态
        order.setStatus(3); // 已完成
        order.setReceiveTime(LocalDateTime.now());
        
        return updateById(order);
    }

    @Override
    public Map<String, Object> getOrderDetail(Long orderId, Long userId) {
        // 1. 查询订单
        Order order = getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("订单不存在");
        }
        
        // 2. 查询订单商品
        LambdaQueryWrapper<OrderItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> orderItems = orderItemMapper.selectList(queryWrapper);
        
        // 3. 组装结果
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("orderItems", orderItems);
        
        return result;
    }

    @Override
    public IPage<Order> pageUserOrders(Long userId, Integer status, int page, int size) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId);
        
        // 如果状态不为0，则按状态筛选
        if (status != null && status > 0) {
            queryWrapper.eq(Order::getStatus, status);
        }
        
        queryWrapper.orderByDesc(Order::getCreateTime);
        
        return page(new Page<>(page, size), queryWrapper);
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return IdUtil.getSnowflakeNextIdStr();
    }
} 