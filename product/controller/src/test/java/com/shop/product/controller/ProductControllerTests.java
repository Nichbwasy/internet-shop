package com.shop.product.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shop.authorization.client.TokensApiClient;
import com.shop.authorization.common.constant.UsersRoles;
import com.shop.authorization.common.constant.jwt.TokenStatus;
import com.shop.authorization.dto.token.JwtAuthenticationTokenDataDto;
import com.shop.common.utils.all.consts.SortDirection;
import com.shop.common.utils.all.generator.StringGenerator;
import com.shop.product.controller.config.CommonProductControllerTestConfiguration;
import com.shop.product.controller.run.ProductControllerTestsRun;
import com.shop.product.dao.CategoryRepository;
import com.shop.product.dao.DiscountRepository;
import com.shop.product.dao.ProductRepository;
import com.shop.product.dto.ProductDto;
import com.shop.product.dto.form.AddOrRemoveForm;
import com.shop.product.dto.form.product.NewProductForm;
import com.shop.product.dto.form.product.ProductFilterForm;
import com.shop.product.model.Category;
import com.shop.product.model.Discount;
import com.shop.product.model.Product;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(classes = {ProductControllerTestsRun.class, CommonProductControllerTestConfiguration.class})
public class ProductControllerTests {

    @Value("${products.page.size}")
    private Integer PAGE_SIZE;
    @Value("${test.access.token}")
    private String TEST_ACCESS_TOKEN ;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private DiscountRepository discountRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TokensApiClient tokensApiClient;

    @Container
    private final static PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer("postgres:14");

    private final static JsonMapper jsonMapper = new JsonMapper();

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeAll
    public static void init() {
        jsonMapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    public void mockApiTokensClient() {
        JwtAuthenticationTokenDataDto tokenDataDto = new JwtAuthenticationTokenDataDto();
        tokenDataDto.setUsername("TEST");
        tokenDataDto.setAuthorities(List.of(UsersRoles.ADMIN));
        tokenDataDto.setAuthenticated(true);

        Mockito.when(tokensApiClient.validateAccessToken(Mockito.anyString()))
                .thenReturn(ResponseEntity.ok().body(TokenStatus.OK));
        Mockito.when(tokensApiClient.getAuthenticationFromToken(Mockito.anyString()))
                .thenReturn(ResponseEntity.ok().body(tokenDataDto));
    }

    @AfterEach
    public void refreshDatabase() {
        productRepository.deleteAll();
        discountRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    public void getProductsPageWithoutFilterTest() throws Exception {
        ProductFilterForm form = new ProductFilterForm();
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < PAGE_SIZE + 1; i++) products.add(productRepository.save(generateProduct()));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/products/1")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()", Matchers.equalTo(PAGE_SIZE)))
                .andReturn();
        List<ProductDto> resultProducts = jsonMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

