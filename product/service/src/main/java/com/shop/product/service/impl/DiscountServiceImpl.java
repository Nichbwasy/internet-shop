package com.shop.product.service.impl;

import com.shop.common.utils.all.exception.dao.*;
import com.shop.product.dao.DiscountRepository;
import com.shop.product.dto.DiscountDto;
import com.shop.product.model.Discount;
import com.shop.product.service.DiscountService;
import com.shop.product.service.mappers.DiscountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
            throw new EntityGetRepositoryException(
                    "Exception while trying to get all discounts! %s".formatted(e.getMessage())
            );
        }
    }

    @Override
    public DiscountDto getDiscount(Long id) {
        try {
            checkIfDiscountNotExists(id);

            log.info("Discount with id '{}' has been found", id);
            return discountMapper.mapToDto(discountRepository.getReferenceById(id));
        } catch (Exception e) {
            log.warn("Exception while trying to get discount with id '{}'! {}", id, e.getMessage());
            throw new EntityGetRepositoryException(
                    "Exception while trying to get discount with id '%s'! %s".formatted(id, e.getMessage())
            );
        }
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public DiscountDto addDiscount(DiscountDto discountDto) {
        try {
            Discount discount = discountMapper.mapToModel(discountDto);
            discount.setCreatedTime(LocalDateTime.now());
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
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Long removeDiscount(Long id) {
        try {
            checkIfDiscountNotExists(id);

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
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public DiscountDto updateDiscount(DiscountDto discountDto) {
        try {
            checkIfDiscountNotExists(discountDto.getId());

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

    @Override
    public List<DiscountDto> findDiscountsByIds(List<Long> ids) {
        try {
            return discountRepository.findByIdIn(ids).stream()
                    .map(discountMapper::mapToDto)
                    .toList();
        } catch (Exception e) {
            log.error("Unable get discounts! {}", e.getMessage());
            throw new EntityGetRepositoryException("Unable get discounts! %s".formatted(e.getMessage()));
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
