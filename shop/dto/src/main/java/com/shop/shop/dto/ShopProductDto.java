package com.shop.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopProductDto {

    public Long id;
    private String code;
    private LocalDateTime creationTime;
    private String approvalStatus;


}
