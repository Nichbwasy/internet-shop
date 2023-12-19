package com.shop.seller.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.shop.seller",
        "com.shop.common.utils.filter"
})
public class RunSellerTestControllerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunSellerTestControllerApplication.class, args);
    }

}
