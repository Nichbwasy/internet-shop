package com.shop.product.client;

import com.shop.product.dto.ProductDto;
import com.shop.product.dto.form.product.ProductFilterForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "productApiClient", url = "${microservice.product.url}", path = "/api/products")
public interface ProductApiClient {

    @PostMapping("/{page}")
    ResponseEntity<List<ProductDto>> getFilteredApprovedProducts(@PathVariable("page") Integer page,
                                                         @RequestBody ProductFilterForm form);

}
