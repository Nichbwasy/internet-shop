package com.shop.media.client;

import com.shop.media.dto.ProductMediaDto;
import com.shop.media.dto.form.CreateMediaForProductForm;
import com.shop.media.dto.metadata.DockMetadataDto;
import com.shop.media.dto.metadata.ImgMetadataDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "productMediaApiClient", url = "${microservice.media.url}", path = "/api/media/products")
public interface ProductMediaApiClient {


    @PostMapping
    ResponseEntity<ProductMediaDto> createProductMedia(@RequestBody CreateMediaForProductForm form);

    @GetMapping("/{productId}/imgs")
    ResponseEntity<List<byte[]>> loadAllProductsImages(@PathVariable("productId") Long productId);

    @PostMapping(value = "/{productId}/imgs", consumes = "multipart/form-data")
    ResponseEntity<ProductMediaDto> addImageToProduct(@PathVariable("productId") Long productId,
                                                      @RequestPart("file") MultipartFile file);

    @DeleteMapping("/{productId}/imgs/{imgId}")
    ResponseEntity<Long> removeImageFromProduct(@PathVariable("productId") Long productId,
                                                @PathVariable("imgId") Long imgId);

    @GetMapping("/{productId}/imgs/data")
    ResponseEntity<List<ImgMetadataDto>> getProductImagesMetadata(@PathVariable("productId") Long productId);

    @GetMapping("/{productId}/imgs/data/{imageId}")
    ResponseEntity<ImgMetadataDto> getProductImageMetadata(@PathVariable("productId")Long productId,
                                                           @PathVariable("imageId")Long imageId);

    @GetMapping("/{productId}/docks/{dockId}")
    ResponseEntity<byte[]> loadProductDock(@PathVariable("productId") Long productId,
                                           @PathVariable("dockId") Long dockId);

    @GetMapping("/{productId}/docks/data")
    ResponseEntity<List<DockMetadataDto>> getProductDocksMetadata(@PathVariable("productId") Long productId);

    @GetMapping("/{productId}/docks/data/{dockId}")
    ResponseEntity<DockMetadataDto> getProductDockMetadata(@PathVariable("productId") Long productId,
                                                           @PathVariable("dockId") Long dockId);

    @PostMapping(value = "/{productId}/docks", consumes = "multipart/form-data")
    ResponseEntity<ProductMediaDto> addDockToProduct(@PathVariable("productId") Long productId,
                                                     @RequestPart("file") MultipartFile file);

    @DeleteMapping("/{productId}/docks/{dockId}")
    ResponseEntity<Long> removeDockFormProduct(@PathVariable("productId") Long productId,
                                                          @PathVariable("dockId") Long dockId);

}
