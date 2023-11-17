package com.shop.product.service.impl;

import com.shop.common.utils.all.exception.dao.*;
import com.shop.product.dao.SubCategoryRepository;
import com.shop.product.dto.SubCategoryDto;
import com.shop.product.model.SubCategory;
import com.shop.product.service.SubCategoryService;
import com.shop.product.service.mappers.SubCategoryMapper;
import jakarta.validation.Valid;
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
public class SubCategoryServiceImpl implements SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final SubCategoryMapper subCategoryMapper;

    @Override
    public List<SubCategoryDto> getAllSubCategories() {
        try {
            List<SubCategory> subCategories = subCategoryRepository.findAll();
            log.info("{} sub categories was found.", subCategories.size());
            return subCategories.stream()
                    .map(subCategoryMapper::mapToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Exception while trying to get all sub categories! {}", e.getMessage());
            throw new EntityGetRepositoryException(
                    "Exception while trying to get all sub categories! %s".formatted(e.getMessage())
            );
        }
    }

    @Override
    public SubCategoryDto getSubCategory(Long id) {
        try {
            checkIfSubCategoryNotExists(id);

            log.info("Sub category with id '{}' has been found", id);
            return subCategoryMapper.mapToDto(subCategoryRepository.getReferenceById(id));
        } catch (Exception e) {
            log.warn("Exception while trying to get all sub categories! {}", e.getMessage());
            throw new EntityGetRepositoryException(
                    "Exception while trying to get all sub categories! %s".formatted(e.getMessage())
            );
        }
    }

    @Override
    @Transactional
    public SubCategoryDto addSubCategory(SubCategoryDto subCategoryDto) {
        try {
            checkIfSubCategoryAlreadyExits(subCategoryDto.getId(), subCategoryDto.getName());

            @Valid SubCategory subCategory = subCategoryMapper.mapToModel(subCategoryDto);
            subCategory = subCategoryRepository.save(subCategory);
            log.info("New sub category with id '{}' has been saved successfully.", subCategory.getId());
            return subCategoryMapper.mapToDto(subCategory);
        } catch (Exception e) {
            log.warn("Unable to save a new sub category! {}", e.getMessage());
            throw new EntitySaveRepositoryException(
                    "Unable to save a new sub category! %s".formatted(e.getMessage())
            );
        }
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Long removeSubCategory(Long id) {
        try {
            checkIfSubCategoryNotExists(id);

            subCategoryRepository.deleteById(id);
            log.info("Sub category with id '{}' has been removed successfully.", id);
            return id;
        } catch (Exception e) {
            log.warn("Unable remove sub category with id '{}'!", id);
            throw new EntityDeleteRepositoryException(
                    "Unable remove sub category with id '%s'!".formatted(id)
            );
        }
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SubCategoryDto updateSubCategory(SubCategoryDto subCategoryDto) {
        try {
            checkIfSubCategoryNotExists(subCategoryDto.getId());

            SubCategory subCategory = subCategoryRepository.getReferenceById(subCategoryDto.getId());
            log.info("Sub category with id '{}' has been found to update!'", subCategoryDto.getId());
            subCategoryMapper.updateModel(subCategoryDto, subCategory);
            log.info("Sub category with id '{}' has been updated successfully.", subCategoryDto.getId());
            return subCategoryMapper.mapToDto(subCategory);
        } catch (Exception e) {
            log.warn("Unable update sub category with id '{}'! {}", subCategoryDto.getId(), e.getMessage());
            throw new EntityUpdateRepositoryException(
                    "Unable update sub category with id '%s'! %s".formatted(subCategoryDto.getId(), e.getMessage())
            );
        }
    }

    private void checkIfSubCategoryNotExists(Long id) {
        if (!subCategoryRepository.existsById(id)) {
            log.warn("Unable to find sub category with id '{}'!", id);
            throw new EntityNotFoundRepositoryException(
                    "Unable to find sub category with id '%s'!".formatted(id)
            );
        }
    }

    private void checkIfSubCategoryAlreadyExits(Long id, String name) {
        if (subCategoryRepository.existsByIdOrName(id, name)) {
            log.warn("Sub category '{}' already exists!", name);
            throw new EntityAlreadyExistsException("Sub category '%s' already exists!".formatted(name));
        }
    }
}
