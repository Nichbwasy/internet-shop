package com.shop.product.service;

import com.shop.common.utils.all.exception.dao.EntityDeleteRepositoryException;
import com.shop.common.utils.all.exception.dao.EntityGetRepositoryException;
import com.shop.common.utils.all.exception.dao.EntitySaveRepositoryException;
import com.shop.common.utils.all.exception.dao.EntityUpdateRepositoryException;
import com.shop.product.dao.CategoryRepository;
import com.shop.product.dao.DiscountRepository;
import com.shop.product.dao.ProductRepository;
import com.shop.product.dto.ProductDto;
import com.shop.product.dto.form.AddOrRemoveForm;
import com.shop.product.dto.form.product.NewProductForm;
import com.shop.product.model.Category;
import com.shop.product.model.Discount;
import com.shop.product.model.Product;
import com.shop.product.service.config.ProductServiceContextConfiguration;
import com.shop.product.service.exception.product.AddingCategoryException;
import com.shop.product.service.exception.product.AddingDiscountException;
import com.shop.product.service.exception.product.RemovingCategoryException;
import com.shop.product.service.exception.product.RemovingDiscountException;
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
@ContextConfiguration(classes = ProductServiceContextConfiguration.class)
public class ProductServiceTest {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private DiscountRepository discountRepository;
    @Autowired
    private ProductService productService;

    @Test
    public void getProductTest() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Prod1");
        Mockito.when(productRepository.existsById(1L)).thenReturn(true);
        Mockito.when(productRepository.getReferenceById(1L)).thenReturn(product);

