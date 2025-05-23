package com.kenanai.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kenanai.shop.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品Mapper接口
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
} 