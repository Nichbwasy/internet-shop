package com.shop.shop.service;

import com.shop.product.dto.form.product.ProductFilterForm;
import com.shop.shop.dto.shop.ShopPageProductInfoDto;

import java.util.List;

public interface ShopProductsPageService {
    List<ShopPageProductInfoDto> showFilteredProductsPage(Integer page, ProductFilterForm form);
}
