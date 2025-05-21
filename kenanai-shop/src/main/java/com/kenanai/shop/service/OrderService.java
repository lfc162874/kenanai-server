package com.kenanai.shop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kenanai.shop.entity.Order;

import java.util.List;
import java.util.Map;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<Order> {

    /**
     * 创建订单
     *
     * @param userId     用户ID
     * @param cartItemIds 购物车项ID列表
     * @param addressId  地址ID
     * @param note       订单备注
     * @return 订单ID
     */
    Long createOrder(Long userId, List<Long> cartItemIds, Long addressId, String note);

    /**
     * 订单支付
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     * @param payType 支付方式
     * @return 是否成功
     */
    boolean payOrder(Long orderId, Long userId, Integer payType);

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     * @return 是否成功
     */
    boolean cancelOrder(Long orderId, Long userId);

    /**
     * 发货
     *
     * @param orderId         订单ID
     * @param deliveryCompany 物流公司
     * @param deliveryNo      物流单号
     * @return 是否成功
     */
    boolean shipOrder(Long orderId, String deliveryCompany, String deliveryNo);

    /**
     * 确认收货
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     * @return 是否成功
     */
    boolean confirmReceive(Long orderId, Long userId);

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     * @return 订单详情，包含订单信息和订单商品列表
     */
    Map<String, Object> getOrderDetail(Long orderId, Long userId);

    /**
     * 分页获取用户订单列表
     *
     * @param userId 用户ID
     * @param status 订单状态，为null则获取全部
     * @param page   页码
     * @param size   每页条数
     * @return 分页订单列表
     */
    IPage<Order> pageUserOrders(Long userId, Integer status, int page, int size);
} 