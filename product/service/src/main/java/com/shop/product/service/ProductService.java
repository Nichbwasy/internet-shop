package com.shop.product.service;

import com.shop.product.dto.ProductDto;
import com.shop.product.dto.form.AddOrRemoveForm;
import com.shop.product.dto.form.product.ApprovalStatusProductFilterForm;
import com.shop.product.dto.form.product.ChangeProductApprovalStatusForm;
import com.shop.product.dto.form.product.NewProductForm;
import com.shop.product.dto.form.product.ProductFilterForm;

import java.util.List;

public interface ProductService {

    ProductDto getProduct(Long id);
    ProductDto addProduct(NewProductForm productForm);
    Long removeProduct(Long id);
    ProductDto updateProduct(ProductDto productDto);
    ProductDto addCategories(AddOrRemoveForm form);
    ProductDto removeCategories(AddOrRemoveForm form);
    ProductDto addDiscounts(AddOrRemoveForm form);
    ProductDto removeDiscounts(AddOrRemoveForm form);

    ProductDto changeProductData(ChangeProductApprovalStatusForm form);
    List<ProductDto> getPageOfFilteredProducts(Integer page, ProductFilterForm form);
    List<ProductDto> getPageOfFilteredApprovalProducts(Integer page, ProductFilterForm form);
    List<ProductDto> getPageOfFilteredApprovalStatusProducts(Integer page, ApprovalStatusProductFilterForm form);
    List<Long> removeProducts(List<Long> ids);
    List<ProductDto> getProductsPageByIds(Integer page, List<Long> ids);
}
