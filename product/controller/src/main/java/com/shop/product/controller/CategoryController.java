package com.shop.product.controller;

import com.shop.product.dto.CategoryDto;
import com.shop.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        log.info("Trying to get all categories...");
        return ResponseEntity.ok().body(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Long id) {
        log.info("Trying to get category with id '{}'...", id);
        return ResponseEntity.ok().body(categoryService.getCategory(id));
    }

    @PostMapping
    public ResponseEntity<CategoryDto> addCategory(@RequestBody CategoryDto categoryDto) {
        log.info("Trying to add a new category...");
        return ResponseEntity.ok().body(categoryService.addCategory(categoryDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> removeCategory(@PathVariable Long id) {
        log.info("Trying to remove a category with id '{}'...", id);
        return ResponseEntity.ok().body(categoryService.removeCategory(id));
    }

    @PutMapping
    public ResponseEntity<CategoryDto> updateCategory(@RequestBody CategoryDto categoryDto) {
        log.info("Trying to update category...");
        return ResponseEntity.ok().body(categoryService.updateCategory(categoryDto));
    }

}
