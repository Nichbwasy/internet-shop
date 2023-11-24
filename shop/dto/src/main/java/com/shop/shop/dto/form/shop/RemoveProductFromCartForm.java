package com.shop.shop.dto.form.shop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoveProductFromCartForm {

    private String accessToken;
    private Long productId;

}
