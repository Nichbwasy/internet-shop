package com.shop.media.controller;

import com.shop.media.common.data.builder.AddMediaToProductFormBuilder;
import com.shop.media.dto.ProductMediaDto;
import com.shop.media.dto.form.AddMediaToProductForm;
import com.shop.media.dto.form.CreateMediaForProductForm;
import com.shop.media.dto.metadata.DockMetadataDto;
import com.shop.media.dto.metadata.ImgMetadataDto;
import com.shop.media.service.ProductMediaApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/media/products")
public class ProductMediaApiController {

    private final ProductMediaApiService productService;

    @PostMapping
    public ResponseEntity<ProductMediaDto> createProductMedia(@RequestBody CreateMediaForProductForm form) {
        log.info("Trying to create a media for the product ");
        return ResponseEntity.ok().body(productService.createNewMediaForProduct(form));
    }

    @GetMapping("/{productId}/imgs")
    public ResponseEntity<List<byte[]>> loadAllProductsImages(@PathVariable Long productId) {
        log.info("Trying to load all product's '{}' images...", productId );
        return ResponseEntity.ok().body(productService.loadProductImages(productId));
    }

    @PostMapping("/{productId}/imgs")
    public ResponseEntity<ProductMediaDto> createProductMedia(@PathVariable Long productId,
                                                              @RequestParam("file") MultipartFile file) {
        log.info("Trying to add image to product '{}'...", productId);
        AddMediaToProductForm form = AddMediaToProductFormBuilder.createProductMediaForm()
                .productId(productId)
                .multipartFile(file)
                .build();
        return ResponseEntity.ok().body(productService.saveProductImage(form));
    }

    @DeleteMapping("/{productId}/imgs/{imgId}")
    public ResponseEntity<Long> removeImageFromProduct(@PathVariable Long productId, @PathVariable Long imgId) {
        log.info("Trying to remove image '{}' from the product '{}'...", imgId, productId);
        return ResponseEntity.ok().body(productService.removeProductImage(productId, imgId));
    }

    @GetMapping("/{productId}/imgs/data")
    public ResponseEntity<List<ImgMetadataDto>> getProductImagesMetadata(@PathVariable Long productId) {
        log.info("Trying to get all images metadata for the product '{}'...", productId);
        return ResponseEntity.ok().body(productService.getProductImagesMetadata(productId));
    }

    @GetMapping("/{productId}/imgs/data/{imageId}")
    public ResponseEntity<ImgMetadataDto> getProductImageMetadata(@PathVariable Long productId, @PathVariable Long imageId) {
        log.info("Trying to get image {} metadata of the product {}...", imageId, productId);
        return ResponseEntity.ok().body(productService.getProductImageMetadata(productId, imageId));
    }

    @GetMapping("/{productId}/docks/{dockId}")
    public ResponseEntity<byte[]> loadProductDock(@PathVariable Long productId, @PathVariable Long dockId) {
        log.info("Trying to load document '{}' of the product '{}'...", dockId, productId);
        return ResponseEntity.ok().body(productService.loadProductDock(productId, dockId));
    }

    @GetMapping("/{productId}/docks/data")
    public ResponseEntity<List<DockMetadataDto>> getProductDocksMetadata(@PathVariable Long productId) {
        log.info("Trying to get all documents metadata of the product '{}'...", productId);
        return ResponseEntity.ok().body(productService.getProductDocksMetadata(productId));
    }

    @GetMapping("/{productId}/docks/data/{dockId}")
    public ResponseEntity<DockMetadataDto> getProductDockMetadata(@PathVariable Long productId, @PathVariable Long dockId) {
        log.info("Trying to get document metadata of the product '{}'...", productId);
        return ResponseEntity.ok().body(productService.getProductDockMetadata(productId, dockId));
    }

    @PostMapping(value = "/{productId}/docks", consumes = "multipart/form-data")
    public ResponseEntity<ProductMediaDto> addDockToProduct(@PathVariable Long productId,
                                                            @RequestPart() MultipartFile file) {
        log.info("Trying to add a new document for the product '{}'...", productId);
        AddMediaToProductForm form = AddMediaToProductFormBuilder.createProductMediaForm()
                .productId(productId)
                .multipartFile(file)
                .build();
        return ResponseEntity.ok().body(productService.saveProductDock(form));
    }

    @DeleteMapping("/{productId}/docks/{dockId}")
    public ResponseEntity<Long> removeDockFormProduct(@PathVariable Long productId, @PathVariable Long dockId) {
        log.info("Trying to remove the document '{}' from the product '{}'...", dockId, productId);
        return ResponseEntity.ok().body(productService.removeProductDock(productId, dockId));
    }

}
