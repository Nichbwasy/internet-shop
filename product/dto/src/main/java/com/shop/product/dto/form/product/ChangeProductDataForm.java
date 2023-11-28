package com.shop.product.dto.form.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeProductDataForm {

    private Long productId;
    private String approvalStatus;

}
