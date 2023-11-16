package com.shop.product.service.impl;

import com.shop.common.utils.all.exception.dao.EntityDeleteRepositoryException;
import com.shop.common.utils.all.exception.dao.EntityNotFoundRepositoryException;
import com.shop.common.utils.all.exception.dao.EntitySaveRepositoryException;
import com.shop.common.utils.all.exception.dao.EntityUpdateRepositoryException;
import com.shop.common.utils.all.generator.StringGenerator;
import com.shop.product.dao.CategoryRepository;
import com.shop.product.dao.DiscountRepository;
import com.shop.product.dao.ProductRepository;
import com.shop.product.dto.ProductDto;
import com.shop.product.dto.form.AddOrRemoveForm;
import com.shop.product.dto.form.product.NewProductForm;
import com.shop.product.model.Category;
import com.shop.product.model.Discount;
import com.shop.product.model.Product;
import com.shop.product.service.ProductService;
import com.shop.product.service.exception.product.AddingCategoryException;
import com.shop.product.service.exception.product.AddingDiscountException;
import com.shop.product.service.exception.product.RemovingDiscountException;
import com.shop.product.service.mappers.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final DiscountRepository discountRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductDto getProduct(Long id) {
        checkIfProductNotExists(id);

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
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ProductDto addProduct(NewProductForm productForm) {
        try {
            Product product = productMapper.mapProductFormToModel(productForm);
            List<Category> categories = categoryRepository.findByIdIn(productForm.getCategoryIds());
            List<Discount> discounts = discountRepository.findByIdIn(productForm.getDiscountIds());
            product.setCategories(categories);
            product.setDiscounts(discounts);
            product.setCreatedTime(LocalDateTime.now());
            product.setCode(StringGenerator.generate(64));

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
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Long removeProduct(Long id) {
        checkIfProductNotExists(id);

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
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ProductDto updateProduct(ProductDto productDto) {
        checkIfProductNotExists(productDto.getId());

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

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ProductDto addCategories(AddOrRemoveForm form) {
        checkIfProductNotExists(form.getTargetId());

        try {
            Product product = productRepository.getReferenceById(form.getTargetId());
            List<Category> categories = categoryRepository.findByIdIn(form.getAddedOrRemovedIds());
            AtomicInteger counter = new AtomicInteger(0);
            categories.stream()
                    .filter(c -> product.getCategories().stream().noneMatch(pc -> pc.getId().equals(c.getId())))
                    .forEach(c -> {
                        log.info("Category '{}' has been added to the product '{}'.", c.getName(), product.getName());
                        product.getCategories().add(c);
                        counter.incrementAndGet();
                    });
            log.info("Added '{}' categories to the product '{}'.", counter.get(), product.getName());
            return productMapper.mapToDto(product);
        } catch (Exception e) {
            log.info("Unable to add new categories to product! {}", e.getMessage());
            throw new AddingCategoryException(
                    "Unable to add new categories to product! %s".formatted(e.getMessage())
            );
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ProductDto removeCategories(AddOrRemoveForm form) {
        checkIfProductNotExists(form.getTargetId());

        try {
            Product product = productRepository.getReferenceById(form.getTargetId());
            List<Category> categories = categoryRepository.findByIdIn(form.getAddedOrRemovedIds());
            AtomicInteger counter = new AtomicInteger(0);
            product.getCategories().removeIf(pc -> {
                if (categories.stream().anyMatch(c -> c.getId().equals(pc.getId()))) {
                    log.info("Category '{}' has been removed from product '{}'.", pc.getName(), product.getName());
                    counter.incrementAndGet();
                    return true;
                }
                return false;
            });
            log.info("Removed '{}' categories from the product '{}'.", counter.get(), product.getName());
            return productMapper.mapToDto(product);
        } catch (Exception e) {
            log.info("Unable to remove categories from product! {}", e.getMessage());
            throw new AddingCategoryException(
                    "Unable to remove categories from product! %s".formatted(e.getMessage())
            );
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ProductDto addDiscount(AddOrRemoveForm form) {
        checkIfProductNotExists(form.getTargetId());

        try {
            Product product = productRepository.getReferenceById(form.getTargetId());
            List<Discount> discounts = discountRepository.findByIdIn(form.getAddedOrRemovedIds());
            AtomicInteger counter = new AtomicInteger(0);
            discounts.stream()
                    .filter(ds -> product.getDiscounts().stream()
                            .noneMatch(pds -> pds.getId().equals(ds.getId())))
                    .forEach(ds -> {
                        log.info("Discount '{}' has been added to the product '{}'.", ds.getName(), product.getName());
                        product.getDiscounts().add(ds);
                        counter.incrementAndGet();
                    });
            log.info("Added '{}' discounts to the product '{}'.", counter.get(), product.getName());
            return productMapper.mapToDto(product);
        } catch (Exception e) {
            log.error("Unable add discounts to product! {}", e.getMessage());
            throw new AddingDiscountException(
                    "Unable add discounts to product! %s".formatted(e.getMessage())
            );
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ProductDto removeDiscount(AddOrRemoveForm form) {
        checkIfProductNotExists(form.getTargetId());

        try {
            Product product = productRepository.getReferenceById(form.getTargetId());
            List<Discount> discounts = discountRepository.findByIdIn(form.getAddedOrRemovedIds());
            AtomicInteger counter = new AtomicInteger(0);
            product.getDiscounts().removeIf(pds -> {
               if (discounts.stream().anyMatch(ds -> ds.getId().equals(pds.getId()))) {
                   log.info("Discount '{}' has been removed from the product '{}'.", pds.getName(), product.getName());
                   counter.incrementAndGet();
                   return true;
               }
               return false;
            });
            log.info("Removed '{}' discounts from the product '{}'.", counter.get(), product.getName());
            return productMapper.mapToDto(product);
        } catch (Exception e) {
            log.error("Unable remove discounts from the product '{}'...", e.getMessage());
            throw new RemovingDiscountException(
                    "Unable remove discounts from the product '%s'...".formatted(e.getMessage())
            );
        }
    }

    private void checkIfProductNotExists(Long id) {
        if (!productRepository.existsById(id)) {
            log.warn("Unable to find product with id '{}'!", id);
            throw new EntityNotFoundRepositoryException(
                    "Unable to find product with id '%s'!".formatted(id)
            );
        }
    }

}
