package com.shop.seller.dto.control;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSellerProductForm {

    private Long sellerProductId;
    private String name;
    private String description;
    private Integer count;
    private BigDecimal price;
    private List<Long> categoryIds = new ArrayList<>();
    private List<Long> discountIds = new ArrayList<>();

}
