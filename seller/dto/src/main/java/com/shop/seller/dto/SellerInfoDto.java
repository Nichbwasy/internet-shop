package com.shop.seller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerInfoDto {

    private Long id;
    private Long userId;
    private LocalDateTime registrationDate;
    private Float rating;
    private String description;
    private List<SellerProductDto> products;

}
