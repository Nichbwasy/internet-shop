package com.shop.media.client;

import com.shop.media.dto.ProductMediaDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "productMediaApiClient", url = "${microservice.media.url}", path = "/api/media/products")
public interface ProductMediaApiClient {

    @GetMapping("/{productId}/imgs")
    ResponseEntity<List<byte[]>> getAllProductsImages(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                      @PathVariable("productId") Long productId);

    @PostMapping(value = "/{productId}/imgs", consumes = "multipart/form-data")
    ResponseEntity<ProductMediaDto> createProductMedia(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                       @PathVariable("productId") Long productId,
                                                       @RequestPart("file") MultipartFile file);

    @DeleteMapping("/{productId}/imgs/{imgId}")
    ResponseEntity<Long> removeImageFromProduct(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                @PathVariable("productId") Long productId,
                                                @PathVariable("imgId") Long imgId);

}
