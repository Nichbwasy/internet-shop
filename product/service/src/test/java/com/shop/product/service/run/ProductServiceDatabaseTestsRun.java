package com.shop.product.service.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.shop.product.model",
        "com.shop.product.dao"
})
public class ProductServiceDatabaseTestsRun {
}
