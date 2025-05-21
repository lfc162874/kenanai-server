package com.kenanai.user.service.impl;

import com.kenanai.common.entity.R;
import com.kenanai.user.api.UserService;
import com.kenanai.user.api.dto.UserDTO;
import com.kenanai.user.entity.User;
import com.kenanai.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 用户服务Dubbo接口实现
 */
@Slf4j
@DubboService
@RequiredArgsConstructor
public class UserDubboServiceImpl implements UserService {

    private final com.kenanai.user.service.UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public R<UserDTO> findByUsername(String username) {
        try {
            User user = userService.findByUsername(username);
            if (user == null) {
                return R.failed("用户不存在");
            }
            
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);
            return R.ok(userDTO);
        } catch (Exception e) {
            log.error("查询用户失败: {}", e.getMessage(), e);
            return R.failed(e.getMessage());
        }
    }

    @Override
    public R<UserDTO> validatePassword(String username, String password) {
        try {
            User user = userService.findByUsername(username);
            if (user == null) {
                return R.failed("用户不存在");
            }
            
            // 验证密码
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return R.failed("密码不正确");
            }
            
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);
            // 不传输密码
            userDTO.setPassword(null);
            return R.ok(userDTO);
        } catch (Exception e) {
            log.error("验证密码失败: {}", e.getMessage(), e);
            return R.failed(e.getMessage());
        }
    }
} 