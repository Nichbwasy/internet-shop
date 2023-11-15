package com.shop.product.controller;

import com.shop.product.dto.ProductDto;
import com.shop.product.dto.form.AddOrRemoveForm;
import com.shop.product.dto.form.product.NewProductForm;
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
    public ResponseEntity<ProductDto> addProduct(@RequestBody NewProductForm productForm) {
        log.info("Trying to add a new product...");
        return ResponseEntity.ok().body(productService.addProduct(productForm));
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

    @PostMapping("/new/category")
    public ResponseEntity<ProductDto> addCategories(@RequestBody AddOrRemoveForm form) {
        log.info("Trying to add a new categories '{}' to the product with id '{}'...",
                form.getTargetId(), form.getAddedOrRemovedIds());
        return ResponseEntity.ok().body(productService.addCategories(form));
    }

    @PostMapping("/removing/category")
    public ResponseEntity<ProductDto> removeCategory(@RequestBody AddOrRemoveForm form) {
        log.info("Trying to remove categories '{}' from the product with id '{}'.",
                form.getTargetId(), form.getAddedOrRemovedIds());
        return ResponseEntity.ok().body(productService.removeCategories(form));
    }


}
