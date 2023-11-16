package com.shop.product.controller;

import com.shop.product.dto.ProductDto;
import com.shop.product.dto.form.AddOrRemoveForm;
import com.shop.product.dto.form.product.NewProductForm;
import com.shop.product.dto.form.product.ProductFilterForm;
import com.shop.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/{page}")
    public ResponseEntity<List<ProductDto>> getProductsPage(
            @PathVariable Integer page,
            @RequestBody ProductFilterForm form
    ) {
        log.info("Trying to get products page '{}'...", page);
        return ResponseEntity.ok().body(productService.getPageOfFilteredProducts(page, form));
    }

    @GetMapping("/product/{id}")
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

    @DeleteMapping("/removing/category")
    public ResponseEntity<ProductDto> removeCategory(@RequestBody AddOrRemoveForm form) {
        log.info("Trying to remove categories '{}' from the product with id '{}'.",
                form.getTargetId(), form.getAddedOrRemovedIds());
        return ResponseEntity.ok().body(productService.removeCategories(form));
    }

    @PostMapping("/new/discount")
    public ResponseEntity<ProductDto> addDiscounts(@RequestBody AddOrRemoveForm form) {
        log.info("Trying to add a new discounts '{}' to the product with id '{}'...",
                form.getTargetId(), form.getAddedOrRemovedIds());
        return ResponseEntity.ok().body(productService.addDiscount(form));
    }

    @DeleteMapping("/removing/discount")
    public ResponseEntity<ProductDto> removeDiscounts(@RequestBody AddOrRemoveForm form) {
        log.info("Trying to remove discounts '{}' from the oriduct with id '{}'...",
                form.getTargetId(), form.getAddedOrRemovedIds());
        return ResponseEntity.ok().body(productService.removeDiscount(form));
    }


}
