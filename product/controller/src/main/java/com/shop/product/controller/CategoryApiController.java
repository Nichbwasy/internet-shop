package com.shop.product.controller;

import com.shop.product.dto.CategoryDto;
import com.shop.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/categories")
public class CategoryApiController {

    private final CategoryService categoryService;

    @GetMapping("/selected")
    public ResponseEntity<List<CategoryDto>> getCategoriesByIds(@RequestBody List<Long> ids) {
        log.info("Trying to get categories by ids '{}'...", ids);
        return ResponseEntity.ok().body(categoryService.findCategoriesByIds(ids));
    }

}
