package com.shop.product.run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.shop.product"
})
@EnableJpaRepositories(basePackages = {"com.shop.product.dao"})
@EntityScan(basePackages = {"com.shop.product.model"})
public class RunProductService {

    public static void main(String[] args) {
        SpringApplication.run(RunProductService.class, args);
    }

}
