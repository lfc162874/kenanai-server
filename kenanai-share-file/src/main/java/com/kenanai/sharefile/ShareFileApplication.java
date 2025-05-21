package com.kenanai.sharefile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class})
public class ShareFileApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShareFileApplication.class, args);
    }
}
