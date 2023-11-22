package com.shop.product.controller;

import com.shop.product.dto.DiscountDto;
import com.shop.product.service.DiscountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/discount")
public class DiscountController {

    private final DiscountService discountService;

    @GetMapping
    public ResponseEntity<List<DiscountDto>> getAllDiscounts() {
        log.info("Trying to get all discounts...");
        return ResponseEntity.ok().body(discountService.getAllDiscounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiscountDto> getDiscount(@PathVariable Long id) {
        log.info("Trying to get discount with id '{}'...", id);
        return ResponseEntity.ok().body(discountService.getDiscount(id));
    }

    @PostMapping
    public ResponseEntity<DiscountDto> addDiscount(@RequestBody DiscountDto discountDto) {
        log.info("Trying to create a new discount...");
        return ResponseEntity.ok().body(discountService.addDiscount(discountDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> removeDiscount(@PathVariable Long id) {
        log.info("Trying to remove discount with id '{}'...", id);
        return ResponseEntity.ok().body(discountService.removeDiscount(id));
    }

    @PutMapping
    public ResponseEntity<DiscountDto> updateDiscount(@RequestBody DiscountDto discountDto) {
        log.info("Trying to update discount...");
        return ResponseEntity.ok().body(discountService.updateDiscount(discountDto));
    }

}
