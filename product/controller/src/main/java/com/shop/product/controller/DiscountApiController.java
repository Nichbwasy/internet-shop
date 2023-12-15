package com.shop.product.controller;

import com.shop.product.dto.DiscountDto;
import com.shop.product.service.DiscountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/discounts")
public class DiscountApiController {

    private final DiscountService discountService;

    @GetMapping("/selected")
    public ResponseEntity<List<DiscountDto>> getDiscountsByIds(@RequestBody List<Long> ids) {
        log.info("Trying to get discounts with selected ids '{}'...", ids);
        return ResponseEntity.ok().body(discountService.findDiscountsByIds(ids));
    }

}
