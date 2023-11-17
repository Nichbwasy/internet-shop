package com.shop.product.service;

import com.shop.common.utils.all.exception.dao.EntityDeleteRepositoryException;
import com.shop.common.utils.all.exception.dao.EntityGetRepositoryException;
import com.shop.common.utils.all.exception.dao.EntitySaveRepositoryException;
import com.shop.common.utils.all.exception.dao.EntityUpdateRepositoryException;
import com.shop.product.dao.DiscountRepository;
import com.shop.product.dto.DiscountDto;
import com.shop.product.model.Discount;
import com.shop.product.service.config.DiscountServiceContextConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DiscountServiceContextConfiguration.class)
public class DiscountServiceTests {

    @Autowired
    private DiscountService discountService;
    @Autowired
    private DiscountRepository discountRepository;

    @Test
    public void getAllDiscountsTest() {
        Mockito.when(discountRepository.findAll()).thenReturn(List.of(new Discount(), new Discount()));

        List<DiscountDto> result = discountService.getAllDiscounts();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
    }

    @Test
    public void getAllDiscountsRepositoryExceptionTest() {
        Mockito.when(discountRepository.findAll()).thenThrow(RuntimeException.class);

        Assertions.assertThrows(EntityGetRepositoryException.class, () -> discountService.getAllDiscounts());
    }

    @Test
    public void getDiscountTest() {
        Discount discount = new Discount();
        discount.setId(1L);
        discount.setName("name");
        Mockito.when(discountRepository.existsById(1L)).thenReturn(true);
        Mockito.when(discountRepository.getReferenceById(1L)).thenReturn(discount);

        DiscountDto result = discountService.getDiscount(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(discount.getId(), result.getId());
        Assertions.assertEquals(discount.getName(), result.getName());
    }

    @Test
    public void getNotExistedDiscountTest() {
        Mockito.when(discountRepository.existsById(Mockito.anyLong())).thenReturn(false);

        Assertions.assertThrows(EntityGetRepositoryException.class, () -> discountService.getDiscount(1L));
    }

    @Test
    public void getDiscountNullIdTest() {
        Mockito.when(discountRepository.existsById(Mockito.nullable(Long.class))).thenThrow(NullPointerException.class);

        Assertions.assertThrows(EntityGetRepositoryException.class, () -> discountService.getDiscount(null));
    }

    @Test
    public void getDiscountRepositoryExceptionTest() {
        Mockito.when(discountRepository.existsById(Mockito.anyLong())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(EntityGetRepositoryException.class, () -> discountService.getDiscount(1L));
    }

    @Test
    public void addDiscountTest() {
        DiscountDto discountDto = new DiscountDto();
        discountDto.setName("Disc1");
        Mockito.when(discountRepository.save(Mockito.any(Discount.class)))
                .thenAnswer(a -> {
                    Discount discount = a.getArgument(0);
                    discount.setId(1L);
                    return discount;
                });

        DiscountDto result = discountService.addDiscount(discountDto);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals(discountDto.getName(), result.getName());
        Assertions.assertTrue(result.getCreatedTime().isBefore(LocalDateTime.now()));
    }

    @Test
    public void addDiscountNullDataTest() {
        Mockito.when(discountRepository.save(Mockito.any(Discount.class)))
                .thenAnswer(a -> {
                    Discount discount = a.getArgument(0);
                    discount.setId(1L);
                    return discount;
                });

        DiscountDto result = discountService.addDiscount(new DiscountDto());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertTrue(result.getCreatedTime().isBefore(LocalDateTime.now()));
    }

    @Test
    public void addDiscountNullTest() {
        Assertions.assertThrows(EntitySaveRepositoryException.class, () -> discountService.addDiscount(null));
    }

    @Test
    public void addDiscountRepositoryExceptionTest() {
        Mockito.when(discountRepository.getReferenceById(Mockito.anyLong())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(EntitySaveRepositoryException.class,
                () -> discountService.addDiscount(new DiscountDto()));
    }

    @Test
    public void removeDiscountTest() {
        Mockito.when(discountRepository.existsById(1L)).thenReturn(true);
        Mockito.doNothing().when(discountRepository).deleteById(1L);

        Assertions.assertEquals(1L, discountService.removeDiscount(1L));
    }

    @Test
    public void removeNotExistedDiscountTest() {
        Mockito.when(discountRepository.existsById(Mockito.anyLong())).thenReturn(false);

        Assertions.assertThrows(EntityDeleteRepositoryException.class, () -> discountService.removeDiscount(1L));
    }

    @Test
    public void removeDiscountWithNullIdTest() {
        Mockito.when(discountRepository.existsById(Mockito.nullable(Long.class))).thenThrow(NullPointerException.class);

        Assertions.assertThrows(EntityDeleteRepositoryException.class, () -> discountService.removeDiscount(null));
    }

    @Test
    public void removeDiscountRepositoryException() {
        Mockito.when(discountRepository.existsById(Mockito.anyLong())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(EntityDeleteRepositoryException.class, () -> discountService.removeDiscount(1L));
    }

    @Test
    public void updateDiscountTest() {
        DiscountDto discountDto = new DiscountDto();
        discountDto.setId(1L);
        discountDto.setName("Disc2");
        Discount discount = new Discount();
        discount.setId(1L);
        discount.setName("Disc1");
        Mockito.when(discountRepository.existsById(1L)).thenReturn(true);
        Mockito.when(discountRepository.getReferenceById(1L)).thenReturn(discount);

        DiscountDto result = discountService.updateDiscount(discountDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(discountDto.getId(), result.getId());
        Assertions.assertEquals(discountDto.getName(), result.getName());
    }

    @Test
    public void updateNorExistedDiscountTest() {
        DiscountDto discountDto = new DiscountDto();
        discountDto.setId(1L);
        Mockito.when(discountRepository.existsById(Mockito.anyLong())).thenReturn(false);

        Assertions.assertThrows(EntityUpdateRepositoryException.class,
                () -> discountService.updateDiscount(discountDto));
    }

    @Test
    public void updateDiscountWithNullDataTest() {
        Mockito.when(discountRepository.existsById(Mockito.nullable(Long.class))).thenThrow(NullPointerException.class);

        Assertions.assertThrows(EntityUpdateRepositoryException.class,
                () -> discountService.updateDiscount(new DiscountDto()));
    }

    @Test
    public void updateNullDiscountTest() {
        Assertions.assertThrows(NullPointerException.class,
                () -> discountService.updateDiscount(null));
    }

    @Test
    public void updateDiscountRepositoryException() {
        Mockito.when(discountRepository.existsById(Mockito.anyLong())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(EntityUpdateRepositoryException.class,
                () -> discountService.updateDiscount(new DiscountDto()));
    }

}
