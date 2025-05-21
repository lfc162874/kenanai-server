package com.kenanai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kenanai.model.PaymentOrder;

/**
 * 支付凭据单服务接口
 */
public interface PaymentOrderService extends IService<PaymentOrder> {

    /**
     * 创建支付订单
     *
     * @param paymentOrder 支付订单信息
     * @return 创建的支付订单
     */
    PaymentOrder createPaymentOrder(PaymentOrder paymentOrder);

    /**
     * 通过支付单号查询支付订单
     *
     * @param payOrderId 支付单号
     * @return 支付订单信息
     */
    PaymentOrder getByPayOrderId(String payOrderId);

    /**
     * 通过业务单号查询支付订单
     *
     * @param bizNo 业务单号
     * @param bizType 业务类型
     * @return 支付订单信息
     */
    PaymentOrder getByBizNo(String bizNo, String bizType);

    /**
     * 更新支付订单状态
     *
     * @param payOrderId 支付单号
     * @param orderState 订单状态
     * @return 是否更新成功
     */
    boolean updateOrderState(String payOrderId, String orderState);

    /**
     * 更新支付订单支付成功信息
     *
     * @param payOrderId 支付单号
     * @param channelStreamId 渠道流水号
     * @param paidAmount 支付金额
     * @return 是否更新成功
     */
    boolean updatePaySuccess(String payOrderId, String channelStreamId, java.math.BigDecimal paidAmount);

    /**
     * 更新支付订单支付失败信息
     *
     * @param payOrderId 支付单号
     * @return 是否更新成功
     */
    boolean updatePayFailed(String payOrderId);

    /**
     * 分页查询支付订单
     *
     * @param page 页码
     * @param size 每页数量
     * @param payOrderId 支付单号（可选）
     * @param orderState 订单状态（可选）
     * @param payChannel 支付渠道（可选）
     * @return 分页支付订单信息
     */
    IPage<PaymentOrder> pagePaymentOrders(int page, int size, String payOrderId, String orderState, String payChannel);

    /**
     * 更新退款信息
     *
     * @param payOrderId 支付单号
     * @param refundChannelStreamId 退款渠道流水号
     * @param refundAmount 退款金额
     * @return 是否更新成功
     */
    boolean updateRefundInfo(String payOrderId, String refundChannelStreamId, java.math.BigDecimal refundAmount);
} 