package com.shop.seller.service.mapper;

import com.shop.product.dto.ProductDto;
import com.shop.product.dto.form.product.NewProductForm;
import com.shop.seller.dto.control.CreateProductForm;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreateProductFormMapper {

    NewProductForm mapToNewProductForm(CreateProductForm form);

}
