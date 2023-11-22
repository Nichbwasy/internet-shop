package com.shop.product.controller;

import com.shop.product.dto.SubCategoryDto;
import com.shop.product.service.SubCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category/sub")
@RequiredArgsConstructor
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    @GetMapping
    public ResponseEntity<List<SubCategoryDto>> getAllSubCategories() {
        log.info("Trying to get all sub categories...");
        return ResponseEntity.ok().body(subCategoryService.getAllSubCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubCategoryDto> getSubCategory(@PathVariable Long id) {
        log.info("Trying to get sub category with id '{}'...", id);
        return ResponseEntity.ok().body(subCategoryService.getSubCategory(id));
    }

    @PostMapping
    public ResponseEntity<SubCategoryDto> addSubCategory(@RequestBody SubCategoryDto subCategoryDto) {
        log.info("Trying to create a new sub category...");
        return ResponseEntity.ok().body(subCategoryService.addSubCategory(subCategoryDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> removeSubCategory(@PathVariable Long id) {
        log.info("Trying to remove sub category with id '{}'...", id);
        return ResponseEntity.ok().body(subCategoryService.removeSubCategory(id));
    }

    @PutMapping
    public ResponseEntity<SubCategoryDto> updateSubCategory(@RequestBody SubCategoryDto subCategoryDto) {
        log.info("Trying to update sub category...");
        return ResponseEntity.ok().body(subCategoryService.updateSubCategory(subCategoryDto));
    }

}
