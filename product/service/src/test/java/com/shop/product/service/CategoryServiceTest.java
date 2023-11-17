package com.shop.product.service;

import com.shop.common.utils.all.exception.dao.EntityDeleteRepositoryException;
import com.shop.common.utils.all.exception.dao.EntityGetRepositoryException;
import com.shop.common.utils.all.exception.dao.EntitySaveRepositoryException;
import com.shop.common.utils.all.exception.dao.EntityUpdateRepositoryException;
import com.shop.product.dao.CategoryRepository;
import com.shop.product.dao.SubCategoryRepository;
import com.shop.product.dto.CategoryDto;
import com.shop.product.dto.form.AddOrRemoveForm;
import com.shop.product.model.Category;
import com.shop.product.model.SubCategory;
import com.shop.product.service.config.CategoryServiceContextConfiguration;
import com.shop.product.service.exception.category.AddingSubCategoryException;
import com.shop.product.service.exception.category.RemovingSubCategoryException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CategoryServiceContextConfiguration.class)
public class CategoryServiceTest {

    @Autowired
    private SubCategoryRepository subCategoryRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryService categoryService;

    @Test
    public void getAllCategoriesTest() {
        Mockito.when(categoryRepository.findAll()).thenReturn(List.of(new Category(), new Category()));

        List<CategoryDto> result = categoryService.getAllCategories();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
    }

    @Test
    public void getAllCategoriesRepositoryExceptionTest() {
        Mockito.when(categoryRepository.findAll()).thenThrow(RuntimeException.class);

        Assertions.assertThrows(EntityGetRepositoryException.class, () -> categoryService.getAllCategories());
    }

