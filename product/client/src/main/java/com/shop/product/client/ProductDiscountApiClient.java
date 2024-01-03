package com.shop.product.client;

import com.shop.product.dto.DiscountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "productDiscountApiClient", url = "${microservice.product.url}", path = "/api/products/discounts")
public interface ProductDiscountApiClient {

    @PostMapping("/selected")
    ResponseEntity<List<DiscountDto>> getDiscountsByIds(@RequestBody List<Long> ids);

}
