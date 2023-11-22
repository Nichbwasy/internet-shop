package com.shop.product.service.config;

import com.shop.product.dao.DiscountRepository;
import com.shop.product.service.DiscountService;
import com.shop.product.service.impl.DiscountServiceImpl;
import com.shop.product.service.mappers.DiscountMapper;
import com.shop.product.service.mappers.DiscountMapperImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class DiscountServiceContextConfiguration {

    @MockBean
    public DiscountRepository discountRepository;

    @Bean
    public DiscountMapper discountMapper() {
        return new DiscountMapperImpl();
    }

    @Bean
    public DiscountService discountService() {
        return new DiscountServiceImpl(
                discountRepository,
                discountMapper()
        );
    }

}
