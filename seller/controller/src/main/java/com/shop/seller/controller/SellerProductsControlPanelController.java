package com.shop.seller.controller;

import com.shop.seller.dto.control.CreateProductForm;
import com.shop.seller.dto.control.SellerProductDetailsDto;
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

    @GetMapping("/product/{id}")
    public ResponseEntity<SellerProductDetailsDto> showSellerProductDetails(
            @PathVariable Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        log.info("Trying to get seller's product '{}' details...", id);
        String accessToken = authorization.substring(BEARER.length());
        return ResponseEntity.ok().body(sellerProductsControlService.showSellerProduct(id, accessToken));
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


}
