package com.shop.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountDto {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdTime;
    private LocalDateTime activationTime;
    private LocalDateTime endingTime;
    private Float discountValue;

}
