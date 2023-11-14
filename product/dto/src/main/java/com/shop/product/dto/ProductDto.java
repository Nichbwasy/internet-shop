package com.shop.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long id;
    private String code;
    private String name;
    private String description;
    private LocalDateTime createdTime;
    private Integer count;
    private BigDecimal price;
    private List<CategoryDto> categories;
    private List<DiscountDto> discounts;

}
