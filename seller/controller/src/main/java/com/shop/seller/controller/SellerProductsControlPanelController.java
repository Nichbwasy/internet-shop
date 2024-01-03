package com.shop.seller.controller;

import com.shop.media.dto.ProductMediaDto;
import com.shop.media.dto.form.CreateProductMediaForm;
import com.shop.seller.dto.control.CreateProductForm;
import com.shop.seller.dto.control.SellerProductDetailsDto;
import com.shop.seller.dto.control.UpdateSellerProductForm;
import com.shop.seller.service.SellerProductsControlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/seller/home/products")
@RequiredArgsConstructor
public class SellerProductsControlPanelController {
    private final String BEARER = "Bearer ";

    private final SellerProductsControlService sellerProductsControlService;

    @GetMapping("/{page}")
    public ResponseEntity<List<SellerProductDetailsDto>> showAllSellersProductsDetails(
            @PathVariable Integer page,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        log.info("Trying to show all seller's products...");
        String accessToken = authorization.substring(BEARER.length());
        return ResponseEntity.ok().body(sellerProductsControlService.showAllSellersProducts(page, accessToken));
    }

    @GetMapping("/product/{sellerProductId}")
    public ResponseEntity<SellerProductDetailsDto> showSellerProductDetails(
            @PathVariable Long sellerProductId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        log.info("Trying to get seller's product '{}' details...", sellerProductId);
        String accessToken = authorization.substring(BEARER.length());
        return ResponseEntity.ok().body(sellerProductsControlService.showSellerProduct(sellerProductId, accessToken));
    }

    @PostMapping
    public ResponseEntity<SellerProductDetailsDto> addNewProduct(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody CreateProductForm form
    ) {
        log.info("Trying to create a new product for a seller...");
        String accessToken = authorization.substring(BEARER.length());
        return ResponseEntity.ok().body(sellerProductsControlService.createNewProduct(form, accessToken));
    }

    @PatchMapping("/product/{sellerProductId}")
    public ResponseEntity<SellerProductDetailsDto> updateProduct(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @PathVariable Long sellerProductId,
            @RequestBody UpdateSellerProductForm form
            ) {
        log.info("Trying to update a seller's product with...");
        String accessToken = authorization.substring(BEARER.length());
        form.setSellerProductId(sellerProductId);
        return ResponseEntity.ok().body(sellerProductsControlService.updateSellersProductInfo(accessToken, form));
    }

    @DeleteMapping("/product/{sellerProductId}")
    public ResponseEntity<Long> removeProduct(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @PathVariable Long sellerProductId
    ) {
        log.info("Trying to delete product '{}' from seller...", sellerProductId);
        String accessToken = authorization.substring(BEARER.length());
        return ResponseEntity.ok().body(sellerProductsControlService.removeProduct(sellerProductId, accessToken));
    }

    @GetMapping("/product/{sellerProductId}/imgs")
    public ResponseEntity<List<byte[]>> getAllProductImgs(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                          @PathVariable Long sellerProductId) {
        log.info("Trying to load all images of the '{}' product...", sellerProductId);
        return ResponseEntity.ok().body(sellerProductsControlService.loadProductImgs(accessToken, sellerProductId));
    }

    @PostMapping("/product/{sellerProductId}/imgs")
    public ResponseEntity<ProductMediaDto> getAllProductImgs(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                             @PathVariable Long sellerProductId,
                                                             @ModelAttribute CreateProductMediaForm form) {
        log.info("Trying to add a new image to the '{}' product...", sellerProductId);
        return ResponseEntity.ok().body(sellerProductsControlService.saveImgToProductMedia(accessToken, sellerProductId, form));
    }

    @DeleteMapping("/product/{sellerProductId}/imgs/{imageId}")
    public ResponseEntity<Long> deleteProductImage(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                   @PathVariable Long sellerProductId,
                                                   @PathVariable Long imageId) {
        log.info("Trying to remove the image '{}' from the product '{}'...", imageId, sellerProductId);
        return ResponseEntity.ok().body(sellerProductsControlService.removeProductImage(accessToken, sellerProductId, imageId));
    }

}
