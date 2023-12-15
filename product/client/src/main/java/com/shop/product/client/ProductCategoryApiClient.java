package com.shop.product.client;

import com.shop.product.dto.CategoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "productCategoryApiClient", url = "${microservice.product.url}", path = "/api/products/categories")
public interface ProductCategoryApiClient {

    @GetMapping("/selected")
    ResponseEntity<List<CategoryDto>> getCategoriesByIds(@RequestBody List<Long> ids);

}
