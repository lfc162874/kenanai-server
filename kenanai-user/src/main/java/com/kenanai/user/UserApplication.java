package com.kenanai.user;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 用户服务应用入口
 */
@SpringBootApplication
@EnableDubbo
@MapperScan("com.kenanai.user.mapper")
@ComponentScan({"com.kenanai.user", "com.kenanai.common"})
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
} 