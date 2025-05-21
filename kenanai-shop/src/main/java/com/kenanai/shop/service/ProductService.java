package com.kenanai.shop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kenanai.shop.entity.Product;

import java.util.List;

/**
 * 商品服务接口
 */
public interface ProductService extends IService<Product> {

    /**
     * 分页查询商品
     *
     * @param page     页码
     * @param size     每页数量
     * @param keyword  关键词
     * @param categoryId 分类ID
     * @return 分页商品数据
     */
    IPage<Product> pageProducts(int page, int size, String keyword, Long categoryId);

    /**
     * 获取热门商品
     *
     * @param limit 数量限制
     * @return 热门商品列表
     */
    List<Product> getHotProducts(int limit);

    /**
     * 获取推荐商品
     *
     * @param limit 数量限制
     * @return 推荐商品列表
     */
    List<Product> getRecommendProducts(int limit);

    /**
     * 更新商品库存
     *
     * @param productId 商品ID
     * @param quantity  数量变化（减少为负数）
     * @return 是否成功
     */
    boolean updateStock(Long productId, int quantity);

    /**
     * 商品上架
     *
     * @param productId 商品ID
     * @return 是否成功
     */
    boolean onSale(Long productId);

    /**
     * 商品下架
     *
     * @param productId 商品ID
     * @return 是否成功
     */
    boolean offSale(Long productId);

    /**
     * 获取商品库存
     */
    Integer getStock(Long productId);

    /**
     * 判断商品是否可售
     */
    Boolean isProductOnSale(Long productId);
} 