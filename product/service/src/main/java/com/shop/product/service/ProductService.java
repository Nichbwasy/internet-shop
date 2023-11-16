package com.shop.product.service;

import com.shop.product.dto.ProductDto;
import com.shop.product.dto.form.AddOrRemoveForm;
import com.shop.product.dto.form.product.NewProductForm;

public interface ProductService {

    ProductDto getProduct(Long id);
    ProductDto addProduct(NewProductForm productForm);
    Long removeProduct(Long id);
    ProductDto updateProduct(ProductDto productDto);
    ProductDto addCategories(AddOrRemoveForm form);
    ProductDto removeCategories(AddOrRemoveForm form);
    ProductDto addDiscount(AddOrRemoveForm form);
    ProductDto removeDiscount(AddOrRemoveForm form);

}
