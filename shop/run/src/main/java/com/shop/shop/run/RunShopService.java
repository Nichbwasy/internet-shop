package com.shop.shop.run;

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
@EntityScan(basePackages = {"com.shop.shop.model"})
@EnableJpaRepositories(basePackages = {"com.shop.shop.dao"})
@EnableFeignClients(basePackages = {"com.shop.product.client", "com.shop.authorization.client"})
@ImportAutoConfiguration({FeignAutoConfiguration.class})
@ComponentScan(basePackages = {
        "com.shop.shop",
        "com.shop.common.utils.all.config.security"
        //"com.shop.common.utils.filter"
})
@EnableTransactionManagement
@Import(DatabaseSchemaInitConfiguration.class)
public class RunShopService {

    public static void main(String[] args) {
        SpringApplication.run(RunShopService.class, args);
    }

}
