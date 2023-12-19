package com.shop.media.run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.shop.media",
        "com.shop.common.utils.all.config.security",
        "com.shop.common.utils.filter"
})
@ImportAutoConfiguration({FeignAutoConfiguration.class})
public class RunMediaMicroservice {

    public static void main(String[] args) {
        SpringApplication.run(RunMediaMicroservice.class, args);
    }

}