    @Test
    public void getCategoryTest() {
        Category category = new Category(1L, "Cat1", null);
        Mockito.when(categoryRepository.existsById(1L)).thenReturn(true);
        Mockito.when(categoryRepository.getReferenceById(1L)).thenReturn(category);

        CategoryDto result = categoryService.getCategory(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(category.getId(), result.getId());
        Assertions.assertEquals(category.getName(), result.getName());
    }

    @Test
    public void getNotExistedCategoryTest() {
        Mockito.when(categoryRepository.existsById(Mockito.anyLong())).thenReturn(false);

        Assertions.assertThrows(EntityGetRepositoryException.class, () -> categoryService.getCategory(1L));
    }

    @Test
    public void getCategoryNullDataTest() {
        Mockito.when(categoryRepository.existsById(Mockito.nullable(Long.class))).thenThrow(NullPointerException.class);

        Assertions.assertThrows(EntityGetRepositoryException.class, () -> categoryService.getCategory(null));
    }

    @Test
    public void getCategoryRepositoryExceptionTest() {
        Mockito.when(categoryRepository.existsById(Mockito.anyLong())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(EntityGetRepositoryException.class, () -> categoryService.getCategory(1L));
    }

    @Test
    public void addCategoryTest() {
        Category category = new Category(1L, "Cat1", null);
        Mockito.when(categoryRepository.existsByIdOrName(Mockito.anyLong(), Mockito.anyString())).thenReturn(false);
        Mockito.when(categoryRepository.save(Mockito.any(Category.class)))
                .thenAnswer(a -> {
                    Category c = a.getArgument(0);
                    c.setId(1L);
                    return c;
                });

        CategoryDto result = categoryService.addCategory(new CategoryDto(null, "Cat1", null));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(category.getId(), result.getId());
        Assertions.assertEquals(category.getName(), result.getName());
    }

    @Test
    public void addAlreadyExistedCategoryTest() {
        Mockito.when(categoryRepository.existsByIdOrName(Mockito.anyLong(), Mockito.anyString())).thenReturn(true);

        Assertions.assertThrows(EntitySaveRepositoryException.class,
                () -> categoryService.addCategory(new CategoryDto()));
    }

    @Test
    public void addCategoryWithNullDataTest() {
        Mockito.when(categoryRepository.existsByIdOrName(Mockito.nullable(Long.class), Mockito.nullable(String.class)))
                .thenThrow(NullPointerException.class);

        Assertions.assertThrows(EntitySaveRepositoryException.class,
                () -> categoryService.addCategory(new CategoryDto(null, null, null)));
    }

    @Test
    public void addNullCategoryTest() {
        Assertions.assertThrows(EntitySaveRepositoryException.class,
                () -> categoryService.addCategory(null));
    }

    @Test
    public void addCategoryRepositoryExceptionTest() {
        Mockito.when(categoryRepository.existsByIdOrName(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(RuntimeException.class);

        Assertions.assertThrows(EntitySaveRepositoryException.class,
                () -> categoryService.addCategory(new CategoryDto()));
    }

    @Test
    public void removeCategoryTest() {
        Mockito.when(categoryRepository.existsById(1L)).thenReturn(true);
        Mockito.doNothing().when(categoryRepository).deleteById(1L);

        Assertions.assertEquals(1L, categoryService.removeCategory(1L));
    }

    @Test
    public void removeNotExistedCategoryTest() {
        Mockito.when(categoryRepository.existsById(Mockito.anyLong())).thenReturn(false);

        Assertions.assertThrows(EntityDeleteRepositoryException.class, () -> categoryService.removeCategory(1L));
    }

    @Test
    public void removeCategoryWithNullDataTest() {
        Mockito.when(categoryRepository.existsById(Mockito.nullable(Long.class))).thenThrow(NullPointerException.class);

        Assertions.assertThrows(EntityDeleteRepositoryException.class, () -> categoryService.removeCategory(null));
    }

    @Test
    public void removeCategoryRepositoryException() {
        Mockito.when(categoryRepository.existsById(Mockito.anyLong())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(EntityDeleteRepositoryException.class, () -> categoryService.removeCategory(1L));
    }

    @Test
    public void updateCategoryTest() {
        Category category = new Category(1L, "Cat1", null);
        Mockito.when(categoryRepository.existsById(1L)).thenReturn(true);
        Mockito.when(categoryRepository.getReferenceById(1L)).thenReturn(category);

        CategoryDto result = categoryService.updateCategory(new CategoryDto(1L, "Cat2", null));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("Cat2", result.getName());
    }

    @Test
    public void updateNotExistedCategoryTest() {
        Mockito.when(categoryRepository.existsById(Mockito.anyLong())).thenReturn(false);

        Assertions.assertThrows(EntityUpdateRepositoryException.class,
                () -> categoryService.updateCategory(new CategoryDto(1L, "Cat1", null)));
    }

    @Test
    public void updateCategoryWithNullDataTest() {
        Mockito.when(categoryRepository.getReferenceById(Mockito.nullable(Long.class)))
                .thenThrow(NullPointerException.class);

        Assertions.assertThrows(EntityUpdateRepositoryException.class,
                () -> categoryService.updateCategory(new CategoryDto()));
    }

    @Test
    public void updateNullCategoryTest() {
        Assertions.assertThrows(NullPointerException.class,
                () -> categoryService.updateCategory(null));
    }

    @Test
    public void updateCategoryRepositoryExceptionTest() {
        Mockito.when(categoryRepository.getReferenceById(Mockito.anyLong())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(EntityUpdateRepositoryException.class,
                () -> categoryService.updateCategory(new CategoryDto()));
    }

    @Test
    public void addSubCategoryTest() {
        AddOrRemoveForm form = new AddOrRemoveForm(1L, List.of(1L, 2L));
        List<SubCategory> subCategories = List.of(
                new SubCategory(1L, "Sub1"),
                new SubCategory(2L, "Sub2"));

        Mockito.when(categoryRepository.existsById(1L)).thenReturn(true);
        Mockito.when(categoryRepository.getReferenceById(1L))
                .thenReturn(new Category(1L, "Cat1", new ArrayList<>()));
        Mockito.when(subCategoryRepository.findByIdIn(form.getAddedOrRemovedIds())).thenReturn(subCategories);

        CategoryDto result = categoryService.addSubCategory(form);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("Cat1", result.getName());
        for (int i = 0; i < subCategories.size(); i++) {
            Assertions.assertEquals(subCategories.get(i).getId(), result.getSubCategories().get(i).getId());
            Assertions.assertEquals(subCategories.get(i).getName(), result.getSubCategories().get(i).getName());
        }
    }

    @Test
    public void addSubCategoryCategoryNotExistsTest() {
        AddOrRemoveForm form = new AddOrRemoveForm(1L, List.of(1L, 2L));
        Mockito.when(categoryRepository.existsById(Mockito.anyLong())).thenReturn(false);

        Assertions.assertThrows(AddingSubCategoryException.class, () -> categoryService.addSubCategory(form));
    }

    @Test
    public void addSubCategoryNullFormDataTest() {
        AddOrRemoveForm form = new AddOrRemoveForm(null, null);
        Mockito.when(categoryRepository.existsById(Mockito.nullable(Long.class))).thenThrow(NullPointerException.class);

        Assertions.assertThrows(AddingSubCategoryException.class, () -> categoryService.addSubCategory(form));
    }

    @Test
    public void addSubCategoryNullFormTest() {
        Assertions.assertThrows(AddingSubCategoryException.class, () -> categoryService.addSubCategory(null));
    }

    @Test
    public void addSubCategoryRepositoryException() {
        Mockito.when(categoryRepository.existsById(Mockito.anyLong())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(AddingSubCategoryException.class,
                () -> categoryService.addSubCategory(new AddOrRemoveForm()));
    }

    @Test
    public void removeSubCategoryTest() {
        AddOrRemoveForm form = new AddOrRemoveForm(1L, List.of(1L, 2L));
        List<SubCategory> subCategories = new ArrayList<>(List.of(
                new SubCategory(1L, "Sub1"),
                new SubCategory(2L, "Sub2")));

        Mockito.when(categoryRepository.existsById(1L)).thenReturn(true);
        Mockito.when(categoryRepository.getReferenceById(1L))
                .thenReturn(new Category(1L, "Cat1", subCategories));
        Mockito.when(subCategoryRepository.findByIdIn(form.getAddedOrRemovedIds()))
                .thenReturn(subCategories);

        CategoryDto result = categoryService.removeSubCategory(form);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("Cat1", result.getName());
        Assertions.assertEquals(0, result.getSubCategories().size());
    }

    @Test
    public void removeSubCategoryCategoryNotExistsTest() {
        AddOrRemoveForm form = new AddOrRemoveForm(1L, List.of(1L, 2L));
        Mockito.when(categoryRepository.existsById(Mockito.anyLong())).thenReturn(false);

        Assertions.assertThrows(RemovingSubCategoryException.class, () -> categoryService.removeSubCategory(form));
    }

    @Test
    public void removeSubCategoryNullFormDataTest() {
        Mockito.when(categoryRepository.existsById(Mockito.nullable(Long.class))).thenThrow(NullPointerException.class);

        Assertions.assertThrows(RemovingSubCategoryException.class,
                () -> categoryService.removeSubCategory(new AddOrRemoveForm(null, null)));
    }

    @Test
    public void removeSubCategoryNullFormTest() {
        Assertions.assertThrows(RemovingSubCategoryException.class, () -> categoryService.removeSubCategory(null));
    }

    @Test
    public void removeSubCategoryRepositoryExceptionTest() {
        AddOrRemoveForm form = new AddOrRemoveForm(1L, List.of(1L, 2L));
        Mockito.when(categoryRepository.existsById(Mockito.anyLong())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(RemovingSubCategoryException.class, () -> categoryService.removeSubCategory(form));
    }

}
