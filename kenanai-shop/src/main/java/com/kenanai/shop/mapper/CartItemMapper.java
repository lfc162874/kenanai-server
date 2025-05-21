package com.kenanai.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kenanai.shop.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车Mapper接口
 */
@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {
} 