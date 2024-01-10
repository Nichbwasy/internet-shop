package com.shop.shop.controller;

import com.shop.product.dto.form.product.ApprovalStatusProductFilterForm;
import com.shop.product.dto.form.product.ChangeProductApprovalStatusForm;
import com.shop.shop.dto.shop.ShopPageProductInfoDto;
import com.shop.shop.service.ShopProductsApprovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/control/shop/")
public class ProductsAdminControlPageController {

    private final ShopProductsApprovalService approvalService;

    @GetMapping("/{page}")
    public ResponseEntity<List<ShopPageProductInfoDto>> showProductsWithSpecificStatus(@PathVariable Integer page,
                                                                                       @RequestBody ApprovalStatusProductFilterForm form) {
        log.info("Trying to show shop's filtered products at '{}' page with specific approval status", page);
        return ResponseEntity.ok().body(approvalService.showProductsPage(page, form));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ShopPageProductInfoDto> showProductInfo(@PathVariable Long id) {
        log.info("Trying to show info of the product with id '{}'...", id);
        return ResponseEntity.ok().body(approvalService.showProductInfo(id));
    }

    @PutMapping("/products/{id}/status")
    public ResponseEntity<ShopPageProductInfoDto> changeProductStatus(@PathVariable Long id,
                                                                  @RequestBody ChangeProductApprovalStatusForm form) {
        log.info("Trying to change info of the product with id '{}'...", id);
        form.setProductId(id);
        return ResponseEntity.ok().body(approvalService.changeProductInfo(form));
    }

}
