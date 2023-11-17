package com.shop.product.service.impl;

import com.shop.common.utils.all.exception.dao.*;
import com.shop.product.dao.CategoryRepository;
import com.shop.product.dao.SubCategoryRepository;
import com.shop.product.dto.CategoryDto;
import com.shop.product.dto.form.AddOrRemoveForm;
import com.shop.product.model.Category;
import com.shop.product.model.SubCategory;
import com.shop.product.service.CategoryService;
import com.shop.product.service.exception.category.AddingSubCategoryException;
import com.shop.product.service.exception.category.RemovingSubCategoryException;
import com.shop.product.service.mappers.CategoryMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
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
            throw new EntityGetRepositoryException(
                    "Exception while trying to get all categories! %s".formatted(e.getMessage())
            );
        }
    }

    @Override
    public CategoryDto getCategory(Long id) {
        try {
            checkIfCategoryNotExists(id);

            log.info("Category with id '{}' has been found", id);
            return categoryMapper.mapToDto(categoryRepository.getReferenceById(id));
        } catch (Exception e) {
            log.warn("Exception while trying to get category with id '{}'! {}", id, e.getMessage());
            throw new EntityGetRepositoryException(
                    "Exception while trying to get category with id '%s'! %s".formatted(id, e.getMessage())
            );
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CategoryDto addCategory(CategoryDto categoryDto) {
        try {
            checkIfCategoryAlreadyExits(categoryDto.getId(), categoryDto.getName());

            @Valid Category category = categoryMapper.mapToModel(categoryDto);
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
        try {
            checkIfCategoryNotExists(id);

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
        try {
            checkIfCategoryNotExists(categoryDto.getId());

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

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CategoryDto addSubCategory(AddOrRemoveForm form) {
        try {
            checkIfCategoryNotExists(form.getTargetId());

            Category category = categoryRepository.getReferenceById(form.getTargetId());
            List<SubCategory> subCategories = subCategoryRepository.findByIdIn(form.getAddedOrRemovedIds());
            AtomicInteger counter = new AtomicInteger(0);
            subCategories.forEach(sc -> {
                if (category.getSubCategories().stream().noneMatch(csc -> csc.getId().equals(sc.getId()))) {
                    log.info("Sub category '{}' has been added to the '{}' category.", sc.getName(), category.getName());
                    counter.incrementAndGet();
                    category.getSubCategories().add(sc);
                }
            });
            log.info("Added '{}' sub categories to the category '{}'.", counter.get(), category.getName());
            return categoryMapper.mapToDto(category);
        } catch (Exception e) {
            log.error("Unable to add sub categories to category! {}", e.getMessage());
            throw new AddingSubCategoryException(
                    "Unable to add sub categories to category! %s".formatted(e.getMessage())
            );
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CategoryDto removeSubCategory(AddOrRemoveForm form) {
        try {
            checkIfCategoryNotExists(form.getTargetId());

            Category category = categoryRepository.getReferenceById(form.getTargetId());
            List<SubCategory> subCategories = subCategoryRepository.findByIdIn(form.getAddedOrRemovedIds());
            AtomicInteger counter = new AtomicInteger(0);
            category.getSubCategories().removeIf(csc -> {
                if (subCategories.stream().anyMatch(sc -> sc.getId().equals(csc.getId()))) {
                    log.info("Sub category '{}' has been removed from category '{}'.", csc.getName(), category.getName());
                    counter.incrementAndGet();
                    return true;
                }
                return false;
            });
            log.info("Removed '{}' sub categories from '{}' category", counter.get(), category.getName());
            return categoryMapper.mapToDto(category);
        } catch (Exception e) {
            log.error("Unable remove sub category from category! {}", e.getMessage());
            throw new RemovingSubCategoryException(
                    "Unable remove sub category from category! %s".formatted(e.getMessage())
            );
        }
    }

    private void checkIfCategoryNotExists(Long id) {
        if (!categoryRepository.existsById(id)) {
            log.warn("Unable to find category with id '{}'!", id);
            throw new EntityNotFoundRepositoryException(
                    "Unable to find category with id '%s'!".formatted(id)
            );
        }
    }

    private void checkIfCategoryAlreadyExits(Long id, String name) {
        if (categoryRepository.existsByIdOrName(id, name)) {
            log.warn("Category '{}' already exists!", name);
            throw new EntityAlreadyExistsException("Category '%s' already exists!".formatted(name));
        }
    }
}
