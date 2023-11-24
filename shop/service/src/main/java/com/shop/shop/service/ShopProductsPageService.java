package com.shop.shop.service;

import com.shop.product.dto.form.product.ProductFilterForm;
import com.shop.shop.dto.UserCartDto;
import com.shop.shop.dto.form.shop.AddProductToCartForm;
import com.shop.shop.dto.form.shop.RemoveProductFromCartForm;
import com.shop.shop.dto.shop.ShopPageProductInfoDto;

import java.util.List;

public interface ShopProductsPageService {
    List<ShopPageProductInfoDto> showFilteredProductsPage(Integer page, ProductFilterForm form);
    ShopPageProductInfoDto showProductInfo(Long productId);
    UserCartDto addProductToCart(AddProductToCartForm form);
    UserCartDto removeProductFromCart(RemoveProductFromCartForm form);
}
