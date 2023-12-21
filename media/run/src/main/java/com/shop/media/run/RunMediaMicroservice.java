package com.shop.media.run;

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
@ComponentScan(basePackages = {
        "com.shop.media",
        "com.shop.common.utils.all.config.security",
        "com.shop.common.utils.filter"
})
@EnableTransactionManagement
@EntityScan(basePackages = {"com.shop.media.model"})
@EnableJpaRepositories(basePackages = {"com.shop.media.dao"})
@EnableFeignClients(basePackages = {"com.shop.authorization.client"})
@ImportAutoConfiguration({FeignAutoConfiguration.class})
@Import(DatabaseSchemaInitConfiguration.class)
public class RunMediaMicroservice {

    public static void main(String[] args) {
        SpringApplication.run(RunMediaMicroservice.class, args);
    }

}
