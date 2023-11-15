package com.shop.product.service.impl;

import com.shop.common.utils.all.exception.dao.EntityDeleteRepositoryException;
import com.shop.common.utils.all.exception.dao.EntityNotFoundRepositoryException;
import com.shop.common.utils.all.exception.dao.EntitySaveRepositoryException;
import com.shop.common.utils.all.exception.dao.EntityUpdateRepositoryException;
import com.shop.product.dao.DiscountRepository;
import com.shop.product.dto.DiscountDto;
import com.shop.product.model.Discount;
import com.shop.product.service.DiscountService;
import com.shop.product.service.mappers.DiscountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscountServiceImpl implements DiscountService {
    
    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;

    @Override
    public List<DiscountDto> getAllDiscounts() {
        try {
            List<Discount> discounts = discountRepository.findAll();
            log.info("{} discounts was found.", discounts.size());
            return discounts.stream()
                    .map(discountMapper::mapToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Exception while trying to get all discounts! {}", e.getMessage());
            throw new EntityDeleteRepositoryException(
                    "Exception while trying to get all discounts! %s".formatted(e.getMessage())
            );
        }
    }

    @Override
    public DiscountDto getDiscount(Long id) {
        checkIfDiscountNotExists(id);

        try {
            log.info("Discount with id '{}' has been found", id);
            return discountMapper.mapToDto(discountRepository.getReferenceById(id));
        } catch (Exception e) {
            log.warn("Exception while trying to get discount with id '{}'! {}", id, e.getMessage());
            throw new EntityDeleteRepositoryException(
                    "Exception while trying to get discount with id '%s'! %s".formatted(id, e.getMessage())
            );
        }
    }

    @Override
    public DiscountDto addDiscount(DiscountDto discountDto) {
        try {
            Discount discount = discountMapper.mapToModel(discountDto);
            discount = discountRepository.save(discount);
            log.info("New discount with id '{}' has been saved successfully.", discount.getId());
            return discountMapper.mapToDto(discount);
        } catch (Exception e) {
            log.warn("Unable to save a new discount! {}", e.getMessage());
            throw new EntitySaveRepositoryException(
                    "Unable to save a new discount! %s".formatted(e.getMessage())
            );
        }
    }

    @Override
    public Long removeDiscount(Long id) {
        checkIfDiscountNotExists(id);

        try {
            discountRepository.deleteById(id);
            log.info("Discount with id '{}' has been removed successfully.", id);
            return id;
        } catch (Exception e) {
            log.warn("Unable remove discount with id '{}'!", id);
            throw new EntityDeleteRepositoryException(
                    "Unable remove discount with id '%s'!".formatted(id)
            );
        }
    }

    @Override
    public DiscountDto updateDiscount(DiscountDto discountDto) {
        checkIfDiscountNotExists(discountDto.getId());

        try {
            Discount discount = discountRepository.getReferenceById(discountDto.getId());
            log.info("Discount with id '{}' has been found to update!'", discountDto.getId());
            discountMapper.updateModel(discountDto, discount);
            log.info("Discount with id '{}' has been updated successfully.", discountDto.getId());
            return discountMapper.mapToDto(discount);
        } catch (Exception e) {
            log.warn("Unable update discount with id '{}'! {}", discountDto.getId(), e.getMessage());
            throw new EntityUpdateRepositoryException(
                    "Unable update discount with id '%s'! %s".formatted(discountDto.getId(), e.getMessage())
            );
        }
    }

    private void checkIfDiscountNotExists(Long id) {
        if (!discountRepository.existsById(id)) {
            log.warn("Unable to find discount with id '{}'!", id);
            throw new EntityNotFoundRepositoryException(
                    "Unable to find discount with id '%s'!".formatted(id)
            );
        }
    }
}
