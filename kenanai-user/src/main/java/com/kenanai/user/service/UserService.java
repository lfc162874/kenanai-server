package com.kenanai.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kenanai.user.dto.RegisterDTO;
import com.kenanai.user.entity.User;

import java.math.BigDecimal;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param registerDTO 注册信息
     * @return 注册成功的用户
     */
    User register(RegisterDTO registerDTO);

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象
     */
    User findByUsername(String username);

    /**
     * 更新用户资料
     * @param user 用户对象
     * @return 更新后的用户对象
     */
    User updateUserProfile(User user);

    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 重置API密钥
     * @param userId 用户ID
     * @return 新的API密钥
     */
    String resetApiKey(Long userId);

    /**
     * 获取用户余额
     * @param userId 用户ID
     * @return 用户余额
     */
    BigDecimal getUserBalance(Long userId);

    /**
     * 更新用户余额
     * @param userId 用户ID
     * @param amount 金额（正数为增加，负数为减少）
     * @return 是否成功
     */
    boolean updateBalance(Long userId, BigDecimal amount);
} 