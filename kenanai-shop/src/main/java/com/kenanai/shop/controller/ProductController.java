package com.kenanai.shop.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kenanai.common.constant.CommonConstants;
import com.kenanai.common.entity.R;
import com.kenanai.shop.entity.Product;
import com.kenanai.shop.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/shop/product")
public class ProductController {

    private final ProductService productService;

    /**
     * 分页查询商品
     */
    @GetMapping("/list")
    public R<IPage<Product>> pageProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        try {
            IPage<Product> products = productService.pageProducts(page, size, keyword, categoryId);
            return R.ok(products);
        } catch (Exception e) {
            log.error("查询商品列表失败: {}", e.getMessage(), e);
            return R.failed("查询商品列表失败");
        }
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/{id}")
    public R<Product> getProductDetail(@PathVariable Long id) {
        try {
            Product product = productService.getById(id);
            if (product == null || product.getStatus() != 1) {
                return R.failed("商品不存在或已下架");
            }
            return R.ok(product);
        } catch (Exception e) {
            log.error("获取商品详情失败: {}", e.getMessage(), e);
            return R.failed("获取商品详情失败");
        }
    }

    /**
     * 获取热门商品
     */
    @GetMapping("/hot")
    public R<List<Product>> getHotProducts(@RequestParam(defaultValue = "6") Integer limit) {
        try {
            List<Product> products = productService.getHotProducts(limit);
            return R.ok(products);
        } catch (Exception e) {
            log.error("获取热门商品失败: {}", e.getMessage(), e);
            return R.failed("获取热门商品失败");
        }
    }

    /**
     * 获取推荐商品
     */
    @GetMapping("/recommend")
    public R<List<Product>> getRecommendProducts(@RequestParam(defaultValue = "6") Integer limit) {
        try {
            List<Product> products = productService.getRecommendProducts(limit);
            return R.ok(products);
        } catch (Exception e) {
            log.error("获取推荐商品失败: {}", e.getMessage(), e);
            return R.failed("获取推荐商品失败");
        }
    }

    /**
     * 新增商品（仅管理员）
     */
    @PostMapping
    public R<Product> addProduct(@RequestBody @Valid Product product, HttpServletRequest request) {
        try {
            // TODO: 权限校验，仅管理员可操作
            
            boolean success = productService.save(product);
            return success ? R.ok(product, "添加商品成功") : R.failed("添加商品失败");
        } catch (Exception e) {
            log.error("添加商品失败: {}", e.getMessage(), e);
            return R.failed("添加商品失败");
        }
    }

    /**
     * 更新商品（仅管理员）
     */
    @PutMapping
    public R<Boolean> updateProduct(@RequestBody @Valid Product product, HttpServletRequest request) {
        try {
            // TODO: 权限校验，仅管理员可操作
            
            boolean success = productService.updateById(product);
            return success ? R.ok(true, "更新商品成功") : R.failed("更新商品失败");
        } catch (Exception e) {
            log.error("更新商品失败: {}", e.getMessage(), e);
            return R.failed("更新商品失败");
        }
    }

    /**
     * 商品上架（仅管理员）
     */
    @PostMapping("/{id}/on-sale")
    public R<Boolean> onSale(@PathVariable Long id, HttpServletRequest request) {
        try {
            // TODO: 权限校验，仅管理员可操作
            
            boolean success = productService.onSale(id);
            return success ? R.ok(true, "商品上架成功") : R.failed("商品上架失败");
        } catch (Exception e) {
            log.error("商品上架失败: {}", e.getMessage(), e);
            return R.failed("商品上架失败");
        }
    }

    /**
     * 商品下架（仅管理员）
     */
    @PostMapping("/{id}/off-sale")
    public R<Boolean> offSale(@PathVariable Long id, HttpServletRequest request) {
        try {
            // TODO: 权限校验，仅管理员可操作
            
            boolean success = productService.offSale(id);
            return success ? R.ok(true, "商品下架成功") : R.failed("商品下架失败");
        } catch (Exception e) {
            log.error("商品下架失败: {}", e.getMessage(), e);
            return R.failed("商品下架失败");
        }
    }

    /**
     * 删除商品（仅管理员）
     */
    @DeleteMapping("/{id}")
    public R<Boolean> deleteProduct(@PathVariable Long id, HttpServletRequest request) {
        try {
            // TODO: 权限校验，仅管理员可操作
            
            boolean success = productService.removeById(id);
            return success ? R.ok(true, "删除商品成功") : R.failed("删除商品失败");
        } catch (Exception e) {
            log.error("删除商品失败: {}", e.getMessage(), e);
            return R.failed("删除商品失败");
        }
    }
} 