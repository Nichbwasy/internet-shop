package com.shop.product.service;

import com.shop.product.dto.ProductDto;

public interface ProductService {

    ProductDto getProduct(Long id);
    ProductDto addProduct(ProductDto productDto);
    Long removeProduct(Long id);
    ProductDto updateProduct(ProductDto productDto);

}
