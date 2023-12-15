package com.shop.product.service;

import com.shop.product.dto.DiscountDto;

import java.util.List;

public interface DiscountService {

    List<DiscountDto> getAllDiscounts();
    DiscountDto getDiscount(Long id);
    DiscountDto addDiscount(DiscountDto discountDto);
    Long removeDiscount(Long id);
    DiscountDto updateDiscount(DiscountDto discountDto);
    List<DiscountDto> findDiscountsByIds(List<Long> ids);

}
