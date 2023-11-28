package com.shop.product.controller;

import com.shop.product.dto.ProductDto;
import com.shop.product.dto.form.product.ApprovalStatusProductFilterForm;
import com.shop.product.dto.form.product.ChangeProductDataForm;
import com.shop.product.dto.form.product.ProductFilterForm;
import com.shop.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductApiController {

    private final ProductService productService;

    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        log.info("Trying to get the product with id '{}'...", id);
        return ResponseEntity.ok().body(productService.getProduct(id));
    }

    @PutMapping("/product/changes")
    public ResponseEntity<ProductDto> updateProductApprovalStatus(@RequestBody ChangeProductDataForm form) {
        log.info("Trying to change the product with id '{}'...", form.getProductId());
        return ResponseEntity.ok().body(productService.changeProductData(form));
    }

    @PostMapping("/{page}")
    public ResponseEntity<List<ProductDto>> getApprovedFilteredProducts(@PathVariable Integer page,
                                                                @RequestBody ProductFilterForm form) {
        log.info("Trying to get the page '{}' of filtered products...", page);
        return ResponseEntity.ok().body(productService.getPageOfFilteredApprovalProducts(page, form));
    }

    @PostMapping("/approval/{page}")
    public ResponseEntity<List<ProductDto>> getApprovedStatusFilteredProducts(@PathVariable Integer page,
                                                                        @RequestBody ApprovalStatusProductFilterForm form) {
        log.info("Trying to get the page '{}' of filtered products...", page);
        return ResponseEntity.ok().body(productService.getPageOfFilteredApprovalStatusProducts(page, form));
    }

}
