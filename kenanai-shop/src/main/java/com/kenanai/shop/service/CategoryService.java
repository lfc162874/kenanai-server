package com.kenanai.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kenanai.shop.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService extends IService<Category> {

    /**
     * 获取所有一级分类
     *
     * @return 一级分类列表
     */
    List<Category> getParentCategories();

    /**
     * 获取指定父分类下的子分类
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<Category> getChildCategories(Long parentId);

    /**
     * 获取分类树结构
     *
     * @return 分类树
     */
    List<Category> getCategoryTree();

    /**
     * 启用分类
     *
     * @param categoryId 分类ID
     * @return 是否成功
     */
    boolean enableCategory(Long categoryId);

    /**
     * 禁用分类
     *
     * @param categoryId 分类ID
     * @return 是否成功
     */
    boolean disableCategory(Long categoryId);
} 