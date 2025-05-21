package com.kenanai.user.api;

import com.kenanai.common.entity.R;
import com.kenanai.user.api.dto.UserDTO;

/**
 * 用户服务Dubbo接口
 */
public interface UserService {

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象
     */
    R<UserDTO> findByUsername(String username);

    /**
     * 验证用户密码
     * @param username 用户名
     * @param password 密码
     * @return 是否有效
     */
    R<UserDTO> validatePassword(String username, String password);
} 