package com.kenanai.user.controller;

import com.kenanai.common.constant.CommonConstants;
import com.kenanai.common.entity.R;
import com.kenanai.user.dto.PasswordDTO;
import com.kenanai.user.dto.RegisterDTO;
import com.kenanai.user.entity.User;
import com.kenanai.user.service.UserService;
import com.kenanai.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.math.BigDecimal;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public R<UserVO> register(@RequestBody @Valid RegisterDTO registerDTO) {
        try {
            User user = userService.register(registerDTO);
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return R.ok(userVO, "注册成功");
        } catch (Exception e) {
            log.error("注册失败: {}", e.getMessage(), e);
            return R.failed(e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public R<UserVO> getUserInfo(HttpServletRequest request) {
        // 从请求中获取用户ID
        String userId = request.getHeader(CommonConstants.USER_ID);
        if (userId == null) {
            return R.failed("未获取到用户ID");
        }

        User user = userService.getById(Long.parseLong(userId));
        if (user == null) {
            return R.failed("用户不存在");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return R.ok(userVO);
    }

    /**
     * 更新用户资料
     */
    @PutMapping("/profile")
    public R<UserVO> updateProfile(@RequestBody @Valid User user, HttpServletRequest request) {
        // 从请求中获取用户ID
        String userId = request.getHeader(CommonConstants.USER_ID);
        if (userId == null) {
            return R.failed("未获取到用户ID");
        }

        // 设置用户ID（防止ID篡改）
        user.setId(Long.parseLong(userId));

        User updated = userService.updateUserProfile(user);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(updated, userVO);
        return R.ok(userVO);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public R<Boolean> updatePassword(@RequestBody @Valid PasswordDTO passwordDTO, HttpServletRequest request) {
        // 从请求中获取用户ID
        String userId = request.getHeader(CommonConstants.USER_ID);
        if (userId == null) {
            return R.failed("未获取到用户ID");
        }

        boolean result = userService.changePassword(
                Long.parseLong(userId),
                passwordDTO.getOldPassword(),
                passwordDTO.getNewPassword()
        );

        return result ? R.ok(true, "密码修改成功") : R.failed("旧密码不正确");
    }

    /**
     * 重置API密钥
     */
    @PutMapping("/reset-api-key")
    public R<String> resetApiKey(HttpServletRequest request) {
        // 从请求中获取用户ID
        String userId = request.getHeader(CommonConstants.USER_ID);
        if (userId == null) {
            return R.failed("未获取到用户ID");
        }

        String apiKey = userService.resetApiKey(Long.parseLong(userId));
        return R.ok(apiKey, "API密钥重置成功");
    }

    /**
     * 获取用户余额
     */
    @GetMapping("/balance")
    public R<BigDecimal> getUserBalance(HttpServletRequest request) {
        // 从请求中获取用户ID
        String userId = request.getHeader(CommonConstants.USER_ID);
        if (userId == null) {
            return R.failed("未获取到用户ID");
        }

        BigDecimal balance = userService.getUserBalance(Long.parseLong(userId));
        return R.ok(balance);
    }

    /**
     * 获取指定用户信息（管理员权限）
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public R<UserVO> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return R.failed("用户不存在");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return R.ok(userVO);
    }
    
    /**
     * 根据用户名查找用户（内部接口，供其他服务调用）
     */
    @GetMapping("/findByUsername")
    public R<User> findUserByUsername(@RequestParam("username") String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return R.failed("用户不存在");
        }
        return R.ok(user);
    }
} 