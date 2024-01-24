package com.shop.shop.controller;


import com.shop.product.dto.form.product.ProductFilterForm;
import com.shop.shop.dto.UserCartDto;
import com.shop.shop.dto.form.shop.AddProductToCartForm;
import com.shop.shop.dto.form.shop.RemoveProductFromCartForm;
import com.shop.shop.dto.shop.ShopPageProductInfoDto;
import com.shop.shop.service.ShopProductsPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopProductsPageController {

    private final ShopProductsPageService shopProductsPageService;

    @GetMapping("/{page}")
    public ResponseEntity<List<ShopPageProductInfoDto>> showShopProductPage(@PathVariable Integer page,
                                                                            @RequestBody ProductFilterForm form) {
        log.info("Trying to show the page '{}' of shop products...", page);
        return ResponseEntity.ok().body(shopProductsPageService.showFilteredProductsPage(page, form));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ShopPageProductInfoDto> showProductsDetails(@PathVariable Long id) {
        log.info("Trying to get info about the product with id '{}'...", id);
        return ResponseEntity.ok().body(shopProductsPageService.showProductInfo(id));
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<UserCartDto> addProductToUserCart(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
                                                            @RequestBody AddProductToCartForm form,
                                                            @PathVariable Long productId) {
        log.info("Trying to add the product '{}' to the user...", form.getProductId());
        form.setUserAccessToken(auth.substring("Bearer ".length()));
        form.setProductId(productId);
        return ResponseEntity.ok().body(shopProductsPageService.addProductToCart(form));
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<UserCartDto> removeProductFromUserCart(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
                                                                 @RequestBody RemoveProductFromCartForm form,
                                                                 @PathVariable Long productId) {
        log.info("Trying to add the product '{}' to the user...", form.getProductId());
        form.setAccessToken(auth.substring("Bearer ".length()));
        form.setProductId(productId);
        return ResponseEntity.ok().body(shopProductsPageService.removeProductFromCart(form));
    }

}