        ProductDto result = productService.getProduct(1L);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(product.getId(), result.getId());
        Assertions.assertEquals(product.getName(), result.getName());
    }

    @Test
    public void getNotExistedProductTest() {
        Mockito.when(productRepository.existsById(Mockito.anyLong())).thenReturn(false);

        Assertions.assertThrows(EntityGetRepositoryException.class, () -> productService.getProduct(1L));
    }

    @Test
    public void getNullProductTest() {
        Mockito.when(productRepository.existsById(Mockito.nullable(Long.class))).thenThrow(NullPointerException.class);

        Assertions.assertThrows(EntityGetRepositoryException.class, () -> productService.getProduct(null));
    }

    @Test
    public void getProductRepositoryTest() {
        Mockito.when(productRepository.existsById(Mockito.anyLong())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(EntityGetRepositoryException.class, () -> productService.getProduct(1L));
    }

    @Test
    public void addProductTest() {
        NewProductForm form = new NewProductForm();
        form.setName("Prod1");

        Mockito.when(categoryRepository.findByIdIn(Mockito.anyList())).thenReturn(new ArrayList<>());
        Mockito.when(discountRepository.findByIdIn(Mockito.anyList())).thenReturn(new ArrayList<>());
        Mockito.when(productRepository.save(Mockito.any(Product.class)))
                .thenAnswer(a -> {
                    Product product = a.getArgument(0);
                    product.setId(1L);
                    return product;
                });

        ProductDto result = productService.addProduct(form);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals(form.getName(), result.getName());
    }

    @Test
    public void addInvalidDataProductTest() {
        NewProductForm form = new NewProductForm();
        form.setName("");

        Assertions.assertThrows(EntitySaveRepositoryException.class, () -> productService.addProduct(form));
    }

    @Test
    public void addNullDataProductTest() {
        Assertions.assertThrows(EntitySaveRepositoryException.class,
                () -> productService.addProduct(new NewProductForm()));
    }

    @Test
    public void addNullProductTest() {
        Assertions.assertThrows(EntitySaveRepositoryException.class, () -> productService.addProduct(null));
    }

    @Test
    public void addProductRepositoryExceptionTest() {
        NewProductForm form = new NewProductForm();
        form.setName("Prod1");

        Mockito.when(categoryRepository.findByIdIn(Mockito.anyList())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(EntitySaveRepositoryException.class, () -> productService.addProduct(form));
    }

    @Test
    public void removeProductTest() {
        Mockito.when(productRepository.existsById(1L)).thenReturn(true);
        Mockito.doNothing().when(productRepository).deleteById(1L);

        Assertions.assertEquals(1L, productService.removeProduct(1L));
    }

    @Test
    public void removeNotExistedProductTest() {
        Mockito.when(productRepository.existsById(Mockito.anyLong())).thenReturn(false);

        Assertions.assertThrows(EntityDeleteRepositoryException.class, () -> productService.removeProduct(1L));
    }

    @Test
    public void removeProductNullIdTest() {
        Mockito.when(productRepository.existsById(Mockito.nullable(Long.class))).thenThrow(NullPointerException.class);

        Assertions.assertThrows(EntityDeleteRepositoryException.class, () -> productService.removeProduct(null));
    }

    @Test
    public void removeProductRepositoryExceptionTest() {
        Mockito.when(productRepository.existsById(Mockito.anyLong())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(EntityDeleteRepositoryException.class, () -> productService.removeProduct(1L));
    }

    @Test
    public void updateProductTest() {
        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setName("Prod2");
        Product product = new Product();
        product.setId(1L);
        product.setName("Prod1");

        Mockito.when(productRepository.existsById(1L)).thenReturn(true);
        Mockito.when(productRepository.getReferenceById(1L)).thenReturn(product);

        ProductDto result = productService.updateProduct(productDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(productDto.getId(), result.getId());
        Assertions.assertEquals(productDto.getName(), result.getName());
    }

    @Test
    public void updateNotExistedProductTest() {
        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        Mockito.when(productRepository.existsById(Mockito.anyLong())).thenReturn(false);

        Assertions.assertThrows(EntityUpdateRepositoryException.class, () -> productService.updateProduct(productDto));
    }

    @Test
    public void updateNullDataProductTest() {
        Mockito.when(productRepository.existsById(Mockito.nullable(Long.class))).thenThrow(NullPointerException.class);

        Assertions.assertThrows(EntityUpdateRepositoryException.class,
                () -> productService.updateProduct(new ProductDto()));
    }

    @Test
    public void updateNullProductTest() {
        Assertions.assertThrows(NullPointerException.class, () -> productService.updateProduct(null));
    }

    @Test
    public void updateProductRepositoryException() {
        Mockito.when(productRepository.existsById(Mockito.anyLong())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(EntityUpdateRepositoryException.class,
                () -> productService.updateProduct(new ProductDto()));
    }

    @Test
    public void addCategoriesTest() {
        AddOrRemoveForm form = new AddOrRemoveForm(1L, List.of(1L, 2L));
        Product product = new Product();
        product.setId(1L);
        product.setName("Prod1");
        product.setCategories(new ArrayList<>());
        List<Category> categories = List.of(
                new Category(1L, "Cat1", null),
                new Category(2L, "Cat2", null)
        );

        Mockito.when(productRepository.existsById(1L)).thenReturn(true);
        Mockito.when(productRepository.getReferenceById(1L)).thenReturn(product);
        Mockito.when(categoryRepository.findByIdIn(form.getAddedOrRemovedIds())).thenReturn(categories);

        ProductDto result = productService.addCategories(form);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(product.getId(), result.getId());
        Assertions.assertEquals(product.getName(), result.getName());
        for (int i = 0; i < categories.size(); i++) {
            Assertions.assertEquals(categories.get(i).getId(), result.getCategories().get(i).getId());
            Assertions.assertEquals(categories.get(i).getName(), result.getCategories().get(i).getName());
        }
    }

    @Test
    public void addCategoriesIfCategoryNotExistsTest() {
        AddOrRemoveForm form = new AddOrRemoveForm(1L, List.of(1L, 2L));

        Mockito.when(productRepository.existsById(Mockito.anyLong())).thenReturn(false);

        Assertions.assertThrows(AddingCategoryException.class, () -> productService.addCategories(form));
    }

    @Test
    public void addCategoriesNullDataTest() {
        Mockito.when(productRepository.existsById(Mockito.nullable(Long.class))).thenThrow(NullPointerException.class);

        Assertions.assertThrows(AddingCategoryException.class, () -> productService.addCategories(new AddOrRemoveForm()));
    }

    @Test
    public void addNullCategoriesTest() {
        Assertions.assertThrows(AddingCategoryException.class, () -> productService.addCategories(null));
    }

    @Test
    public void addCategoriesRepositoryExceptionTest() {
        AddOrRemoveForm form = new AddOrRemoveForm(1L, List.of(1L, 2L));

        Mockito.when(productRepository.existsById(Mockito.anyLong())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(AddingCategoryException.class, () -> productService.addCategories(form));
    }

    @Test
    public void removeCategoriesTest() {
        AddOrRemoveForm form = new AddOrRemoveForm(1L, List.of(1L, 2L));
        List<Category> categories = new ArrayList<>(List.of(
                new Category(1L, "Cat1", null),
                new Category(2L, "Cat2", null)
        ));
        Product product = new Product();
        product.setId(1L);
        product.setName("Prod1");
        product.setCategories(categories);

        Mockito.when(productRepository.existsById(1L)).thenReturn(true);
        Mockito.when(productRepository.getReferenceById(1L)).thenReturn(product);
        Mockito.when(categoryRepository.findByIdIn(form.getAddedOrRemovedIds())).thenReturn(categories);

        ProductDto result = productService.removeCategories(form);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(product.getId(), result.getId());
        Assertions.assertEquals(product.getName(), result.getName());
        Assertions.assertEquals(0, result.getCategories().size());
    }

    @Test
    public void removeCategoriesFromNotExistedProductTest() {
        AddOrRemoveForm form = new AddOrRemoveForm(1L, List.of(1L, 2L));

        Mockito.when(productRepository.existsById(Mockito.anyLong())).thenReturn(false);

        Assertions.assertThrows(RemovingCategoryException.class, () -> productService.removeCategories(form));
    }

    @Test
    public void removeCategoriesNullDataTest() {
        Mockito.when(productRepository.existsById(Mockito.nullable(Long.class))).thenReturn(false);

        Assertions.assertThrows(RemovingCategoryException.class,
                () -> productService.removeCategories(new AddOrRemoveForm()));
    }

    @Test
    public void removeNullCategoriesTest() {
        Assertions.assertThrows(RemovingCategoryException.class, () -> productService.removeCategories(null));
    }

    @Test
    public void removeCategoriesRepositoryExceptionTest() {
        AddOrRemoveForm form = new AddOrRemoveForm(1L, List.of(1L, 2L));

        Mockito.when(productRepository.existsById(Mockito.anyLong())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(RemovingCategoryException.class, () -> productService.removeCategories(form));
    }

    @Test
    public void addDiscountsTest() {
        AddOrRemoveForm form = new AddOrRemoveForm(1L, List.of(1L, 2L));
        Product product = new Product();
        product.setId(1L);
        product.setName("Prod1");
        product.setDiscounts(new ArrayList<>());
        Discount discount1 = new Discount();
        Discount discount2 = new Discount();
        discount1.setId(1L);
        discount2.setId(2L);
        List<Discount> discounts = List.of(discount1, discount2);

        Mockito.when(productRepository.existsById(1L)).thenReturn(true);
        Mockito.when(productRepository.getReferenceById(1L)).thenReturn(product);
        Mockito.when(discountRepository.findByIdIn(form.getAddedOrRemovedIds())).thenReturn(discounts);

        ProductDto result = productService.addDiscounts(form);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(product.getId(), result.getId());
        Assertions.assertEquals(product.getName(), result.getName());
        for (int i = 0; i < discounts.size(); i++) {
            Assertions.assertEquals(discounts.get(i).getId(), result.getDiscounts().get(i).getId());
            Assertions.assertEquals(discounts.get(i).getName(), result.getDiscounts().get(i).getName());
        }
    }

    @Test
    public void addDiscountsIfCategoryNotExistsTest() {
        AddOrRemoveForm form = new AddOrRemoveForm(1L, List.of(1L, 2L));

        Mockito.when(productRepository.existsById(Mockito.anyLong())).thenReturn(false);

        Assertions.assertThrows(AddingDiscountException.class, () -> productService.addDiscounts(form));
    }

    @Test
    public void addDiscountsNullDataTest() {
        Mockito.when(productRepository.existsById(Mockito.nullable(Long.class))).thenThrow(NullPointerException.class);

        Assertions.assertThrows(AddingDiscountException.class, () -> productService.addDiscounts(new AddOrRemoveForm()));
    }

    @Test
    public void addNullDiscountsTest() {
        Assertions.assertThrows(AddingDiscountException.class, () -> productService.addDiscounts(null));
    }

    @Test
    public void addDiscountsRepositoryExceptionTest() {
        AddOrRemoveForm form = new AddOrRemoveForm(1L, List.of(1L, 2L));

        Mockito.when(productRepository.existsById(Mockito.anyLong())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(AddingDiscountException.class, () -> productService.addDiscounts(form));
    }

    @Test
    public void removeDiscountsTest() {
        AddOrRemoveForm form = new AddOrRemoveForm(1L, List.of(1L, 2L));
        Discount discount1 = new Discount();
        Discount discount2 = new Discount();
        discount1.setId(1L);
        discount2.setId(2L);
        List<Discount> discounts = new ArrayList<>(List.of(discount1, discount2));
        Product product = new Product();
        product.setId(1L);
        product.setName("Prod1");
        product.setDiscounts(discounts);

        Mockito.when(productRepository.existsById(1L)).thenReturn(true);
        Mockito.when(productRepository.getReferenceById(1L)).thenReturn(product);
        Mockito.when(discountRepository.findByIdIn(form.getAddedOrRemovedIds())).thenReturn(discounts);

        ProductDto result = productService.removeDiscounts(form);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(product.getId(), result.getId());
        Assertions.assertEquals(product.getName(), result.getName());
        Assertions.assertEquals(0, result.getDiscounts().size());
    }

    @Test
    public void removeDiscountsFromNotExistedProductTest() {
        AddOrRemoveForm form = new AddOrRemoveForm(1L, List.of(1L, 2L));

        Mockito.when(productRepository.existsById(Mockito.anyLong())).thenReturn(false);

        Assertions.assertThrows(RemovingDiscountException.class, () -> productService.removeDiscounts(form));
    }

    @Test
    public void removeDiscountsNullDataTest() {
        Mockito.when(productRepository.existsById(Mockito.nullable(Long.class))).thenReturn(false);

        Assertions.assertThrows(RemovingDiscountException.class,
                () -> productService.removeDiscounts(new AddOrRemoveForm()));
    }

    @Test
    public void removeNullDiscountsTest() {
        Assertions.assertThrows(RemovingDiscountException.class, () -> productService.removeDiscounts(null));
    }

    @Test
    public void removeDiscountsRepositoryExceptionTest() {
        AddOrRemoveForm form = new AddOrRemoveForm(1L, List.of(1L, 2L));

        Mockito.when(productRepository.existsById(Mockito.anyLong())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(RemovingDiscountException.class, () -> productService.removeDiscounts(form));
    }

}
