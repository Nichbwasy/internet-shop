package com.shop.product.service;

import com.shop.common.utils.all.exception.dao.EntityDeleteRepositoryException;
import com.shop.common.utils.all.exception.dao.EntityGetRepositoryException;
import com.shop.common.utils.all.exception.dao.EntitySaveRepositoryException;
import com.shop.common.utils.all.exception.dao.EntityUpdateRepositoryException;
import com.shop.product.dao.SubCategoryRepository;
import com.shop.product.dto.SubCategoryDto;
import com.shop.product.model.SubCategory;
import com.shop.product.service.config.SubCategoryServiceContextConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SubCategoryServiceContextConfiguration.class)
public class SubCategoryServiceTests {

    @Autowired
    private SubCategoryRepository subCategoryRepository;
    @Autowired
    private SubCategoryService subCategoryService;

    @Test
    public void getAllSubCategoriesTest() {
        Mockito.when(subCategoryRepository.findAll()).thenReturn(List.of(new SubCategory(), new SubCategory()));

        List<SubCategoryDto> allSubCategories = subCategoryService.getAllSubCategories();

        Assertions.assertEquals(2, allSubCategories.size());
    }

    @Test
    public void getAllWithRepositoryExceptionTest() {
        Mockito.when(subCategoryRepository.findAll()).thenThrow(RuntimeException.class);

        Assertions.assertThrows(EntityGetRepositoryException.class, () -> subCategoryService.getAllSubCategories());
    }

    @Test
    public void getSubCategoryTest() {
        SubCategory subCategory = new SubCategory(1L, "Sub1");
        Mockito.when(subCategoryRepository.existsById(1L)).thenReturn(true);
        Mockito.when(subCategoryRepository.getReferenceById(1L)).thenReturn(subCategory);

        SubCategoryDto result = subCategoryService.getSubCategory(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(subCategory.getId(), result.getId());
        Assertions.assertEquals(subCategory.getName(), result.getName());
    }

    @Test
    public void getNotExistedSubCategoryTest() {
        Mockito.when(subCategoryRepository.existsById(1L)).thenReturn(false);

        Assertions.assertThrows(EntityGetRepositoryException.class, () -> subCategoryService.getSubCategory(1L));
    }

    @Test
    public void getNullIdSubCategoryTest() {
        Mockito.when(subCategoryRepository.existsById(Mockito.nullable(Long.class)))
                .thenThrow(NullPointerException.class);

        Assertions.assertThrows(EntityGetRepositoryException.class, () -> subCategoryService.getSubCategory(null));
    }

    @Test
    public void getSubCategoryRepositoryExceptionTest() {
        Mockito.when(subCategoryRepository.existsById(Mockito.anyLong())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(EntityGetRepositoryException.class, () -> subCategoryService.getSubCategory(1L));
    }

    @Test
    public void addSubCategoryTest() {
        SubCategory subCategory = new SubCategory(1L, "Sub1");
        Mockito.when(subCategoryRepository.existsByIdOrName(Mockito.anyLong(), Mockito.anyString()))
                        .thenReturn(false);
        Mockito.when(subCategoryRepository.save(Mockito.any(SubCategory.class)))
                .thenAnswer(a -> {
                    SubCategory sc = a.getArgument(0);
                    sc.setId(1L);
                    return sc;
                });

        SubCategoryDto result = subCategoryService.addSubCategory(new SubCategoryDto(null, "Sub1"));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(subCategory.getId(), result.getId());
        Assertions.assertEquals(subCategory.getName(), result.getName());
    }

    @Test
    public void addAlreadyExistedSubCategoryTest() {
        Mockito.when(subCategoryRepository.existsByIdOrName(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(true);

        Assertions.assertThrows(EntitySaveRepositoryException.class,
                () -> subCategoryService.addSubCategory(new SubCategoryDto(1L, "Sub1")));
    }

    @Test
    public void addNullDataSubCategoryTest() {
        Assertions.assertThrows(EntitySaveRepositoryException.class,
                () -> subCategoryService.addSubCategory(new SubCategoryDto()));
    }

    @Test
    public void addNullSubCategoryTest() {
        Assertions.assertThrows(EntitySaveRepositoryException.class,
                () -> subCategoryService.addSubCategory(null));
    }

    @Test
    public void addSubCategoryRepositoryException() {
        Mockito.when(subCategoryRepository.existsByIdOrName(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(RuntimeException.class);

        Assertions.assertThrows(EntitySaveRepositoryException.class,
                () -> subCategoryService.addSubCategory(new SubCategoryDto()));
    }

    @Test
    public void deleteSubCategoryTest() {
        Mockito.when(subCategoryRepository.existsById(1L)).thenReturn(true);
        Mockito.doNothing().when(subCategoryRepository).deleteById(1L);

        Assertions.assertEquals(1L, subCategoryService.removeSubCategory(1L));
    }

    @Test
    public void deleteNotExistedSubCategoryTest() {
        Mockito.when(subCategoryRepository.existsById(1L)).thenReturn(false);

        Assertions.assertThrows(EntityDeleteRepositoryException.class,
                () -> subCategoryService.removeSubCategory(1L));
    }

    @Test
    public void deleteNullSubCategoryTest() {
        Mockito.when(subCategoryRepository.existsById(Mockito.nullable(Long.class)))
                .thenThrow(NullPointerException.class);

        Assertions.assertThrows(EntityDeleteRepositoryException.class,
                () -> subCategoryService.removeSubCategory(null));
    }

    @Test
    public void deleteSubCategoryRepositoryException() {
        Mockito.when(subCategoryRepository.existsById(Mockito.anyLong())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(EntityDeleteRepositoryException.class,
                () -> subCategoryService.removeSubCategory(1L));
    }

    @Test
    public void updateSubCategoryTest() {
        Mockito.when(subCategoryRepository.existsById(1L)).thenReturn(true);
        Mockito.when(subCategoryRepository.getReferenceById(1L))
                .thenReturn(new SubCategory(1L, "Sub1"));

        SubCategoryDto result = subCategoryService.updateSubCategory(new SubCategoryDto(1L, "Sub2"));
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("Sub2", result.getName());
    }

    @Test
    public void updateNotExistedSubCategoryTest() {
        Mockito.when(subCategoryRepository.existsById(Mockito.anyLong())).thenReturn(false);

        Assertions.assertThrows(EntityUpdateRepositoryException.class,
                () -> subCategoryService.updateSubCategory(new SubCategoryDto(1L, "Sub1")));
    }

    @Test
    public void updateNullDataSubCategoryTest() {
        Mockito.when(subCategoryRepository.existsById(Mockito.nullable(Long.class)))
                .thenThrow(NullPointerException.class);

        Assertions.assertThrows(EntityUpdateRepositoryException.class,
                () -> subCategoryService.updateSubCategory(new SubCategoryDto(null, null)));
    }

    @Test
    public void updateNullSubCategoryTest() {
        Assertions.assertThrows(NullPointerException.class,
                () -> subCategoryService.updateSubCategory(null));
    }

    @Test
    public void updateSubCategoryRepositoryExceptionTest() {
        Mockito.when(subCategoryRepository.existsById(Mockito.anyLong())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(EntityUpdateRepositoryException.class,
                () -> subCategoryService.updateSubCategory(new SubCategoryDto()));
    }

}
