package com.kenanai.shop;

import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.*;

public class UserRegisterTest {

    private static final String REGISTER_URL = "http://localhost:9999/user/register";
    private static final int USER_COUNT = 100;

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void testRegisterUsersConcurrently() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10); // 控制并发线程数
        CountDownLatch latch = new CountDownLatch(USER_COUNT); // 用于等待所有任务完成

        for (int i = 1; i <= USER_COUNT; i++) {
            int index = i;
            executor.submit(() -> {
                try {
                    Map<String, Object> requestBody = new HashMap<>();
                    requestBody.put("username", "testuser" + index);
                    requestBody.put("password", "123456");
                    requestBody.put("email", "user" + index + "@test.com");

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

                    ResponseEntity<String> response = restTemplate.postForEntity(REGISTER_URL, entity, String.class);
                    System.out.println("用户" + index + " 注册结果: " + response.getStatusCode() + " - " + response.getBody());

                } catch (Exception e) {
                    System.err.println("用户" + index + " 注册异常: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 等待所有线程完成
        executor.shutdown();
        System.out.println("全部注册任务完成");
    }
}
