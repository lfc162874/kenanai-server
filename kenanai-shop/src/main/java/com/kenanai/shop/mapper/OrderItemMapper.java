package com.kenanai.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kenanai.shop.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单商品Mapper接口
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
    
    /**
     * 批量插入订单商品
     *
     * @param orderItems 订单商品列表
     * @return 影响行数
     */
    int insertBatchSomeColumn(@Param("list") List<OrderItem> orderItems);
} 