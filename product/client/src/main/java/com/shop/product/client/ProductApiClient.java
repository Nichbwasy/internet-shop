package com.shop.product.client;

import com.shop.product.dto.ProductDto;
import com.shop.product.dto.form.product.ApprovalStatusProductFilterForm;
import com.shop.product.dto.form.product.ChangeProductDataForm;
import com.shop.product.dto.form.product.NewProductForm;
import com.shop.product.dto.form.product.ProductFilterForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "productApiClient", url = "${microservice.product.url}", path = "/api/products")
public interface ProductApiClient {

    @GetMapping("/product/{id}")
    ResponseEntity<ProductDto> getProduct(@PathVariable("id") Long id);

    @PutMapping("/product/changes")
    ResponseEntity<ProductDto> updateProductData(@RequestBody ChangeProductDataForm form);

    @PostMapping("/{page}")
    ResponseEntity<List<ProductDto>> getFilteredApprovedProducts(@PathVariable("page") Integer page,
                                                         @RequestBody ProductFilterForm form);

    @PostMapping("/approval/{page}")
    ResponseEntity<List<ProductDto>> getFilteredApprovalStatusProducts(@PathVariable("page") Integer page,
                                                                 @RequestBody ApprovalStatusProductFilterForm form);

    @DeleteMapping("/removing")
    ResponseEntity<List<Long>> removeProducts(@RequestBody List<Long> ids);

    @GetMapping("/ids/{page}")
    ResponseEntity<List<ProductDto>> getProductsByIds(@PathVariable("page") Integer page, @RequestBody List<Long> ids);

    @PostMapping
    ResponseEntity<ProductDto> createProduct(@RequestBody NewProductForm form);

    @DeleteMapping("/product/{id}")
    ResponseEntity<Long> removeProduct(@PathVariable("id") Long id);
}
