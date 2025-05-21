package com.kenanai.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付凭据单数据传输对象
 */
@Data
public class PaymentOrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 支付单号
     */
    private String payOrderId;

    /**
     * 付款方ID
     */
    private String payerId;

    /**
     * 付款方ID类型
     */
    private String payerType;

    /**
     * 收款方ID
     */
    private String payeeId;

    /**
     * 收款方ID类型
     */
    private String payeeType;

    /**
     * 业务单号
     */
    private String bizNo;

    /**
     * 业务单号类型
     */
    private String bizType;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 已支付金额
     */
    private BigDecimal paidAmount;

    /**
     * 已退款金额
     */
    private BigDecimal refundedAmount;

    /**
     * 外部支付流水号
     */
    private String channelStreamId;

    /**
     * 退款渠道流水号
     */
    private String refundChannelStreamId;

    /**
     * 支付链接
     */
    private String payUrl;

    /**
     * 支付渠道
     */
    private String payChannel;

    /**
     * 支付备注
     */
    private String memo;

    /**
     * 订单状态
     */
    private String orderState;

    /**
     * 支付成功时间
     */
    private LocalDateTime paySucceedTime;

    /**
     * 支付失败时间
     */
    private LocalDateTime payFailedTime;

    /**
     * 支付超时时间
     */
    private LocalDateTime payExpireTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 