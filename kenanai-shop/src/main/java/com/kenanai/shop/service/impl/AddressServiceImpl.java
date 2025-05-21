package com.kenanai.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kenanai.shop.entity.Address;
import com.kenanai.shop.mapper.AddressMapper;
import com.kenanai.shop.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 地址服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Override
    public List<Address> getUserAddresses(Long userId) {
        LambdaQueryWrapper<Address> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Address::getUserId, userId)
                .orderByDesc(Address::getDefaultStatus)
                .orderByDesc(Address::getCreateTime);
        
        return list(queryWrapper);
    }

    @Override
    public Address getDefaultAddress(Long userId) {
        LambdaQueryWrapper<Address> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Address::getUserId, userId)
                .eq(Address::getDefaultStatus, 1)
                .last("LIMIT 1");
        
        return getOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setDefaultAddress(Long userId, Long addressId) {
        // 先将该用户所有地址设为非默认
        LambdaUpdateWrapper<Address> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Address::getUserId, userId)
                .set(Address::getDefaultStatus, 0);
                
        update(updateWrapper);
        
        // 再将指定地址设为默认
        Address address = getById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            return false;
        }
        
        address.setDefaultStatus(1);
        return updateById(address);
    }

    @Override
    public boolean deleteAddress(Long userId, Long addressId) {
        LambdaQueryWrapper<Address> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Address::getId, addressId)
                .eq(Address::getUserId, userId);
                
        return remove(queryWrapper);
    }
} 