package com.kenanai.constant;

/**
 * 支付相关常量
 */
public interface PaymentConstants {

    /**
     * 订单状态
     */
    interface OrderState {
        /**
         * 待支付
         */
        String WAITING = "WAITING";

        /**
         * 支付中
         */
        String PAYING = "PAYING";

        /**
         * 已支付
         */
        String PAID = "PAID";

        /**
         * 支付失败
         */
        String FAILED = "FAILED";

        /**
         * 已取消
         */
        String CANCELED = "CANCELED";

        /**
         * 已超时
         */
        String EXPIRED = "EXPIRED";

        /**
         * 部分退款
         */
        String REFUNDED_PART = "REFUNDED_PART";

        /**
         * 全额退款
         */
        String REFUNDED_FULL = "REFUNDED_FULL";
    }

    /**
     * 支付渠道
     */
    interface PayChannel {
        /**
         * 支付宝
         */
        String ALIPAY = "ALIPAY";

        /**
         * 微信支付
         */
        String WECHAT = "WECHAT";

        /**
         * 银联支付
         */
        String UNIONPAY = "UNIONPAY";

        /**
         * 余额支付
         */
        String BALANCE = "BALANCE";
    }

    /**
     * 付款方/收款方类型
     */
    interface PayerType {
        /**
         * 用户
         */
        String USER = "USER";

        /**
         * 系统
         */
        String SYSTEM = "SYSTEM";

        /**
         * 商户
         */
        String MERCHANT = "MERCHANT";
    }

    /**
     * 业务单号类型
     */
    interface BizType {
        /**
         * 充值
         */
        String RECHARGE = "RECHARGE";

        /**
         * 订单
         */
        String ORDER = "ORDER";

        /**
         * 会员
         */
        String MEMBERSHIP = "MEMBERSHIP";
    }
} 