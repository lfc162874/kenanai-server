package com.kenanai.shop.controller;

import com.kenanai.common.constant.CommonConstants;
import com.kenanai.common.entity.R;
import com.kenanai.shop.entity.Address;
import com.kenanai.shop.service.AddressService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/shop/address")
public class AddressController {

    private final AddressService addressService;

    /**
     * 获取用户地址列表
     */
    @GetMapping("/list")
    public R<List<Address>> getUserAddresses(HttpServletRequest request) {
        try {
            String userIdStr = request.getHeader(CommonConstants.USER_ID);
            if (userIdStr == null) {
                return R.failed("请先登录");
            }
            
            Long userId = Long.parseLong(userIdStr);
            List<Address> addresses = addressService.getUserAddresses(userId);
            return R.ok(addresses);
        } catch (Exception e) {
            log.error("获取地址列表失败: {}", e.getMessage(), e);
            return R.failed("获取地址列表失败");
        }
    }

    /**
     * 获取默认地址
     */
    @GetMapping("/default")
    public R<Address> getDefaultAddress(HttpServletRequest request) {
        try {
            String userIdStr = request.getHeader(CommonConstants.USER_ID);
            if (userIdStr == null) {
                return R.failed("请先登录");
            }
            
            Long userId = Long.parseLong(userIdStr);
            Address address = addressService.getDefaultAddress(userId);
            return address != null ? R.ok(address) : R.failed("未设置默认地址");
        } catch (Exception e) {
            log.error("获取默认地址失败: {}", e.getMessage(), e);
            return R.failed("获取默认地址失败");
        }
    }

    /**
     * 新增地址
     */
    @PostMapping
    public R<Address> addAddress(@RequestBody @Valid Address address, HttpServletRequest request) {
        try {
            String userIdStr = request.getHeader(CommonConstants.USER_ID);
            if (userIdStr == null) {
                return R.failed("请先登录");
            }
            
            Long userId = Long.parseLong(userIdStr);
            address.setUserId(userId);
            
            // 如果是默认地址，需要将其他地址设为非默认
            if (address.getDefaultStatus() != null && address.getDefaultStatus() == 1) {
                addressService.setDefaultAddress(userId, null);
            }
            
            boolean success = addressService.save(address);
            return success ? R.ok(address, "添加地址成功") : R.failed("添加地址失败");
        } catch (Exception e) {
            log.error("添加地址失败: {}", e.getMessage(), e);
            return R.failed("添加地址失败");
        }
    }

    /**
     * 更新地址
     */
    @PutMapping
    public R<Boolean> updateAddress(@RequestBody @Valid Address address, HttpServletRequest request) {
        try {
            String userIdStr = request.getHeader(CommonConstants.USER_ID);
            if (userIdStr == null) {
                return R.failed("请先登录");
            }
            
            Long userId = Long.parseLong(userIdStr);
            
            // 确保地址属于当前用户
            Address existAddress = addressService.getById(address.getId());
            if (existAddress == null || !existAddress.getUserId().equals(userId)) {
                return R.failed("无权限修改该地址");
            }
            
            // 设置用户ID，防止篡改
            address.setUserId(userId);
            
            // 如果设为默认地址，需要将其他地址设为非默认
            if (address.getDefaultStatus() != null && address.getDefaultStatus() == 1) {
                addressService.setDefaultAddress(userId, address.getId());
            }
            
            boolean success = addressService.updateById(address);
            return success ? R.ok(true, "更新地址成功") : R.failed("更新地址失败");
        } catch (Exception e) {
            log.error("更新地址失败: {}", e.getMessage(), e);
            return R.failed("更新地址失败");
        }
    }

    /**
     * 删除地址
     */
    @DeleteMapping("/{id}")
    public R<Boolean> deleteAddress(@PathVariable Long id, HttpServletRequest request) {
        try {
            String userIdStr = request.getHeader(CommonConstants.USER_ID);
            if (userIdStr == null) {
                return R.failed("请先登录");
            }
            
            Long userId = Long.parseLong(userIdStr);
            boolean success = addressService.deleteAddress(userId, id);
            return success ? R.ok(true, "删除地址成功") : R.failed("删除地址失败");
        } catch (Exception e) {
            log.error("删除地址失败: {}", e.getMessage(), e);
            return R.failed("删除地址失败");
        }
    }

    /**
     * 设置默认地址
     */
    @PostMapping("/{id}/default")
    public R<Boolean> setDefaultAddress(@PathVariable Long id, HttpServletRequest request) {
        try {
            String userIdStr = request.getHeader(CommonConstants.USER_ID);
            if (userIdStr == null) {
                return R.failed("请先登录");
            }
            
            Long userId = Long.parseLong(userIdStr);
            boolean success = addressService.setDefaultAddress(userId, id);
            return success ? R.ok(true, "设置默认地址成功") : R.failed("设置默认地址失败");
        } catch (Exception e) {
            log.error("设置默认地址失败: {}", e.getMessage(), e);
            return R.failed("设置默认地址失败");
        }
    }
} 