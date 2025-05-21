package com.kenanai.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@TableName("shop_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 默认超时时间
     */
    public static final int DEFAULT_TIME_OUT_MINUTES = 30;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 运费
     */
    private BigDecimal freightAmount;

    /**
     * 支付方式（1: 支付宝，2: 微信，3: 余额）
     */
    private Integer payType;

    /**
     * 订单来源（1: PC，2: APP，3: 小程序）
     */
    private Integer sourceType;

    /**
     * 订单状态（0: 待付款，1: 待发货，2: 已发货，3: 已完成，4: 已关闭，5: 已取消）
     */
    private Integer status;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 收货人地址
     */
    private String receiverAddress;

    /**
     * 订单备注
     */
    private String note;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 发货时间
     */
    private LocalDateTime deliveryTime;

    /**
     * 确认收货时间
     */
    private LocalDateTime receiveTime;

    /**
     * 评价时间
     */
    private LocalDateTime commentTime;


    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

} 