package com.shop.product.dto.form.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewProductForm {

    private String name;
    private String description;
    private Integer count;
    private BigDecimal price;
    private List<Long> categoryIds;
    private List<Long> discountIds;

}
