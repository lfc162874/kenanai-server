package com.kenanai.shop.controller;

import com.kenanai.common.constant.CommonConstants;
import com.kenanai.common.entity.R;
import com.kenanai.shop.entity.Category;
import com.kenanai.shop.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/shop/category")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 获取一级分类
     */
    @GetMapping("/parent")
    public R<List<Category>> getParentCategories() {
        try {
            List<Category> categories = categoryService.getParentCategories();
            return R.ok(categories);
        } catch (Exception e) {
            log.error("获取一级分类失败: {}", e.getMessage(), e);
            return R.failed("获取一级分类失败");
        }
    }

    /**
     * 获取子分类
     */
    @GetMapping("/children/{parentId}")
    public R<List<Category>> getChildCategories(@PathVariable Long parentId) {
        try {
            List<Category> categories = categoryService.getChildCategories(parentId);
            return R.ok(categories);
        } catch (Exception e) {
            log.error("获取子分类失败: {}", e.getMessage(), e);
            return R.failed("获取子分类失败");
        }
    }

    /**
     * 获取分类树
     */
    @GetMapping("/tree")
    public R<List<Category>> getCategoryTree() {
        try {
            List<Category> categoryTree = categoryService.getCategoryTree();
            return R.ok(categoryTree);
        } catch (Exception e) {
            log.error("获取分类树失败: {}", e.getMessage(), e);
            return R.failed("获取分类树失败");
        }
    }

    /**
     * 新增分类（仅管理员）
     */
    @PostMapping
    public R<Category> addCategory(@RequestBody @Valid Category category, HttpServletRequest request) {
        try {
            // TODO: 权限校验，仅管理员可操作
            
            boolean success = categoryService.save(category);
            return success ? R.ok(category, "添加分类成功") : R.failed("添加分类失败");
        } catch (Exception e) {
            log.error("添加分类失败: {}", e.getMessage(), e);
            return R.failed("添加分类失败");
        }
    }

    /**
     * 更新分类（仅管理员）
     */
    @PutMapping
    public R<Boolean> updateCategory(@RequestBody @Valid Category category, HttpServletRequest request) {
        try {
            // TODO: 权限校验，仅管理员可操作
            
            boolean success = categoryService.updateById(category);
            return success ? R.ok(true, "更新分类成功") : R.failed("更新分类失败");
        } catch (Exception e) {
            log.error("更新分类失败: {}", e.getMessage(), e);
            return R.failed("更新分类失败");
        }
    }

    /**
     * 启用分类（仅管理员）
     */
    @PostMapping("/{id}/enable")
    public R<Boolean> enableCategory(@PathVariable Long id, HttpServletRequest request) {
        try {
            // TODO: 权限校验，仅管理员可操作
            
            boolean success = categoryService.enableCategory(id);
            return success ? R.ok(true, "启用分类成功") : R.failed("启用分类失败");
        } catch (Exception e) {
            log.error("启用分类失败: {}", e.getMessage(), e);
            return R.failed("启用分类失败");
        }
    }

    /**
     * 禁用分类（仅管理员）
     */
    @PostMapping("/{id}/disable")
    public R<Boolean> disableCategory(@PathVariable Long id, HttpServletRequest request) {
        try {
            // TODO: 权限校验，仅管理员可操作
            
            boolean success = categoryService.disableCategory(id);
            return success ? R.ok(true, "禁用分类成功") : R.failed("禁用分类失败");
        } catch (Exception e) {
            log.error("禁用分类失败: {}", e.getMessage(), e);
            return R.failed("禁用分类失败");
        }
    }

    /**
     * 删除分类（仅管理员）
     */
    @DeleteMapping("/{id}")
    public R<Boolean> deleteCategory(@PathVariable Long id, HttpServletRequest request) {
        try {
            // TODO: 权限校验，仅管理员可操作
            
            // 检查是否有子分类
            List<Category> children = categoryService.getChildCategories(id);
            if (!children.isEmpty()) {
                return R.failed("存在子分类，无法删除");
            }
            
            boolean success = categoryService.removeById(id);
            return success ? R.ok(true, "删除分类成功") : R.failed("删除分类失败");
        } catch (Exception e) {
            log.error("删除分类失败: {}", e.getMessage(), e);
            return R.failed("删除分类失败");
        }
    }
} 