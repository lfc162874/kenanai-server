package com.kenanai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kenanai.mapper.PaymentOrderMapper;
import com.kenanai.model.PaymentOrder;
import com.kenanai.service.PaymentOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付凭据单服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentOrderServiceImpl extends ServiceImpl<PaymentOrderMapper, PaymentOrder> implements PaymentOrderService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentOrder createPaymentOrder(PaymentOrder paymentOrder) {
        // 确保关键字段不为空
        if (paymentOrder == null || StringUtils.isBlank(paymentOrder.getPayOrderId())) {
            throw new IllegalArgumentException("支付单号不能为空");
        }

        // 检查支付单号是否已存在
        PaymentOrder existOrder = getByPayOrderId(paymentOrder.getPayOrderId());
        if (existOrder != null) {
            throw new IllegalArgumentException("支付单号已存在");
        }

        // 设置初始值
        if (paymentOrder.getPaidAmount() == null) {
            paymentOrder.setPaidAmount(BigDecimal.ZERO);
        }
        if (paymentOrder.getRefundedAmount() == null) {
            paymentOrder.setRefundedAmount(BigDecimal.ZERO);
        }
        if (paymentOrder.getCreateTime() == null) {
            paymentOrder.setCreateTime(LocalDateTime.now());
        }
        if (paymentOrder.getUpdateTime() == null) {
            paymentOrder.setUpdateTime(LocalDateTime.now());
        }

        // 保存订单
        boolean success = save(paymentOrder);
        if (!success) {
            throw new RuntimeException("创建支付订单失败");
        }

        log.info("创建支付订单成功: {}", paymentOrder.getPayOrderId());
        return paymentOrder;
    }

    @Override
    public PaymentOrder getByPayOrderId(String payOrderId) {
        if (StringUtils.isBlank(payOrderId)) {
            return null;
        }

        LambdaQueryWrapper<PaymentOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentOrder::getPayOrderId, payOrderId);

        return getOne(queryWrapper);
    }

    @Override
    public PaymentOrder getByBizNo(String bizNo, String bizType) {
        if (StringUtils.isBlank(bizNo)) {
            return null;
        }

        LambdaQueryWrapper<PaymentOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentOrder::getBizNo, bizNo);
        if (StringUtils.isNotBlank(bizType)) {
            queryWrapper.eq(PaymentOrder::getBizType, bizType);
        }

        return getOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrderState(String payOrderId, String orderState) {
        if (StringUtils.isBlank(payOrderId) || StringUtils.isBlank(orderState)) {
            return false;
        }

        LambdaUpdateWrapper<PaymentOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PaymentOrder::getPayOrderId, payOrderId)
                .set(PaymentOrder::getOrderState, orderState)
                .set(PaymentOrder::getUpdateTime, LocalDateTime.now());

        boolean success = update(updateWrapper);
        if (success) {
            log.info("更新支付订单状态成功: payOrderId={}, orderState={}", payOrderId, orderState);
        } else {
            log.warn("更新支付订单状态失败: payOrderId={}, orderState={}", payOrderId, orderState);
        }

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePaySuccess(String payOrderId, String channelStreamId, BigDecimal paidAmount) {
        if (StringUtils.isBlank(payOrderId) || paidAmount == null) {
            return false;
        }

        PaymentOrder paymentOrder = getByPayOrderId(payOrderId);
        if (paymentOrder == null) {
            log.warn("支付成功更新失败，未找到支付订单: payOrderId={}", payOrderId);
            return false;
        }

        LambdaUpdateWrapper<PaymentOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PaymentOrder::getPayOrderId, payOrderId)
                .set(PaymentOrder::getOrderState, "PAID")
                .set(PaymentOrder::getPaidAmount, paidAmount)
                .set(PaymentOrder::getPaySucceedTime, LocalDateTime.now())
                .set(StringUtils.isNotBlank(channelStreamId), PaymentOrder::getChannelStreamId, channelStreamId)
                .set(PaymentOrder::getUpdateTime, LocalDateTime.now());

        boolean success = update(updateWrapper);
        if (success) {
            log.info("更新支付成功信息成功: payOrderId={}, channelStreamId={}, paidAmount={}", payOrderId, channelStreamId, paidAmount);
        } else {
            log.warn("更新支付成功信息失败: payOrderId={}", payOrderId);
        }

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePayFailed(String payOrderId) {
        if (StringUtils.isBlank(payOrderId)) {
            return false;
        }

        LambdaUpdateWrapper<PaymentOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PaymentOrder::getPayOrderId, payOrderId)
                .set(PaymentOrder::getOrderState, "FAILED")
                .set(PaymentOrder::getPayFailedTime, LocalDateTime.now())
                .set(PaymentOrder::getUpdateTime, LocalDateTime.now());

        boolean success = update(updateWrapper);
        if (success) {
            log.info("更新支付失败信息成功: payOrderId={}", payOrderId);
        } else {
            log.warn("更新支付失败信息失败: payOrderId={}", payOrderId);
        }

        return success;
    }

    @Override
    public IPage<PaymentOrder> pagePaymentOrders(int page, int size, String payOrderId, String orderState, String payChannel) {
        Page<PaymentOrder> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<PaymentOrder> queryWrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        if (StringUtils.isNotBlank(payOrderId)) {
            queryWrapper.eq(PaymentOrder::getPayOrderId, payOrderId);
        }
        if (StringUtils.isNotBlank(orderState)) {
            queryWrapper.eq(PaymentOrder::getOrderState, orderState);
        }
        if (StringUtils.isNotBlank(payChannel)) {
            queryWrapper.eq(PaymentOrder::getPayChannel, payChannel);
        }

        // 按创建时间倒序排序
        queryWrapper.orderByDesc(PaymentOrder::getCreateTime);

        return page(pageParam, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRefundInfo(String payOrderId, String refundChannelStreamId, BigDecimal refundAmount) {
        if (StringUtils.isBlank(payOrderId) || refundAmount == null) {
            return false;
        }

        PaymentOrder paymentOrder = getByPayOrderId(payOrderId);
        if (paymentOrder == null) {
            log.warn("退款信息更新失败，未找到支付订单: payOrderId={}", payOrderId);
            return false;
        }

        // 检查退款金额是否超过已支付金额
        BigDecimal totalRefundedAmount = paymentOrder.getRefundedAmount().add(refundAmount);
        if (totalRefundedAmount.compareTo(paymentOrder.getPaidAmount()) > 0) {
            log.warn("退款金额超过已支付金额: payOrderId={}, paidAmount={}, totalRefundedAmount={}", 
                    payOrderId, paymentOrder.getPaidAmount(), totalRefundedAmount);
            return false;
        }

        // 计算退款后的订单状态
        String orderState = "REFUNDED_PART";
        if (totalRefundedAmount.compareTo(paymentOrder.getPaidAmount()) == 0) {
            orderState = "REFUNDED_FULL";
        }

        LambdaUpdateWrapper<PaymentOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PaymentOrder::getPayOrderId, payOrderId)
                .set(PaymentOrder::getOrderState, orderState)
                .set(PaymentOrder::getRefundedAmount, totalRefundedAmount)
                .set(StringUtils.isNotBlank(refundChannelStreamId), PaymentOrder::getRefundChannelStreamId, refundChannelStreamId)
                .set(PaymentOrder::getUpdateTime, LocalDateTime.now());

        boolean success = update(updateWrapper);
        if (success) {
            log.info("更新退款信息成功: payOrderId={}, refundChannelStreamId={}, refundAmount={}, totalRefundedAmount={}", 
                    payOrderId, refundChannelStreamId, refundAmount, totalRefundedAmount);
        } else {
            log.warn("更新退款信息失败: payOrderId={}", payOrderId);
        }

        return success;
    }
} 