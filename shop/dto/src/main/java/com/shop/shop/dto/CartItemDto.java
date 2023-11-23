package com.shop.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {

    private Long id;
    private UserCartDto userCartDto;
    private Long productId;
    private LocalDateTime additionTime;
    private Integer count;

}
