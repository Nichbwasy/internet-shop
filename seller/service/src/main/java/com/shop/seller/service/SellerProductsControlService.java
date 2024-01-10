package com.shop.seller.service;

import com.shop.media.dto.ProductMediaDto;
import com.shop.media.dto.form.AddMediaToProductForm;
import com.shop.media.dto.metadata.ImgMetadataDto;
import com.shop.seller.dto.control.CreateProductForm;
import com.shop.seller.dto.control.SellerProductDetailsDto;
import com.shop.seller.dto.control.UpdateSellerProductForm;

import java.util.List;

public interface SellerProductsControlService {
    List<SellerProductDetailsDto> showAllSellersProducts(Integer page, String accessToken);
    SellerProductDetailsDto showSellerProduct(Long productId, String accessToken);
    SellerProductDetailsDto createNewProduct(CreateProductForm form, String accessToken);
    Long removeProduct(Long productId, String accessToken);
    SellerProductDetailsDto updateSellersProductInfo(String accessToken, UpdateSellerProductForm form);
    List<byte[]> loadProductImgs(String accessToken, Long sellerProductId);
    ProductMediaDto saveImgToProductMedia(String accessToken, Long sellerProductId, AddMediaToProductForm form);
    Long removeProductImage(String accessToken, Long sellerProductId, Long imageId);
    List<ImgMetadataDto> getProductImagesMetadata(String accessToken, Long sellerProductId);
    ImgMetadataDto getProductImageMetadata(String accessToken, Long sellerProductId, Long imageId);
}
