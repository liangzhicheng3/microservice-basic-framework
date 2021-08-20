package com.liangzhicheng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class AreaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AreaApplication.class, args);
    }

}
