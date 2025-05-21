package com.kenanai.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kenanai.common.constant.CommonConstants;
import com.kenanai.common.entity.R;
import com.kenanai.shop.entity.Order;
import com.kenanai.shop.mapper.OrderMapper;
import com.kenanai.shop.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 支付控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/shop/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderMapper orderMapper;

    /**
     * 获取支付信息
     */
    @GetMapping("/info/{orderId}")
    public R<Map<String, String>> getPayInfo(@PathVariable Long orderId, @RequestParam Integer payType, HttpServletRequest request) {
        try {
            String userIdStr = request.getHeader(CommonConstants.USER_ID);
            if (userIdStr == null) {
                return R.failed("请先登录");
            }
            
            Long userId = Long.parseLong(userIdStr);
            
            // 查询订单
            LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Order::getId, orderId)
                    .eq(Order::getUserId, userId);
            Order order = orderMapper.selectOne(queryWrapper);
            
            if (order == null) {
                return R.failed("订单不存在");
            }
            
            if (order.getStatus() != 0) {
                return R.failed("订单状态不支持支付");
            }
            
            // 生成支付信息
            Map<String, String> payInfo = paymentService.generatePayInfo(
                    order.getOrderNo(),
                    order.getPayAmount().toString(),
                    payType
            );
            
            return R.ok(payInfo);
        } catch (Exception e) {
            log.error("获取支付信息失败: {}", e.getMessage(), e);
            return R.failed(e.getMessage());
        }
    }

    /**
     * 查询支付状态
     */
    @GetMapping("/status")
    public R<String> queryPayStatus(@RequestParam String orderNo, @RequestParam Integer payType) {
        try {
            String status = paymentService.queryPayStatus(orderNo, payType);
            return R.ok(status);
        } catch (Exception e) {
            log.error("查询支付状态失败: {}", e.getMessage(), e);
            return R.failed("查询支付状态失败");
        }
    }

    /**
     * 支付回调
     */
    @PostMapping("/callback/{payType}")
    public String handlePayCallback(@PathVariable Integer payType, @RequestBody Map<String, String> params) {
        try {
            boolean success = paymentService.handlePayCallback(params, payType);
            return success ? "success" : "fail";
        } catch (Exception e) {
            log.error("处理支付回调失败: {}", e.getMessage(), e);
            return "fail";
        }
    }

    /**
     * 申请退款
     */
    @PostMapping("/refund")
    public R<Boolean> refund(@RequestBody Map<String, String> params, HttpServletRequest request) {
        try {
            String userIdStr = request.getHeader(CommonConstants.USER_ID);
            if (userIdStr == null) {
                return R.failed("请先登录");
            }
            
            String orderNo = params.get("orderNo");
            String refundAmount = params.get("refundAmount");
            String refundReason = params.get("refundReason");
            Integer payType = Integer.valueOf(params.get("payType"));
            
            // 验证订单所属
            LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Order::getOrderNo, orderNo)
                    .eq(Order::getUserId, Long.parseLong(userIdStr));
            if (orderMapper.selectCount(queryWrapper) == 0) {
                return R.failed("订单不存在");
            }
            
            boolean success = paymentService.refund(orderNo, refundAmount, refundReason, payType);
            return success ? R.ok(true, "退款申请成功") : R.failed("退款申请失败");
        } catch (Exception e) {
            log.error("申请退款失败: {}", e.getMessage(), e);
            return R.failed("申请退款失败");
        }
    }
} 