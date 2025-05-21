package com.kenanai.shop.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kenanai.common.constant.CommonConstants;
import com.kenanai.common.entity.R;
import com.kenanai.shop.entity.Order;
import com.kenanai.shop.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/shop/order")
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public R<Long> createOrder(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            String userIdStr = request.getHeader(CommonConstants.USER_ID);
            if (userIdStr == null) {
                return R.failed("请先登录");
            }

            Long userId = Long.parseLong(userIdStr);
            List<Long> cartItemIds = (List<Long>) params.get("cartItemIds");
            Long addressId = Long.parseLong(params.get("addressId").toString());
            String note = (String) params.get("note");

            Long orderId = orderService.createOrder(userId, cartItemIds, addressId, note);
            return R.ok(orderId, "创建订单成功");
        } catch (Exception e) {
            log.error("创建订单失败: {}", e.getMessage(), e);
            return R.failed(e.getMessage());
        }
    }

    /**
     * 支付订单
     */
    @PostMapping("/{orderId}/pay")
    public R<Boolean> payOrder(@PathVariable Long orderId, @RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            String userIdStr = request.getHeader(CommonConstants.USER_ID);
            if (userIdStr == null) {
                return R.failed("请先登录");
            }

            Long userId = Long.parseLong(userIdStr);
            Integer payType = Integer.valueOf(params.get("payType").toString());

            boolean success = orderService.payOrder(orderId, userId, payType);
            return success ? R.ok(true, "支付成功") : R.failed("支付失败");
        } catch (Exception e) {
            log.error("支付订单失败: {}", e.getMessage(), e);
            return R.failed("支付订单失败");
        }
    }

    /**
     * 取消订单
     */
    @PostMapping("/{orderId}/cancel")
    public R<Boolean> cancelOrder(@PathVariable Long orderId, HttpServletRequest request) {
        try {
            String userIdStr = request.getHeader(CommonConstants.USER_ID);
            if (userIdStr == null) {
                return R.failed("请先登录");
            }

            Long userId = Long.parseLong(userIdStr);
            boolean success = orderService.cancelOrder(orderId, userId);
            return success ? R.ok(true, "取消订单成功") : R.failed("取消订单失败");
        } catch (Exception e) {
            log.error("取消订单失败: {}", e.getMessage(), e);
            return R.failed("取消订单失败");
        }
    }

    /**
     * 确认收货
     */
    @PostMapping("/{orderId}/confirm")
    public R<Boolean> confirmReceive(@PathVariable Long orderId, HttpServletRequest request) {
        try {
            String userIdStr = request.getHeader(CommonConstants.USER_ID);
            if (userIdStr == null) {
                return R.failed("请先登录");
            }

            Long userId = Long.parseLong(userIdStr);
            boolean success = orderService.confirmReceive(orderId, userId);
            return success ? R.ok(true, "确认收货成功") : R.failed("确认收货失败");
        } catch (Exception e) {
            log.error("确认收货失败: {}", e.getMessage(), e);
            return R.failed("确认收货失败");
        }
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public R<Map<String, Object>> getOrderDetail(@PathVariable Long orderId, HttpServletRequest request) {
        try {
            String userIdStr = request.getHeader(CommonConstants.USER_ID);
            if (userIdStr == null) {
                return R.failed("请先登录");
            }

            Long userId = Long.parseLong(userIdStr);
            Map<String, Object> orderDetail = orderService.getOrderDetail(orderId, userId);
            return R.ok(orderDetail);
        } catch (Exception e) {
            log.error("获取订单详情失败: {}", e.getMessage(), e);
            return R.failed(e.getMessage());
        }
    }

    /**
     * 获取用户订单列表
     */
    @GetMapping("/list")
    public R<IPage<Order>> getUserOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        try {
            String userIdStr = request.getHeader(CommonConstants.USER_ID);
            if (userIdStr == null) {
                return R.failed("请先登录");
            }

            Long userId = Long.parseLong(userIdStr);
            IPage<Order> orders = orderService.pageUserOrders(userId, status, page, size);
            return R.ok(orders);
        } catch (Exception e) {
            log.error("获取订单列表失败: {}", e.getMessage(), e);
            return R.failed("获取订单列表失败");
        }
    }

    /**
     * 后台发货（仅管理员）
     */
    @PostMapping("/{orderId}/ship")
    public R<Boolean> shipOrder(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> params) {
        try {
            // TODO: 权限校验，仅管理员可操作

            String deliveryCompany = params.get("deliveryCompany");
            String deliveryNo = params.get("deliveryNo");

            boolean success = orderService.shipOrder(orderId, deliveryCompany, deliveryNo);
            return success ? R.ok(true, "发货成功") : R.failed("发货失败");
        } catch (Exception e) {
            log.error("发货失败: {}", e.getMessage(), e);
            return R.failed("发货失败");
        }
    }

    /**
     * 生成订单相关token
     */
    @PostMapping("/token/{orderId}")
    public R<String> generateToken(@PathVariable Long orderId) {
        try {
            if (orderId == null) {
                return R.failed("缺少必要参数: orderId");
            }

            String orderIdStr = orderId.toString();
            String uuid = java.util.UUID.randomUUID().toString().replaceAll("-", "");

            // 拼接生成token
            String token = orderIdStr + uuid;

            return R.ok(token, "token生成成功");
        } catch (Exception e) {
            log.error("生成token失败: {}", e.getMessage(), e);
            return R.failed("生成token失败");
        }
    }


} 