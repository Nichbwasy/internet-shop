package com.shop.seller.dto.auxiliary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryDto {

    private String name;
    private List<ProductSubCategoryDto> subCategories;

}
