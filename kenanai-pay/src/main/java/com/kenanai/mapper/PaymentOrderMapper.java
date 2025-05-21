package com.kenanai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kenanai.model.PaymentOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付凭据单Mapper接口
 */
@Mapper
public interface PaymentOrderMapper extends BaseMapper<PaymentOrder> {
} 