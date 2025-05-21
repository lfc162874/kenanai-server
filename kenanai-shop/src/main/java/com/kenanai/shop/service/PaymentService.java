package com.kenanai.shop.service;

import java.util.Map;

/**
 * 支付服务接口
 */
public interface PaymentService {

    /**
     * 生成支付信息
     *
     * @param orderNo 订单号
     * @param amount  支付金额
     * @param payType 支付方式（1：支付宝，2：微信，3：银联）
     * @return 支付参数
     */
    Map<String, String> generatePayInfo(String orderNo, String amount, Integer payType);

    /**
     * 查询支付状态
     *
     * @param orderNo 订单号
     * @param payType 支付方式
     * @return 支付状态（success：支付成功，waiting：等待支付，failed：支付失败）
     */
    String queryPayStatus(String orderNo, Integer payType);

    /**
     * 支付回调处理
     *
     * @param params  回调参数
     * @param payType 支付方式
     * @return 处理结果
     */
    boolean handlePayCallback(Map<String, String> params, Integer payType);

    /**
     * 申请退款
     *
     * @param orderNo     订单号
     * @param refundAmount 退款金额
     * @param refundReason 退款原因
     * @param payType     支付方式
     * @return 退款结果
     */
    boolean refund(String orderNo, String refundAmount, String refundReason, Integer payType);
} 