package com.kenanai.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kenanai.shop.entity.Category;
import com.kenanai.shop.mapper.CategoryMapper;
import com.kenanai.shop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> getParentCategories() {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getParentId, 0L)
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort);
        
        return list(queryWrapper);
    }

    @Override
    public List<Category> getChildCategories(Long parentId) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getParentId, parentId)
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort);
        
        return list(queryWrapper);
    }

    @Override
    public List<Category> getCategoryTree() {
        // 获取所有分类
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort);
        List<Category> allCategories = list(queryWrapper);
        
        // 按父ID分组
        Map<Long, List<Category>> parentIdMap = allCategories.stream()
                .collect(Collectors.groupingBy(Category::getParentId));
        
        // 构建一级分类及其子分类
        List<Category> result = new ArrayList<>();
        List<Category> parentCategories = parentIdMap.getOrDefault(0L, new ArrayList<>());
        
        for (Category parent : parentCategories) {
            // 设置子分类
            buildChildCategories(parent, parentIdMap);
            result.add(parent);
        }
        
        return result;
    }
    
    /**
     * 递归构建子分类
     */
    private void buildChildCategories(Category parent, Map<Long, List<Category>> parentIdMap) {
        List<Category> children = parentIdMap.getOrDefault(parent.getId(), new ArrayList<>());
        if (!children.isEmpty()) {
            parent.setChildren(children);
            // 递归设置子分类的子分类
            for (Category child : children) {
                buildChildCategories(child, parentIdMap);
            }
        }
    }

    @Override
    public boolean enableCategory(Long categoryId) {
        Category category = getById(categoryId);
        if (category == null) {
            return false;
        }
        
        category.setStatus(1);
        return updateById(category);
    }

    @Override
    public boolean disableCategory(Long categoryId) {
        Category category = getById(categoryId);
        if (category == null) {
            return false;
        }
        
        category.setStatus(0);
        return updateById(category);
    }
} 