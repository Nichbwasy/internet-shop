package com.shop.product.service.dao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.shop.product.model",
        "com.shop.product.dao",
})
public class ProductServiceDatabaseTestsRun {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ProductServiceDatabaseTestsRun.class, args);
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        System.out.println();
    }

}
