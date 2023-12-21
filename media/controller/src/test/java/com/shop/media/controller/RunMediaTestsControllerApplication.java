package com.shop.media.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.shop.media",
        "com.shop.common.utils.filter"
})
public class RunMediaTestsControllerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunMediaTestsControllerApplication.class);
    }

}
