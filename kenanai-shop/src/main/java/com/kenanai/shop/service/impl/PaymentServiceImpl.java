package com.kenanai.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kenanai.shop.entity.Order;
import com.kenanai.shop.mapper.OrderMapper;
import com.kenanai.shop.service.OrderService;
import com.kenanai.shop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 支付服务实现类（模拟支付功能）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderMapper orderMapper;
    private final OrderService orderService;

    @Override
    public Map<String, String> generatePayInfo(String orderNo, String amount, Integer payType) {
        // 模拟生成支付信息
        Map<String, String> result = new HashMap<>();
        
        // 根据支付方式生成不同的支付参数
        switch (payType) {
            case 1: // 支付宝
                result.put("payUrl", "https://mock.alipay.com/gateway.do?orderId=" + orderNo);
                result.put("qrCode", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAQAAAAE..."); // 模拟二维码数据
                break;
            case 2: // 微信
                result.put("payUrl", "weixin://wxpay/bizpayurl?pr=" + orderNo);
                result.put("qrCode", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAQAAAAE..."); // 模拟二维码数据
                break;
            case 3: // 银联
                result.put("payUrl", "https://mock.unionpay.com/gateway?orderId=" + orderNo);
                result.put("formData", "<form action='https://mock.unionpay.com/gateway' method='post'><input type='hidden' name='orderId' value='" + orderNo + "'/></form>");
                break;

            case 4: // 余额支付
                result.put("balance", "100.00");
                result.put("payType", "4");
            default:
                throw new IllegalArgumentException("不支持的支付方式");
        }
        
        result.put("orderNo", orderNo);
        result.put("amount", amount);
        result.put("channel", String.valueOf(payType));
        
        log.info("生成支付信息: {}", result);
        return result;
    }

    @Override
    public String queryPayStatus(String orderNo, Integer payType) {
        // 查询数据库中的订单状态
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getOrderNo, orderNo);
        Order order = orderMapper.selectOne(queryWrapper);
        
        if (order == null) {
            return "failed";
        }
        
        // 根据订单状态返回支付状态
        if (order.getStatus() >= 1) { // 已支付
            return "success";
        } else if (order.getStatus() == 0) { // 待支付
            return "waiting";
        } else { // 已取消等其他状态
            return "failed";
        }
    }

    @Override
    public boolean handlePayCallback(Map<String, String> params, Integer payType) {
        // 模拟支付回调处理
        try {
            String orderNo = params.get("orderNo");
            String tradeStatus = params.get("tradeStatus");
            
            // 验证参数
            if (orderNo == null || tradeStatus == null) {
                return false;
            }
            
            // 查询订单
            LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Order::getOrderNo, orderNo);
            Order order = orderMapper.selectOne(queryWrapper);
            
            if (order == null) {
                return false;
            }
            
            // 判断支付状态
            if ("SUCCESS".equals(tradeStatus)) {
                // 支付成功，更新订单状态
                if (order.getStatus() == 0) { // 确保订单状态是待支付
                    order.setStatus(1); // 更新为待发货
                    order.setPayType(payType);
                    order.setPaymentTime(LocalDateTime.now());
                    orderMapper.updateById(order);
                }
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("处理支付回调失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean refund(String orderNo, String refundAmount, String refundReason, Integer payType) {
        // 模拟退款
        try {
            // 查询订单
            LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Order::getOrderNo, orderNo);
            Order order = orderMapper.selectOne(queryWrapper);
            
            if (order == null) {
                return false;
            }
            
            // 判断订单状态，只有已支付的订单可以退款
            if (order.getStatus() < 1 || order.getStatus() > 3) {
                return false;
            }
            
            // 模拟80%概率退款成功
            boolean success = new Random().nextInt(10) < 8;
            if (success) {
                // 如果是已发货但未确认收货的订单，需要执行取消订单逻辑
                if (order.getStatus() <= 2) {
                    orderService.cancelOrder(order.getId(), order.getUserId());
                }
                
                // 更新订单状态
                order.setStatus(4); // 已退款
                orderMapper.updateById(order);
            }
            
            return success;
        } catch (Exception e) {
            log.error("申请退款失败: {}", e.getMessage(), e);
            return false;
        }
    }
} 