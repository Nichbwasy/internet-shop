package com.shop.seller.dto.control;

import com.shop.seller.dto.SellerProductDto;
import com.shop.seller.dto.auxiliary.ProductCategoryDto;
import com.shop.seller.dto.auxiliary.ProductDiscountDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SellerProductDetailsDto extends SellerProductDto {

    private String code;
    private String name;
    private String description;
    private LocalDateTime createdTime;
    private Integer count;
    private BigDecimal price;
    private String approvalStatus;
    private List<ProductCategoryDto> categories;
    private List<ProductDiscountDto> discounts;

}
