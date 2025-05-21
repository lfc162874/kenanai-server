-- 创建数据库
CREATE DATABASE IF NOT EXISTS kenanai DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE kenanai;

-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `balance` decimal(15,2) DEFAULT '0.00' COMMENT '账户余额',
  `api_key` varchar(100) DEFAULT NULL COMMENT 'API密钥',
  `status` tinyint(4) DEFAULT '0' COMMENT '状态(0-正常,1-锁定)',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除(0-未删除,1-已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  UNIQUE KEY `uk_api_key` (`api_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 初始化管理员账号 密码为 admin123
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `status`, `create_time`, `update_time`)
VALUES ('admin', '$2a$10$1oJwMnGCvB38UPRLqHzG1uJOI4TW2DSJNDpaxCfkR.ZSq5ql2z5Va', '系统管理员', 0, NOW(), NOW());

-- 聊天记录表
CREATE TABLE IF NOT EXISTS `chat_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `message` text NOT NULL COMMENT '用户消息',
  `response` text NOT NULL COMMENT '系统响应',
  `model` varchar(50) DEFAULT NULL COMMENT '模型名称',
  `tokens` int(11) DEFAULT '0' COMMENT '消耗的token数',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天记录表';

-- 用户充值记录表
CREATE TABLE IF NOT EXISTS `recharge_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `amount` decimal(15,2) NOT NULL COMMENT '充值金额',
  `payment_method` varchar(20) DEFAULT NULL COMMENT '支付方式',
  `transaction_id` varchar(100) DEFAULT NULL COMMENT '交易ID',
  `status` tinyint(4) DEFAULT '0' COMMENT '状态(0-未支付,1-已支付,2-已取消)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充值记录表';


CREATE TABLE payment_order (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                               pay_order_id VARCHAR(64) NOT NULL COMMENT '支付单号',
                               payer_id VARCHAR(64) NOT NULL COMMENT '付款方ID',
                               payer_type VARCHAR(32) NOT NULL COMMENT '付款方ID类型',
                               payee_id VARCHAR(64) NOT NULL COMMENT '收款方ID',
                               payee_type VARCHAR(32) NOT NULL COMMENT '收款方ID类型',
                               biz_no VARCHAR(64) COMMENT '业务单号',
                               biz_type VARCHAR(32) COMMENT '业务单号类型',
                               order_amount DECIMAL(15, 2) NOT NULL COMMENT '订单金额',
                               paid_amount DECIMAL(15, 2) NOT NULL DEFAULT 0.00 COMMENT '已支付金额',
                               refunded_amount DECIMAL(15, 2) NOT NULL DEFAULT 0.00 COMMENT '已退款金额',
                               channel_stream_id VARCHAR(128) COMMENT '外部支付流水号',
                               refund_channel_stream_id VARCHAR(128) COMMENT '退款渠道流水号',
                               pay_url VARCHAR(256) COMMENT '支付链接',
                               pay_channel VARCHAR(32) NOT NULL COMMENT '支付渠道',
                               memo VARCHAR(256) COMMENT '支付备注',
                               order_state VARCHAR(32) NOT NULL COMMENT '订单状态',
                               pay_succeed_time DATETIME COMMENT '支付成功时间',
                               pay_failed_time DATETIME COMMENT '支付失败时间',
                               pay_expire_time DATETIME NOT NULL COMMENT '支付超时时间',
                               create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               UNIQUE KEY uni_pay_order_id (pay_order_id),
                               KEY idx_payer_id (payer_id),
                               KEY idx_payee_id (payee_id),
                               KEY idx_biz_no (biz_no),
                               KEY idx_order_state (order_state),
                               KEY idx_pay_expire_time (pay_expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付凭据单表';