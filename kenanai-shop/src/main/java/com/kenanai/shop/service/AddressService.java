package com.kenanai.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kenanai.shop.entity.Address;

import java.util.List;

/**
 * 地址服务接口
 */
public interface AddressService extends IService<Address> {

    /**
     * 获取用户地址列表
     *
     * @param userId 用户ID
     * @return 地址列表
     */
    List<Address> getUserAddresses(Long userId);

    /**
     * 获取默认地址
     *
     * @param userId 用户ID
     * @return 默认地址，如果没有则返回null
     */
    Address getDefaultAddress(Long userId);

    /**
     * 设置默认地址
     *
     * @param userId    用户ID
     * @param addressId 地址ID
     * @return 是否成功
     */
    boolean setDefaultAddress(Long userId, Long addressId);

    /**
     * 删除地址
     *
     * @param userId    用户ID
     * @param addressId 地址ID
     * @return 是否成功
     */
    boolean deleteAddress(Long userId, Long addressId);
} 