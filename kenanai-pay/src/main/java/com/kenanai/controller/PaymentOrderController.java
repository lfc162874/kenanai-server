package com.kenanai.controller;

import com.alibaba.nacos.api.utils.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kenanai.common.entity.R;
import com.kenanai.model.PaymentOrder;
import com.kenanai.service.PaymentOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 支付凭据单控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/pay/payment-order")
public class PaymentOrderController {

    private final PaymentOrderService paymentOrderService;

    /**
     * 创建支付订单
     */
    @PostMapping
    public R<PaymentOrder> createPaymentOrder(@RequestBody @Valid PaymentOrder paymentOrder) {
        try {
            PaymentOrder createdOrder = paymentOrderService.createPaymentOrder(paymentOrder);
            return R.ok(createdOrder, "创建支付订单成功");
        } catch (Exception e) {
            log.error("创建支付订单失败: {}", e.getMessage(), e);
            return R.failed(e.getMessage());
        }
    }

    /**
     * 根据支付单号查询支付订单
     */
    @GetMapping("/order-id/{payOrderId}")
    public R<PaymentOrder> getByPayOrderId(@PathVariable String payOrderId) {
        if (StringUtils.isBlank(payOrderId)) {
            return R.failed("支付单号不能为空");
        }

        try {
            PaymentOrder paymentOrder = paymentOrderService.getByPayOrderId(payOrderId);
            if (paymentOrder == null) {
                return R.failed("支付订单不存在");
            }
            return R.ok(paymentOrder);
        } catch (Exception e) {
            log.error("查询支付订单失败: {}", e.getMessage(), e);
            return R.failed("查询支付订单失败");
        }
    }

    /**
     * 根据业务单号查询支付订单
     */
    @GetMapping("/biz-no")
    public R<PaymentOrder> getByBizNo(
            @RequestParam String bizNo,
            @RequestParam(required = false) String bizType) {
        if (StringUtils.isBlank(bizNo)) {
            return R.failed("业务单号不能为空");
        }

        try {
            PaymentOrder paymentOrder = paymentOrderService.getByBizNo(bizNo, bizType);
            if (paymentOrder == null) {
                return R.failed("支付订单不存在");
            }
            return R.ok(paymentOrder);
        } catch (Exception e) {
            log.error("查询支付订单失败: {}", e.getMessage(), e);
            return R.failed("查询支付订单失败");
        }
    }

    /**
     * 更新支付订单状态
     */
    @PutMapping("/state")
    public R<Boolean> updateOrderState(
            @RequestParam String payOrderId,
            @RequestParam String orderState) {
        if (StringUtils.isBlank(payOrderId) || StringUtils.isBlank(orderState)) {
            return R.failed("支付单号和订单状态不能为空");
        }

        try {
            boolean success = paymentOrderService.updateOrderState(payOrderId, orderState);
            if (success) {
                return R.ok(true, "更新订单状态成功");
            } else {
                return R.failed("更新订单状态失败");
            }
        } catch (Exception e) {
            log.error("更新订单状态失败: {}", e.getMessage(), e);
            return R.failed("更新订单状态失败");
        }
    }

    /**
     * 更新支付成功信息
     */
    @PutMapping("/pay-success")
    public R<Boolean> updatePaySuccess(
            @RequestParam String payOrderId,
            @RequestParam(required = false) String channelStreamId,
            @RequestParam BigDecimal paidAmount) {
        if (StringUtils.isBlank(payOrderId) || paidAmount == null) {
            return R.failed("支付单号和支付金额不能为空");
        }

        try {
            boolean success = paymentOrderService.updatePaySuccess(payOrderId, channelStreamId, paidAmount);
            if (success) {
                return R.ok(true, "更新支付成功信息成功");
            } else {
                return R.failed("更新支付成功信息失败");
            }
        } catch (Exception e) {
            log.error("更新支付成功信息失败: {}", e.getMessage(), e);
            return R.failed("更新支付成功信息失败");
        }
    }

    /**
     * 更新支付失败信息
     */
    @PutMapping("/pay-failed")
    public R<Boolean> updatePayFailed(@RequestParam String payOrderId) {
        if (StringUtils.isBlank(payOrderId)) {
            return R.failed("支付单号不能为空");
        }

        try {
            boolean success = paymentOrderService.updatePayFailed(payOrderId);
            if (success) {
                return R.ok(true, "更新支付失败信息成功");
            } else {
                return R.failed("更新支付失败信息失败");
            }
        } catch (Exception e) {
            log.error("更新支付失败信息失败: {}", e.getMessage(), e);
            return R.failed("更新支付失败信息失败");
        }
    }

    /**
     * 分页查询支付订单
     */
    @GetMapping("/page")
    public R<IPage<PaymentOrder>> pagePaymentOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String payOrderId,
            @RequestParam(required = false) String orderState,
            @RequestParam(required = false) String payChannel) {
        try {
            IPage<PaymentOrder> result = paymentOrderService.pagePaymentOrders(page, size, payOrderId, orderState, payChannel);
            return R.ok(result);
        } catch (Exception e) {
            log.error("分页查询支付订单失败: {}", e.getMessage(), e);
            return R.failed("分页查询支付订单失败");
        }
    }

    /**
     * 更新退款信息
     */
    @PutMapping("/refund-info")
    public R<Boolean> updateRefundInfo(
            @RequestParam String payOrderId,
            @RequestParam(required = false) String refundChannelStreamId,
            @RequestParam BigDecimal refundAmount) {
        if (StringUtils.isBlank(payOrderId) || refundAmount == null) {
            return R.failed("支付单号和退款金额不能为空");
        }

        try {
            boolean success = paymentOrderService.updateRefundInfo(payOrderId, refundChannelStreamId, refundAmount);
            if (success) {
                return R.ok(true, "更新退款信息成功");
            } else {
                return R.failed("更新退款信息失败");
            }
        } catch (Exception e) {
            log.error("更新退款信息失败: {}", e.getMessage(), e);
            return R.failed("更新退款信息失败");
        }
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public R<PaymentOrder> getById(@PathVariable Long id) {
        if (id == null) {
            return R.failed("订单ID不能为空");
        }

        try {
            PaymentOrder paymentOrder = paymentOrderService.getById(id);
            if (paymentOrder == null) {
                return R.failed("支付订单不存在");
            }
            return R.ok(paymentOrder);
        } catch (Exception e) {
            log.error("查询支付订单详情失败: {}", e.getMessage(), e);
            return R.failed("查询支付订单详情失败");
        }
    }

    /**
     * 删除支付订单（逻辑删除，仅用于测试环境）
     */
    @DeleteMapping("/{id}")
    public R<Boolean> deleteById(@PathVariable Long id) {
        if (id == null) {
            return R.failed("订单ID不能为空");
        }

        try {
            boolean success = paymentOrderService.removeById(id);
            if (success) {
                return R.ok(true, "删除支付订单成功");
            } else {
                return R.failed("删除支付订单失败");
            }
        } catch (Exception e) {
            log.error("删除支付订单失败: {}", e.getMessage(), e);
            return R.failed("删除支付订单失败");
        }
    }
} 