package com.shop.media.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.shop.media",
        "com.shop.common.utils.filter"
})
@EnableTransactionManagement
@EntityScan(basePackages = {"com.shop.media.model"})
@EnableJpaRepositories(basePackages = {"com.shop.media.dao"})
public class RunMediaTestsControllerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunMediaTestsControllerApplication.class);
    }

}
