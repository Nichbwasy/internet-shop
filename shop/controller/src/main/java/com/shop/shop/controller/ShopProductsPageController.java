package com.shop.shop.controller;


import com.shop.product.dto.form.product.ProductFilterForm;
import com.shop.shop.dto.shop.ShopPageProductInfoDto;
import com.shop.shop.service.ShopProductsPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

}
