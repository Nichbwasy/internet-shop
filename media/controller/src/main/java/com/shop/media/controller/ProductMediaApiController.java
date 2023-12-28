package com.shop.media.controller;

import com.shop.media.dto.ProductMediaDto;
import com.shop.media.dto.form.CreateProductMediaForm;
import com.shop.media.service.ProductMediaApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/media/products")
public class ProductMediaApiController {

    private final ProductMediaApiService productService;

    @GetMapping("/{productId}/imgs")
    public ResponseEntity<List<byte[]>> getAllProductsImages(@PathVariable Long productId) {
        log.info("Trying to load all product's '{}' images...", productId );
        return ResponseEntity.ok().body(productService.loadImagesForProduct(productId));
    }

    @PostMapping("/{productId}/imgs")
    public ResponseEntity<ProductMediaDto> createProductMedia(@PathVariable Long productId,
                                                              @ModelAttribute CreateProductMediaForm form) {
        log.info("Trying to add image to product '{}'...", form.getProductId());
        form.setProductId(productId);
        return ResponseEntity.ok().body(productService.saveProductImage(form));
    }

    @DeleteMapping("/{productId}/imgs/{imgId}")
    public ResponseEntity<Long> removeImageFromProduct(@PathVariable Long productId, @PathVariable Long imgId) {
        log.info("Trying to remove image '{}' from the product '{}'...", imgId, productId);
        return ResponseEntity.ok().body(productService.removeProductImage(productId, imgId));
    }

}
