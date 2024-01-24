package com.shop.order.run;

import com.shop.common.utils.all.config.dao.DatabaseSchemaInitConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.shop.order"
})
@EnableTransactionManagement
@EntityScan(basePackages = {"com.shop.order.model"})
@EnableJpaRepositories(basePackages = {"com.shop.order.dao"})
@Import(DatabaseSchemaInitConfiguration.class)
public class RunOrderMicroservice {

    public static void main(String[] args) {
        SpringApplication.run(RunOrderMicroservice.class, args);
    }

}
