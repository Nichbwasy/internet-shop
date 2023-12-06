package com.shop.seller.dto.control;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductForm {

    private String name;
    private String description;
    private Integer count;
    private BigDecimal price;
    private List<Long> categoryIds = new ArrayList<>();
    private List<Long> discountIds = new ArrayList<>();

}
