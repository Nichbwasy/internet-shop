package com.shop.shop.dto.form.shop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddProductToCartForm {

    private String userAccessToken;
    private Long productId;
    private Integer count;

}
