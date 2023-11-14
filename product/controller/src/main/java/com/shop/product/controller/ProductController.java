package com.shop.product.controller;

import com.shop.product.dto.ProductDto;
import com.shop.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        log.info("Trying to get product with id '{}'...", id);
        return ResponseEntity.ok().body(productService.getProduct(id));
    }

    @PostMapping
    public ResponseEntity<ProductDto> addProduct(@RequestBody ProductDto productDto) {
        log.info("Trying to add a new product...");
        return ResponseEntity.ok().body(productService.addProduct(productDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> removeProduct(@PathVariable Long id) {
        log.info("Trying to remove product with id '{}'...",id);
        return ResponseEntity.ok().body(productService.removeProduct(id));
    }

    @PutMapping
    public ResponseEntity<ProductDto> updateProduct(@RequestBody ProductDto productDto) {
        log.info("Trying to update a product...");
        return ResponseEntity.ok().body(productService.updateProduct(productDto));
    }


}
