package com.shop.seller.controller;

import com.shop.media.dto.ProductMediaDto;
import com.shop.media.dto.form.AddMediaToProductForm;
import com.shop.media.dto.metadata.DockMetadataDto;
import com.shop.seller.service.SellerProductsControlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/seller/home/products/{sellerProductId}/docs")
public class SellerProductDocumentsControlPageController {

    private final SellerProductsControlService productsControlService;

    @GetMapping("/{dockId}")
    public ResponseEntity<byte[]> loadProductDockFile(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                      @PathVariable Long sellerProductId,
                                                      @PathVariable Long dockId) {
        log.info("Trying to load document '{}' of the product '{}'...", dockId, sellerProductId);
        return ResponseEntity.ok().body(productsControlService.loadProductDock(accessToken, sellerProductId, dockId));
    }

    @GetMapping("/data")
    public ResponseEntity<List<DockMetadataDto>> getAllSellersProductDocksMetadata(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                                                   @PathVariable Long sellerProductId) {
        log.info("Trying to load all seller's product docks '{}' metadata...", sellerProductId);
        return ResponseEntity.ok().body(productsControlService.getProductDocksMetadata(accessToken, sellerProductId));
    }

    @GetMapping("/data/{dockId}")
    public ResponseEntity<DockMetadataDto> getSellersProductDockMetadata(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                                         @PathVariable Long sellerProductId,
                                                                         @PathVariable Long dockId) {
        log.info("Trying to load all seller's product docks '{}' metadata...", sellerProductId);
        return ResponseEntity.ok().body(productsControlService.getProductDockMetadata(accessToken, sellerProductId, dockId));
    }

    @PostMapping
    public ResponseEntity<ProductMediaDto> uploadProductDock(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                             @PathVariable Long sellerProductId,
                                                             @ModelAttribute("form") AddMediaToProductForm form) {
        log.info("Trying to upload a new document to the product '{}'...", sellerProductId);
        return ResponseEntity.ok().body(productsControlService.saveDockToProductMedia(accessToken, sellerProductId, form));
    }

    @DeleteMapping("/{dockId}")
    public ResponseEntity<Long> deleteProductDock(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                  @PathVariable Long sellerProductId,
                                                  @PathVariable Long dockId) {
        log.info("Trying to remove dock '{}' from the product '{}'...", dockId, sellerProductId);
        return ResponseEntity.ok().body(productsControlService.removeProductDock(accessToken, sellerProductId, dockId));
    }


}
