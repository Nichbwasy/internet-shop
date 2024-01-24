package com.shop.seller.controller;

import com.shop.media.dto.ProductMediaDto;
import com.shop.media.dto.form.AddMediaToProductForm;
import com.shop.media.dto.metadata.ImgMetadataDto;
import com.shop.seller.service.SellerProductsControlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/seller/home/products/{sellerProductId}/imgs")
public class SellerProductImagesControlPageController {

    private final SellerProductsControlService productsControlService;

    @GetMapping
    public ResponseEntity<List<byte[]>> getAllProductImgs(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                          @PathVariable Long sellerProductId) {
        log.info("Trying to load all images of the '{}' product...", sellerProductId);
        return ResponseEntity.ok().body(productsControlService.loadProductImgs(accessToken, sellerProductId));
    }

    @PostMapping
    public ResponseEntity<ProductMediaDto> uploadImage(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                             @PathVariable Long sellerProductId,
                                                             @ModelAttribute("form") AddMediaToProductForm form) {
        log.info("Trying to add a new image to the '{}' product...", sellerProductId);
        return ResponseEntity.ok().body(productsControlService.saveImgToProductMedia(accessToken, sellerProductId, form));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Long> deleteProductImage(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                   @PathVariable Long sellerProductId,
                                                   @PathVariable Long imageId) {
        log.info("Trying to remove the image '{}' from the product '{}'...", imageId, sellerProductId);
        return ResponseEntity.ok().body(productsControlService.removeProductImage(accessToken, sellerProductId, imageId));
    }

    @GetMapping("/data")
    public ResponseEntity<List<ImgMetadataDto>> getProductImagesMetadata(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                                         @PathVariable Long sellerProductId) {
        log.info("Trying get metadata of all images for the seller's product '{}'...", sellerProductId);
        return ResponseEntity.ok().body(productsControlService.getProductImagesMetadata(accessToken, sellerProductId));
    }

    @GetMapping("/data/{imageId}")
    public ResponseEntity<ImgMetadataDto> getProductImageMetadata(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                                  @PathVariable Long sellerProductId,
                                                                  @PathVariable Long imageId) {
        log.info("Trying to get metadata of image '{}' from the product '{}'...", imageId, sellerProductId);
        return ResponseEntity.ok().body(productsControlService.getProductImageMetadata(accessToken, sellerProductId, imageId));
    }

}
