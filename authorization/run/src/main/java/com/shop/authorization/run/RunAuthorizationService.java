package com.shop.authorization.run;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.shop.authorization.dao"})
@EntityScan(basePackages = {"com.shop.authorization.model"})
@EnableTransactionManagement
public class RunAuthorizationService {

    public static void main(String[] args) {
        SpringApplication.run(RunAuthorizationService.class);
    }

}
