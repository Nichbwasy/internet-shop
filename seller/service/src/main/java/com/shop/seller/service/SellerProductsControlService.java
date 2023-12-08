package com.shop.seller.service;

import com.shop.seller.dto.control.CreateProductForm;
import com.shop.seller.dto.control.SellerProductDetailsDto;

import java.util.List;

public interface SellerProductsControlService {
    List<SellerProductDetailsDto> showAllSellersProducts(Integer page, String accessToken);
    SellerProductDetailsDto showSellerProduct(Long productId, String accessToken);
    SellerProductDetailsDto createNewProduct(CreateProductForm form, String accessToken);
    Long removeProduct(Long productId, String accessToken);
}
