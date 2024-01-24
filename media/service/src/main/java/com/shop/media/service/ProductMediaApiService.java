package com.shop.media.service;

import com.shop.media.dto.ProductMediaDto;
import com.shop.media.dto.form.AddMediaToProductForm;
import com.shop.media.dto.form.CreateMediaForProductForm;
import com.shop.media.dto.metadata.DockMetadataDto;
import com.shop.media.dto.metadata.ImgMetadataDto;

import java.util.List;

public interface ProductMediaApiService {
    ProductMediaDto createNewMediaForProduct(CreateMediaForProductForm form);

    List<byte[]> loadProductImages(Long productId);
    ProductMediaDto saveProductImage(AddMediaToProductForm form);
    Long removeProductImage(Long productId, Long imageId);
    List<ImgMetadataDto> getProductImagesMetadata(Long productId);
    ImgMetadataDto getProductImageMetadata(Long productId, Long imageId);

    byte[] loadProductDock(Long productId, Long dockId);
    List<DockMetadataDto> getProductDocksMetadata(Long productId);
    DockMetadataDto getProductDockMetadata(Long productId, Long dockId);
    ProductMediaDto saveProductDock(AddMediaToProductForm form);
    Long removeProductDock(Long productId, Long dockId);

}
