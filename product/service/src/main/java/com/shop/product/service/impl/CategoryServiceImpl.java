package com.shop.product.service.impl;

import com.shop.common.utils.all.exception.dao.EntityDeleteRepositoryException;
import com.shop.common.utils.all.exception.dao.EntityNotFoundRepositoryException;
import com.shop.common.utils.all.exception.dao.EntitySaveRepositoryException;
import com.shop.common.utils.all.exception.dao.EntityUpdateRepositoryException;
import com.shop.product.dao.CategoryRepository;
import com.shop.product.dto.CategoryDto;
import com.shop.product.model.Category;
import com.shop.product.service.CategoryService;
import com.shop.product.service.mappers.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> getAllCategories() {
        try {
            List<Category> subCategories = categoryRepository.findAll();
            log.info("{} categories was found.", subCategories.size());
            return subCategories.stream()
                    .map(categoryMapper::mapToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Exception while trying to get all categories! {}", e.getMessage());
            throw new EntityDeleteRepositoryException(
                    "Exception while trying to get all categories! %s".formatted(e.getMessage())
            );
        }
    }

    @Override
    public CategoryDto getCategory(Long id) {
        checkIfCategoryExists(id);

        try {
            log.info("Category with id '{}' has been found", id);
            return categoryMapper.mapToDto(categoryRepository.getReferenceById(id));
        } catch (Exception e) {
            log.warn("Exception while trying to get category with id '{}'! {}", id, e.getMessage());
            throw new EntityDeleteRepositoryException(
                    "Exception while trying to get category with id '%s'! %s".formatted(id, e.getMessage())
            );
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CategoryDto addCategory(CategoryDto categoryDto) {
        try {
            Category category = categoryMapper.mapToModel(categoryDto);
            category = categoryRepository.save(category);
            log.info("New category with id '{}' has been saved successfully.", category.getId());
            return categoryMapper.mapToDto(category);
        } catch (Exception e) {
            log.warn("Unable to save a new category! {}", e.getMessage());
            throw new EntitySaveRepositoryException(
                    "Unable to save a new category! %s".formatted(e.getMessage())
            );
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Long removeCategory(Long id) {
        checkIfCategoryExists(id);

        try {
            categoryRepository.deleteById(id);
            log.info("Category with id '{}' has been removed successfully.", id);
            return id;
        } catch (Exception e) {
            log.warn("Unable remove category with id '{}'!", id);
            throw new EntityDeleteRepositoryException(
                    "Unable remove category with id '%s'!".formatted(id)
            );
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        checkIfCategoryExists(categoryDto.getId());

        try {
            Category category = categoryRepository.getReferenceById(categoryDto.getId());
            log.info("Category with id '{}' has been found to update!'", categoryDto.getId());
            categoryMapper.updateModel(categoryDto, category);
            log.info("Category with id '{}' has been updated successfully.", categoryDto.getId());
            return categoryMapper.mapToDto(category);
        } catch (Exception e) {
            log.warn("Unable update category with id '{}'! {}", categoryDto.getId(), e.getMessage());
            throw new EntityUpdateRepositoryException(
                    "Unable update category with id '%s'! %s".formatted(categoryDto.getId(), e.getMessage())
            );
        }
    }

    private void checkIfCategoryExists(Long id) {
        if (categoryRepository.existsById(id)) {
            log.warn("Unable to find category with id '{}'!", id);
            throw new EntityNotFoundRepositoryException(
                    "Unable to find category with id '%s'!".formatted(id)
            );
        }
    }
}
