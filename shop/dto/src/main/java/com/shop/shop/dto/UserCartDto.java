package com.shop.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCartDto {

    private Long id;
    private Long userId;
    private List<CartItemDto> cartItemDtos;


}
