package com.kenanai.shop;

import com.kenanai.shop.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Set;

@SpringBootTest
public class OrderPayConcurrencyTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 多线程高并发模拟支付同一个订单，测试排行榜热度分数是否一致
     */
    @Test
    public void testPayOrderConcurrency() throws InterruptedException {
        int threadCount = 50; // 并发线程数
        Long orderId = 1L;    // 假设订单ID为1（请确保数据库中有此订单）
        Long userId = 2L;     // 假设用户ID为1（请确保数据库中有此用户）
        Integer payType = 1;  // 假设支付类型为1

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 清空排行榜
        redisTemplate.delete("hot_products");

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    orderService.payOrder(orderId, userId, payType);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // 检查排行榜
        Double score = redisTemplate.opsForZSet().score("hot_products", 6L);
        System.out.println("商品1的热度分数：" + score);
        // 理论上只允许一次支付成功，分数应为1
        // Assertions.assertEquals(1.0, score);
    }

    /**
     * 多订单多商品高并发支付测试
     */
    @Test
    public void testMultiOrderMultiProductConcurrency() throws InterruptedException {
        int orderCount = 5; // 订单数量
        int threadCount = 50; // 并发线程数
        Long userId = 2L;     // 假设用户ID为2
        Integer payType = 1;  // 假设支付类型为1

        // 假设订单和商品的关系如下（请确保数据库中有这些订单和商品）
        Long[] orderIds = {32L, 33L, 34L, 35L, 36L};
        // 订单11->101, 12->102, 13->101, 14->103, 15->101

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(orderCount);

        // 清空排行榜
        redisTemplate.delete("hot_products");

        for (int i = 0; i < orderCount; i++) {
            final int idx = i;
            executor.submit(() -> {
                try {
                    orderService.payOrder(orderIds[idx], userId, payType);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

//        // 检查每个商品的分数
//        Long[] uniqueProductIds = {101L, 102L, 103L};
//        for (Long productId : uniqueProductIds) {
//            Double score = redisTemplate.opsForZSet().score("hot_products", productId);
//            System.out.println("商品" + productId + "的热度分数：" + score);
//        }
        // 预期输出：101分数为3，102为1，103为1
    }

    /**
     * 模拟真实多用户多订单高并发支付场景
     * 使用两个用户(1和2)交替支付多个订单，验证排行榜一致性
     */
    @Test
    public void testRealUsersConcurrentPayment() throws InterruptedException {
        // 待支付的订单IDs
        Long[] orderIds = {32L, 33L, 34L, 35L, 36L};
        
        // 模拟两个用户
        Long[] userIds = {2L};
        
        // 支付类型
        Integer payType = 1;
        
        // 并发线程数 (至少等于订单数)
        int threadCount = Math.max(20, orderIds.length);
        
        // 准备线程池和计数器
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(orderIds.length);
        
        // 清空排行榜
        redisTemplate.delete("hot_products");
        
        System.out.println("======= 开始多用户并发支付测试 =======");
        System.out.println("订单IDs: " + java.util.Arrays.toString(orderIds));
        
        // 模拟多个用户并发支付不同订单
        for (int i = 0; i < orderIds.length; i++) {
            final Long orderId = orderIds[i];
            // 交替使用不同用户ID
            final Long userId = userIds[i % userIds.length];
            
            executor.submit(() -> {
                try {
                    System.out.println("用户" + userId + "尝试支付订单" + orderId);
                    boolean success = orderService.payOrder(orderId, userId, payType);
                    System.out.println("用户" + userId + "支付订单" + orderId + (success ? "成功" : "失败"));
                    
                    if (success) {
                        // 查询该订单的商品，记录商品ID
                        // 注意：实际测试时应替换为实际查询逻辑
                        System.out.println("订单" + orderId + "支付成功，商品应被加入排行榜");
                    }
                } catch (Exception e) {
                    System.err.println("支付异常: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // 等待所有任务完成
        latch.await();
        executor.shutdown();
        
        // 查询并显示排行榜数据
        System.out.println("\n======= 排行榜数据 =======");
        Set<ZSetOperations.TypedTuple<Object>> rankingWithScores = 
            redisTemplate.opsForZSet().reverseRangeWithScores("hot_products", 0, -1);
        
        if (rankingWithScores != null && !rankingWithScores.isEmpty()) {
            int rank = 1;
            for (ZSetOperations.TypedTuple<Object> item : rankingWithScores) {
                System.out.println(String.format("第%d名: 商品ID=%s, 热度=%s", 
                    rank++, item.getValue(), item.getScore()));
            }
        } else {
            System.out.println("排行榜为空，可能是支付都失败了或热度未正确计算");
        }
        
        System.out.println("======= 测试结束 =======");
    }
} 