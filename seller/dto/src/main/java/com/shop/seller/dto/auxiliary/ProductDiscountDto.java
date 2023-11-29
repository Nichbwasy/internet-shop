package com.shop.seller.dto.auxiliary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDiscountDto {

    private String name;
    private String description;
    private LocalDateTime createdTime;
    private LocalDateTime activationTime;
    private LocalDateTime endingTime;
    private Float discountValue;

}
