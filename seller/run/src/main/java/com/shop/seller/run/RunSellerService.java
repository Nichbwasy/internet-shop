package com.shop.seller.run;

import com.shop.common.utils.all.config.dao.DatabaseSchemaInitConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EntityScan(basePackages = {"com.shop.seller.model"})
@EnableJpaRepositories(basePackages = {"com.shop.seller.dao"})
@EnableTransactionManagement
@ComponentScan(basePackages = {
        "com.shop.seller",
        "com.shop.common.utils.all.config.security",
        "com.shop.common.utils.filter"
})
@ImportAutoConfiguration({FeignAutoConfiguration.class})
@EnableFeignClients(basePackages = {
        "com.shop.authorization.client",
        "com.shop.product.client",
        "com.shop.media.client"
})
@Import(DatabaseSchemaInitConfiguration.class)
public class RunSellerService {

    public static void main(String[] args) {
        SpringApplication.run(RunSellerService.class, args);
    }

}
