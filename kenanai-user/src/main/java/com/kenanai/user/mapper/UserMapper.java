package com.kenanai.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kenanai.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
} 