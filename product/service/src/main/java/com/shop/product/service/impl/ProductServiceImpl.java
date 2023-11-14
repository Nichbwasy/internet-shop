package com.shop.product.service.impl;

import com.shop.common.utils.all.exception.dao.EntityDeleteRepositoryException;
import com.shop.common.utils.all.exception.dao.EntityNotFoundRepositoryException;
import com.shop.common.utils.all.exception.dao.EntitySaveRepositoryException;
import com.shop.common.utils.all.exception.dao.EntityUpdateRepositoryException;
import com.shop.product.dao.ProductRepository;
import com.shop.product.dto.ProductDto;
import com.shop.product.model.Product;
import com.shop.product.service.ProductService;
import com.shop.product.service.mappers.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductDto getProduct(Long id) {
        checkIfProductExists(id);

        try {
            log.info("Product with id '{}' has been found", id);
            return productMapper.mapToDto(productRepository.getReferenceById(id));
        } catch (Exception e) {
            log.warn("Exception while trying to get product with id '{}'! {}", id, e.getMessage());
            throw new EntityDeleteRepositoryException(
                    "Exception while trying to get product with id '%s'! %s".formatted(id, e.getMessage())
            );
        }
    }

    @Override
    public ProductDto addProduct(ProductDto productDto) {
        try {
            Product product = productMapper.mapToModel(productDto);
            product = productRepository.save(product);
            log.info("New product with id '{}' has been saved successfully.", product.getId());
            return productMapper.mapToDto(product);
        } catch (Exception e) {
            log.warn("Unable to save a new product! {}", e.getMessage());
            throw new EntitySaveRepositoryException(
                    "Unable to save a new product! %s".formatted(e.getMessage())
            );
        }
    }

    @Override
    public Long removeProduct(Long id) {
        checkIfProductExists(id);

        try {
            productRepository.deleteById(id);
            log.info("Product with id '{}' has been removed successfully.", id);
            return id;
        } catch (Exception e) {
            log.warn("Unable remove product with id '{}'!", id);
            throw new EntityDeleteRepositoryException(
                    "Unable remove product with id '%s'!".formatted(id)
            );
        }
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        checkIfProductExists(productDto.getId());

        try {
            Product product = productRepository.getReferenceById(productDto.getId());
            log.info("Product with id '{}' has been found to update!'", product.getId());
            productMapper.updateModel(productDto, product);
            log.info("Product with id '{}' has been updated successfully.", productDto.getId());
            return productMapper.mapToDto(product);
        } catch (Exception e) {
            log.warn("Unable update product with id '{}'! {}", productDto.getId(), e.getMessage());
            throw new EntityUpdateRepositoryException(
                    "Unable update product with id '%s'! %s".formatted(productDto.getId(), e.getMessage())
            );
        }
    }

    private void checkIfProductExists(Long id) {
        if (productRepository.existsById(id)) {
            log.warn("Unable to find product with id '{}'!", id);
            throw new EntityNotFoundRepositoryException(
                    "Unable to find product with id '%s'!".formatted(id)
            );
        }
    }

}
