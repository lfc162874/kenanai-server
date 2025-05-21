package com.kenanai.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kenanai.shop.entity.Product;
import com.kenanai.shop.mapper.ProductMapper;
import com.kenanai.shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商品服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Override
    public IPage<Product> pageProducts(int page, int size, String keyword, Long categoryId) {
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        
        // 关键词查询
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(Product::getName, keyword)
                    .or()
                    .like(Product::getDescription, keyword);
        }
        
        // 分类查询
        if (categoryId != null && categoryId > 0) {
            queryWrapper.eq(Product::getCategoryId, categoryId);
        }
        
        // 只查询上架商品
        queryWrapper.eq(Product::getStatus, 1);
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(Product::getCreateTime);
        
        return page(new Page<>(page, size), queryWrapper);
    }

    @Override
    public List<Product> getHotProducts(int limit) {
        // 获取redis zset的hot_products的排序值按照降序的前limit参数
        Set<Object> hotProducts = redisTemplate.opsForZSet().reverseRange("hot_products", 0, limit - 1);
        if (hotProducts!=null){
            //根据id查商品然后吧商品数据返回就好了
           return hotProducts.stream().map(id -> {
                Product product = getById((Serializable) id);
                product.setSales(redisTemplate.opsForZSet().score("hot_products", id).intValue());
                return product;
            }).toList();
        }
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getStatus, 1)
                .orderByDesc(Product::getSales)
                .last("LIMIT " + limit);
        
        return list(queryWrapper);
    }

    @Override
    public List<Product> getRecommendProducts(int limit) {
        // 这里可以根据业务需求实现推荐逻辑，简单实现为获取最新商品
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getStatus, 1)
                .orderByDesc(Product::getCreateTime)
                .last("LIMIT " + limit);
        
        return list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStock(Long productId, int quantity) {
        // 增加库存为正数，减少库存为负数
        Product product = getById(productId);
        if (product == null) {
            return false;
        }
        
        // 如果是减库存操作，检查库存是否足够
        if (quantity < 0 && product.getStock() < Math.abs(quantity)) {
            throw new RuntimeException("商品库存不足");
        }
        
        // 更新库存
        LambdaUpdateWrapper<Product> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Product::getId, productId)
                .setSql("stock = stock + " + quantity);
        
        // 如果是减库存操作，同时更新销量
        if (quantity < 0) {
            updateWrapper.setSql("sales = sales + " + Math.abs(quantity));
        }
        
        return update(updateWrapper);
    }

    @Override
    public boolean onSale(Long productId) {
        LambdaUpdateWrapper<Product> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Product::getId, productId)
                .set(Product::getStatus, 1);
        
        return update(updateWrapper);
    }

    @Override
    public boolean offSale(Long productId) {
        LambdaUpdateWrapper<Product> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Product::getId, productId)
                .set(Product::getStatus, 0);
        
        return update(updateWrapper);
    }

    @Override
    public Integer getStock(Long productId) {
        Product product = getById(productId);
        return product != null ? product.getStock() : null;
    }

    @Override
    public Boolean isProductOnSale(Long productId) {
        Product product = getById(productId);
        return product != null && product.getStatus() != null && product.getStatus() == 1;
    }
} 