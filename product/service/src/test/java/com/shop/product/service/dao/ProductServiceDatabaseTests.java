package com.shop.product.service.dao;

import com.shop.common.utils.all.consts.SortDirection;
import com.shop.product.dto.ProductDto;
import com.shop.product.dto.form.product.NewProductForm;
import com.shop.product.dto.form.product.ProductFilterForm;
import com.shop.product.service.ProductService;
import com.shop.product.service.dao.condig.ProductServiceDatabaseTestsConfiguration;
import com.shop.product.service.exception.product.GetProductsPageException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ProductServiceDatabaseTestsConfiguration.class)
public class ProductServiceDatabaseTests {

    @Autowired
    private ProductService productService;

    @Test
    public void getPageOfFilteredProductsAscendingFilterByNameTest() {
        initProductsForSortingTests();
        ProductFilterForm filterForm = new ProductFilterForm();
        filterForm.setName("Product");
        filterForm.setSortByName(SortDirection.ASCENDING);

        List<ProductDto> result = productService.getPageOfFilteredProducts(1, filterForm);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals("ProductA", result.get(0).getName());
        Assertions.assertEquals("ProductB", result.get(1).getName());
        Assertions.assertEquals("ProductC", result.get(2).getName());
    }

    @Test
    public void getPageOfFilteredProductsDescendingFilterByNameTest() {
        initProductsForSortingTests();
        ProductFilterForm filterForm = new ProductFilterForm();
        filterForm.setName("Product");
        filterForm.setSortByName(SortDirection.DESCENDING);

        List<ProductDto> result = productService.getPageOfFilteredProducts(1, filterForm);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals("ProductC", result.get(0).getName());
        Assertions.assertEquals("ProductB", result.get(1).getName());
        Assertions.assertEquals("ProductA", result.get(2).getName());
    }

    @Test
    public void getPageOfFilteredProductsAscendingFilterByPriceTest() {
        initProductsForSortingTests();
        ProductFilterForm filterForm = new ProductFilterForm();
        filterForm.setMinPrice(new BigDecimal("15.0"));
        filterForm.setMaxPrice(new BigDecimal("35.0"));
        filterForm.setSortByPrice(SortDirection.ASCENDING);

        List<ProductDto> result = productService.getPageOfFilteredProducts(1, filterForm);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.get(0).getPrice().compareTo(result.get(1).getPrice()) < 0);
    }

    @Test
    public void getPageOfFilteredProductsDescendingFilterByPriceTest() {
        initProductsForSortingTests();
        ProductFilterForm filterForm = new ProductFilterForm();
        filterForm.setMinPrice(new BigDecimal("15.0"));
        filterForm.setMaxPrice(new BigDecimal("35.0"));
        filterForm.setSortByPrice(SortDirection.DESCENDING);

        List<ProductDto> result = productService.getPageOfFilteredProducts(1, filterForm);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.get(0).getPrice().compareTo(result.get(1).getPrice()) > 0);
    }

    @Test
    public void getPageOfFilteredProductsAscendingFilterByCreationTimeTest() {
        initProductsForSortingTests();
        LocalDateTime minTime = LocalDateTime.now().minusSeconds(30);
        LocalDateTime maxTime = LocalDateTime.now().plusSeconds(30);

        ProductFilterForm filterForm = new ProductFilterForm();
        filterForm.setMaxCreatedTime(minTime);
        filterForm.setMaxCreatedTime(maxTime);
        filterForm.setSortByCreatedTime(SortDirection.ASCENDING);

        List<ProductDto> result = productService.getPageOfFilteredProducts(1, filterForm);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertTrue(result.get(0).getCreatedTime().isBefore(result.get(1).getCreatedTime()));
        Assertions.assertTrue(result.get(1).getCreatedTime().isBefore(result.get(2).getCreatedTime()));
    }

    @Test
    public void getPageOfFilteredProductsDescendingFilterByCreationTimeTest() {
        initProductsForSortingTests();
        LocalDateTime minTime = LocalDateTime.now().minusSeconds(30);
        LocalDateTime maxTime = LocalDateTime.now().plusSeconds(30);

        ProductFilterForm filterForm = new ProductFilterForm();
        filterForm.setMaxCreatedTime(minTime);
        filterForm.setMaxCreatedTime(maxTime);
        filterForm.setSortByCreatedTime(SortDirection.DESCENDING);

        List<ProductDto> result = productService.getPageOfFilteredProducts(1, filterForm);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertTrue(result.get(0).getCreatedTime().isAfter(result.get(1).getCreatedTime()));
        Assertions.assertTrue(result.get(1).getCreatedTime().isAfter(result.get(2).getCreatedTime()));
    }

    @Test
    public void getPageOfFilteredProductsNullFormDataTest() {
        initProductsForSortingTests();
        ProductFilterForm filterForm = new ProductFilterForm();
        List<ProductDto> result = productService.getPageOfFilteredProducts(1, filterForm);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
    }

    @Test
    public void getPageOfFilteredProductsNullFormTest() {
        Assertions.assertThrows(GetProductsPageException.class,
                () -> productService.getPageOfFilteredProducts(1, null));
    }

    private void initProductsForSortingTests() {
        NewProductForm form1 = new NewProductForm();
        form1.setName("ProductA");
        form1.setCount(1);
        form1.setPrice(new BigDecimal("10.0"));
        NewProductForm form2 = new NewProductForm();
        form2.setName("ProductB");
        form2.setCount(1);
        form2.setPrice(new BigDecimal("20.0"));
        NewProductForm form3 = new NewProductForm();
        form3.setName("ProductC");
        form3.setCount(1);
        form3.setPrice(new BigDecimal("30.0"));

        productService.addProduct(form1);
        productService.addProduct(form2);
        productService.addProduct(form3);
    }


}
