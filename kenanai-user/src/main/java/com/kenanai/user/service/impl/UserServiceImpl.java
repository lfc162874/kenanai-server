package com.kenanai.user.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kenanai.user.dto.RegisterDTO;
import com.kenanai.user.entity.User;
import com.kenanai.user.mapper.UserMapper;
import com.kenanai.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        User existUser = findByUsername(registerDTO.getUsername());
        if (existUser != null) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        existUser = getOne(new LambdaQueryWrapper<User>().eq(User::getEmail, registerDTO.getEmail()));
        if (existUser != null) {
            throw new IllegalArgumentException("邮箱已被注册");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        // 加密密码
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setNickname(registerDTO.getUsername()); // 默认昵称与用户名相同
        user.setStatus(0); // 0-正常状态
        user.setBalance(new BigDecimal("0")); // 初始余额为0
        user.setApiKey(IdUtil.fastSimpleUUID()); // 生成API密钥
        user.setDeleted(0); // 0-未删除
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        // 保存用户
        save(user);
        
        return user;
    }

    @Override
    public User findByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User updateUserProfile(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 获取原始用户对象
        User existUser = getById(user.getId());
        if (existUser == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 更新基本信息，但保留敏感信息不变
        existUser.setNickname(user.getNickname());
        existUser.setPhone(user.getPhone());
        existUser.setEmail(user.getEmail());
        existUser.setAvatar(user.getAvatar());
        existUser.setUpdateTime(LocalDateTime.now());

        // 更新用户信息
        updateById(existUser);
        return existUser;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }

        // 加密新密码并更新
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());

        return updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String resetApiKey(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 生成新的API密钥
        String apiKey = IdUtil.fastSimpleUUID();
        user.setApiKey(apiKey);
        user.setUpdateTime(LocalDateTime.now());

        updateById(user);
        return apiKey;
    }

    @Override
    public BigDecimal getUserBalance(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return user.getBalance();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBalance(Long userId, BigDecimal amount) {
        User user = getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 计算新余额（加上或减去金额）
        BigDecimal newBalance = user.getBalance().add(amount);
        
        // 如果是扣费操作，需要检查余额是否足够
        if (amount.compareTo(BigDecimal.ZERO) < 0 && newBalance.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }

        user.setBalance(newBalance);
        user.setUpdateTime(LocalDateTime.now());

        return updateById(user);
    }
} 