        resultProducts.forEach(resProd ->
            Assertions.assertTrue(products.stream().anyMatch(pr -> isProductModelAndDtoEquivalent(pr, resProd)))
        );
    }

    @Test
    public void getProductsPageWithFilterByNameAscendingOrderingTest() throws Exception {
        ProductFilterForm form = new ProductFilterForm();
        form.setName("PRODUCT");
        form.setSortByName(SortDirection.ASCENDING);
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < PAGE_SIZE + 1; i++) {
            Product product = generateProduct();
            if (i < (PAGE_SIZE + 1) / 2) product.setName("PRODUCT" + i);
            products.add(productRepository.save(product));
        }

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/products/1")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()",
                        Matchers.equalTo((PAGE_SIZE + 1) / 2)))
                .andReturn();
        List<ProductDto> resultProducts = jsonMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

        for (int i = 0; i < resultProducts.size(); i++) {
            int finalI = i;
            Assertions.assertTrue(resultProducts.get(i).getName().contains("PRODUCT"));
            Assertions.assertTrue(products.stream().anyMatch(pr ->
                    isProductModelAndDtoEquivalent(pr, resultProducts.get(finalI)))
            );
            Assertions.assertEquals("PRODUCT" + i, products.get(i).getName());
        }
    }

    @Test
    public void getProductsPageWithFilterByNameDescendingOrderingTest() throws Exception {
        ProductFilterForm form = new ProductFilterForm();
        form.setName("PRODUCT");
        form.setSortByName(SortDirection.DESCENDING);
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < PAGE_SIZE + 1; i++) {
            Product product = generateProduct();
            if (i < (PAGE_SIZE + 1) / 2) product.setName("PRODUCT" + i);
            products.add(productRepository.save(product));
        }

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/products/1")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()",
                        Matchers.equalTo((PAGE_SIZE + 1) / 2)))
                .andReturn();
        List<ProductDto> resultProducts = jsonMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

        for (int i = resultProducts.size() - 1; i >= 0 ; i--) {
            int finalI = i;
            Assertions.assertTrue(resultProducts.get(i).getName().contains("PRODUCT"));
            Assertions.assertTrue(products.stream().anyMatch(pr ->
                    isProductModelAndDtoEquivalent(pr, resultProducts.get(finalI)))
            );
            Assertions.assertEquals("PRODUCT" + i, products.get(i).getName());
        }
    }

    @Test
    public void getProductsPageWithFilterByPriceAscendingOrderingTest() throws Exception {
        ProductFilterForm form = new ProductFilterForm();
        form.setMinPrice(BigDecimal.valueOf(1));
        form.setMinPrice(BigDecimal.valueOf(1 + ((PAGE_SIZE + 1))));
        form.setSortByPrice(SortDirection.ASCENDING);
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < PAGE_SIZE + 1; i++) {
            Product product = generateProduct();
            product.setPrice(BigDecimal.valueOf(1 + i));
            products.add(productRepository.save(product));
        }

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/products/1")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()",
                        Matchers.equalTo(((PAGE_SIZE + 1) / 2) + 1)))
                .andReturn();
        List<ProductDto> resultProducts = jsonMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

        AtomicReference<ProductDto> previous = new AtomicReference<>();
        resultProducts.forEach(resProd -> {
            Assertions.assertTrue(products.stream().anyMatch(pr -> isProductModelAndDtoEquivalent(pr, resProd)));
            if (previous.get() != null) Assertions.assertTrue(previous.get().getPrice().compareTo(resProd.getPrice()) < 0);
            previous.set(resProd);
        });
    }

    @Test
    public void getProductsPageWithFilterByPriceDescendingOrderingTest() throws Exception {
        ProductFilterForm form = new ProductFilterForm();
        form.setMinPrice(BigDecimal.valueOf(1));
        form.setMinPrice(BigDecimal.valueOf(1 + ((PAGE_SIZE + 1))));
        form.setSortByPrice(SortDirection.DESCENDING);
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < PAGE_SIZE + 1; i++) {
            Product product = generateProduct();
            product.setPrice(BigDecimal.valueOf(1 + i));
            products.add(productRepository.save(product));
        }

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/products/1")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()",
                        Matchers.equalTo(((PAGE_SIZE + 1) / 2) + 1)))
                .andReturn();
        List<ProductDto> resultProducts = jsonMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

        AtomicReference<ProductDto> previous = new AtomicReference<>();
        resultProducts.forEach(resProd -> {
            Assertions.assertTrue(products.stream().anyMatch(pr -> isProductModelAndDtoEquivalent(pr, resProd)));
            if (previous.get() != null) Assertions.assertTrue(previous.get().getPrice().compareTo(resProd.getPrice()) > 0);
            previous.set(resProd);
        });
    }

    @Test
    public void getProductsNotExistedPageTest() throws Exception {
        ProductFilterForm form = new ProductFilterForm();

        mockMvc.perform(MockMvcRequestBuilders.post("/products/999")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()", Matchers.equalTo(0)))
                .andReturn();
    }

    @Test
    public void getProductsZeroPageTest() throws Exception {
        ProductFilterForm form = new ProductFilterForm();

        mockMvc.perform(MockMvcRequestBuilders.post("/products/0")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getProductsNegativePageTest() throws Exception {
        ProductFilterForm form = new ProductFilterForm();

        mockMvc.perform(MockMvcRequestBuilders.post("/products/-1")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getProductsPageWithoutFormTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/products/1")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getProductTest() throws Exception {
        Product product = productRepository.save(generateProduct());

        mockMvc.perform(MockMvcRequestBuilders.get("/products/product/1")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(product.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(product.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(product.getDescription()));
    }

    @Test
    public void getNotExistedProductTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/products/product/1")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void addProductTest() throws Exception {
        NewProductForm form = generateProductForm();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        ProductDto result = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), ProductDto.class);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(form.getName(), result.getName());
        Assertions.assertEquals(form.getCount(), result.getCount());
        Assertions.assertEquals(form.getDescription(), result.getDescription());
        Assertions.assertEquals(0, form.getPrice().compareTo(result.getPrice()));
        Assertions.assertTrue(result.getCreatedTime().isBefore(LocalDateTime.now()));
    }

    @Test
    public void addAlreadyExistedProductTest() throws Exception {
        Product product = productRepository.save(generateProduct());
        NewProductForm form = new NewProductForm();
        form.setName(product.getName());
        form.setPrice(product.getPrice());
        form.setCount(product.getCount());
        form.setDescription(product.getDescription());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        ProductDto result = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), ProductDto.class);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(form.getName(), result.getName());
        Assertions.assertEquals(form.getCount(), result.getCount());
        Assertions.assertEquals(form.getDescription(), result.getDescription());
        Assertions.assertEquals(0, form.getPrice().compareTo(result.getPrice()));
        Assertions.assertTrue(result.getCreatedTime().isBefore(LocalDateTime.now()));
        Assertions.assertNotEquals(result.getId(), product.getId());
        Assertions.assertNotEquals(result.getCode(), product.getCode());
    }

    @Test
    public void addNullDataProductTest() throws Exception {
        NewProductForm form = new NewProductForm();

        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void addNullProductTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void removeProductTest() throws Exception {
        Product product = productRepository.save(generateProduct());

        mockMvc.perform(MockMvcRequestBuilders.delete("/products/" + product.getId())
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(product.getId()));
    }

    @Test
    public void removeNotExistedProductTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/products/1")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void updateProductTest() throws Exception {
        Product product = productRepository.save(generateProduct());
        ProductDto productDto = generateProductDto();
        productDto.setId(product.getId());

        mockMvc.perform(MockMvcRequestBuilders.put("/products")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(productDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(productDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(productDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(productDto.getDescription()));
    }

    @Test
    public void updateNotExistedProductTest() throws Exception {
        ProductDto productDto = generateProductDto();
        productDto.setId(1L);

        mockMvc.perform(MockMvcRequestBuilders.put("/products")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(productDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void updateNullDataProductTest() throws Exception {
        ProductDto productDto = new ProductDto();

        mockMvc.perform(MockMvcRequestBuilders.put("/products")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(productDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void updateNullProductTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/products")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void addCategoriesTest() throws Exception {
        Product product = productRepository.save(generateProduct());
        Category cat1 = categoryRepository.save(generateCategory());
        Category cat2 = categoryRepository.save(generateCategory());
        AddOrRemoveForm form = new AddOrRemoveForm(0L, List.of(0L, cat1.getId(), cat2.getId()));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/products/product/" + product.getId() + "/new/category")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        ProductDto result = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), ProductDto.class);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(product.getId(), result.getId());
        Assertions.assertEquals(2, result.getCategories().size());
        result.getCategories().forEach(cat ->
                Assertions.assertTrue(
                        Stream.of(cat1, cat2).anyMatch(c -> c.getId().equals(cat.getId()))
                )
        );
    }

    @Test
    public void addNotExistedCategoriesTest() throws Exception {
        AddOrRemoveForm form = new AddOrRemoveForm(0L, List.of(0L, 1L, 2L));

        mockMvc.perform(MockMvcRequestBuilders.post("/products/product/1/new/category")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void addNullDataCategoriesTest() throws Exception {
        AddOrRemoveForm form = new AddOrRemoveForm();

        mockMvc.perform(MockMvcRequestBuilders.post("/products/product/1/new/category")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void addNullCategoriesTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/products/product/1/new/category")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void removeCategoryTest() throws Exception{
        Category cat1 = categoryRepository.save(generateCategory());
        Category cat2 = categoryRepository.save(generateCategory());
        Product generateProduct = generateProduct();
        generateProduct.setCategories(List.of(cat1, cat2));
        Product product = productRepository.save(generateProduct);
        AddOrRemoveForm form = new AddOrRemoveForm(0L, List.of(0L, cat1.getId(), cat2.getId()));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/products/product/" + product.getId() + "/removing/category")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        ProductDto result = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), ProductDto.class);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(product.getId(), result.getId());
        Assertions.assertEquals(0, result.getCategories().size());
    }

    @Test
    public void removeNotExistCategoryTest() throws Exception{
        AddOrRemoveForm form = new AddOrRemoveForm(0L, List.of(0L, 1L, 2L));

        mockMvc.perform(MockMvcRequestBuilders.delete("/products/product/1/removing/category")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void removeNullDataCategoryTest() throws Exception{
        AddOrRemoveForm form = new AddOrRemoveForm();

        mockMvc.perform(MockMvcRequestBuilders.delete("/products/product/1/removing/category")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void removeNullCategoryTest() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.delete("/products/product/1/removing/category")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void addDiscountsTest() throws Exception {
        Product product = productRepository.save(generateProduct());
        Discount dis1 = discountRepository.save(generateDiscount());
        Discount dis2 = discountRepository.save(generateDiscount());
        AddOrRemoveForm form = new AddOrRemoveForm(0L, List.of(0L, dis1.getId(), dis2.getId()));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/products/product/" + product.getId() + "/new/discount")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        ProductDto result = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), ProductDto.class);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(product.getId(), result.getId());
        Assertions.assertEquals(2, result.getDiscounts().size());
        result.getDiscounts().forEach(dis ->
                Assertions.assertTrue(
                        Stream.of(dis1, dis2).anyMatch(c -> c.getId().equals(dis.getId()))
                )
        );
    }

    @Test
    public void addNotExistedDiscountsTest() throws Exception {
        AddOrRemoveForm form = new AddOrRemoveForm(0L, List.of(0L, 1L, 2L));

        mockMvc.perform(MockMvcRequestBuilders.post("/products/product/1/new/discount")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void addNullDataDiscountsTest() throws Exception {
        AddOrRemoveForm form = new AddOrRemoveForm();

        mockMvc.perform(MockMvcRequestBuilders.post("/products/product/1/new/discount")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void addNullDiscountsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/products/product/1/new/discount")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void removeDiscountsTest() throws Exception{
        Discount dis1 = discountRepository.save(generateDiscount());
        Discount dis2 = discountRepository.save(generateDiscount());
        Product generateProduct = generateProduct();
        generateProduct.setDiscounts(List.of(dis1, dis2));
        Product product = productRepository.save(generateProduct);
        AddOrRemoveForm form = new AddOrRemoveForm(0L, List.of(0L, dis1.getId(), dis2.getId()));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/products/product/" + product.getId() + "/removing/discount")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        ProductDto result = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), ProductDto.class);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(product.getId(), result.getId());
        Assertions.assertEquals(0, result.getCategories().size());
    }

    @Test
    public void removeNotExistDiscountsTest() throws Exception{
        AddOrRemoveForm form = new AddOrRemoveForm(0L, List.of(0L, 1L, 2L));

        mockMvc.perform(MockMvcRequestBuilders.delete("/products/product/1/removing/discount")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void removeNullDataDiscountsTest() throws Exception{
        AddOrRemoveForm form = new AddOrRemoveForm();

        mockMvc.perform(MockMvcRequestBuilders.delete("/products/product/1/removing/discount")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void removeNullDiscountsTest() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.delete("/products/product/1/removing/discount")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @NotNull
    private Product generateProduct() {
        Product product = new Product();
        Random random = new Random();
        product.setName(StringGenerator.generate(10));
        product.setDescription(StringGenerator.generate(30));
        product.setCode(StringGenerator.generate(64));
        product.setCreatedTime(LocalDateTime.now());
        product.setCount(random.nextInt(1, 100));
        product.setPrice(BigDecimal.valueOf(random.nextDouble(0.01, 99.99)));
        return product;
    }

    @NotNull
    private ProductDto generateProductDto() {
        ProductDto productDto = new ProductDto();
        Random random = new Random();
        productDto.setName(StringGenerator.generate(10));
        productDto.setDescription(StringGenerator.generate(30));
        productDto.setCode(StringGenerator.generate(64));
        productDto.setCreatedTime(LocalDateTime.now());
        productDto.setCount(random.nextInt(1, 100));
        productDto.setPrice(BigDecimal.valueOf(random.nextDouble(0.01, 99.99)));
        return productDto;
    }

    @NotNull
    private NewProductForm generateProductForm() {
        NewProductForm form = new NewProductForm();
        Random random = new Random();
        form.setName(StringGenerator.generate(10));
        form.setDescription(StringGenerator.generate(30));
        form.setCount(random.nextInt(1, 100));
        form.setPrice(BigDecimal.valueOf(random.nextDouble(0.01, 99.99)));
        return form;
    }

    @NotNull
    private Category generateCategory() {
        return new Category(null, StringGenerator.generate(10), null);
    }

    @NotNull
    private Discount generateDiscount() {
        Discount discount = new Discount();
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();
        discount.setName(StringGenerator.generate(10));
        discount.setDescription(StringGenerator.generate(30));
        discount.setDiscountValue(random.nextFloat());
        discount.setCreatedTime(now);
        discount.setActivationTime(now.plusSeconds(random.nextInt(1, 10)));
        discount.setEndingTime(now.plusSeconds(random.nextInt(11, 20)));
        return discount;    }

    private Boolean isProductModelAndDtoEquivalent(Product model, ProductDto dto) {
        return model.getId().equals(dto.getId()) &&
                model.getName().equals(dto.getName()) &&
                model.getDescription().equals(dto.getDescription()) &&
                model.getCode().equals(dto.getCode()) &&
                model.getCount().equals(dto.getCount());
    }

}
