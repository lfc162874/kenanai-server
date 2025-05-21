package com.kenanai.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 */
@Data
@TableName("shop_product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品原价
     */
    private BigDecimal originalPrice;

    /**
     * 商品分类ID
     */
    private Long categoryId;

    /**
     * 商品图片地址
     */
    private String imageUrl;

    /**
     * 商品详情（富文本）
     */
    private String detail;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 销售数量
     */
    private Integer sales;

    /**
     * 商品状态（0: 下架，1: 上架）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 