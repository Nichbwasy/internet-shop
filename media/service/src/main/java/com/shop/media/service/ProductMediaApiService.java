package com.shop.media.service;

import com.shop.media.dto.ProductMediaDto;
import com.shop.media.dto.form.CreateProductMediaForm;

import java.io.InputStream;
import java.util.List;

public interface ProductMediaApiService {

    List<InputStream> loadImagesForProduct(Long productId);
    ProductMediaDto saveProductImage(CreateProductMediaForm form);
    Long removeProductImage(Long productId, Long imageId);

}
