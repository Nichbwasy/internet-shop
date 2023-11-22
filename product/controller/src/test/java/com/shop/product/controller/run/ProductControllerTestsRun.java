package com.shop.product.controller.run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.shop.product",
        "com.shop.common.utils.filter"
})
public class ProductControllerTestsRun {

    public static void main(String[] args) {
        SpringApplication.run(ProductControllerTestsRun.class, args);
    }

}
