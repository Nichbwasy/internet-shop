package com.shop.media.service;

import com.shop.media.dto.ProductMediaDto;
import com.shop.media.dto.form.AddMediaToProductForm;
import com.shop.media.dto.form.CreateMediaForProductForm;
import com.shop.media.dto.metadata.ImgMetadataDto;

import java.util.List;

public interface ProductMediaApiService {
    ProductMediaDto createNewMediaForProduct(CreateMediaForProductForm form);
    List<byte[]> loadImagesForProduct(Long productId);
    ProductMediaDto saveProductImage(AddMediaToProductForm form);
    Long removeProductImage(Long productId, Long imageId);
    List<ImgMetadataDto> getProductImagesMetadata(Long productId);
    ImgMetadataDto getProductImageMetadata(Long productId, Long imageId);

}
