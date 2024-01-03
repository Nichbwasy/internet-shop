package com.shop.seller.controller.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shop.authorization.client.TokensApiClient;
import com.shop.authorization.common.constant.UsersRoles;
import com.shop.authorization.common.constant.jwt.TokenStatus;
import com.shop.authorization.common.data.builder.AccessTokenUserInfoBuilder;
import com.shop.authorization.common.data.builder.JwtAuthenticationTokenDataDtoBuilder;
import com.shop.authorization.dto.token.AccessTokenUserInfoDto;
import com.shop.authorization.dto.token.JwtAuthenticationTokenDataDto;
import com.shop.product.client.ProductApiClient;
import com.shop.product.client.ProductCategoryApiClient;
import com.shop.product.client.ProductDiscountApiClient;
import com.shop.product.common.data.builder.CategoryDtoBuilder;
import com.shop.product.common.data.builder.DiscountDtoBuilder;
import com.shop.product.common.data.builder.ProductDtoBuilder;
import com.shop.product.dto.CategoryDto;
import com.shop.product.dto.DiscountDto;
import com.shop.product.dto.ProductDto;
import com.shop.product.dto.form.product.NewProductForm;
import com.shop.seller.common.test.data.builder.CreateProductFormBuilder;
import com.shop.seller.common.test.data.builder.SellerInfoBuilder;
import com.shop.seller.common.test.data.builder.SellerProductBuilder;
import com.shop.seller.common.test.data.builder.UpdateSellerProductFormBuilder;
import com.shop.seller.controller.RunSellerTestControllerApplication;
import com.shop.seller.controller.config.CommonSellerControllersTestConfiguration;
import com.shop.seller.controller.utils.mapper.SellerControllerTestMapper;
import com.shop.seller.dao.SellerInfoRepository;
import com.shop.seller.dao.SellerProductRepository;
import com.shop.seller.dto.control.CreateProductForm;
import com.shop.seller.dto.control.SellerProductDetailsDto;
import com.shop.seller.dto.control.UpdateSellerProductForm;
import com.shop.seller.model.SellerInfo;
import com.shop.seller.model.SellerProduct;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(classes = {RunSellerTestControllerApplication.class, CommonSellerControllersTestConfiguration.class})
public class SellerProductsControlPanelControllerTests {

    private final static JsonMapper jsonMapper = new JsonMapper();
    @Value("${test.access.token}")
    private String TEST_ACCESS_TOKEN;
    @Value("${admin.control.sellers.info.page.size}")
    private Integer SELLERS_INFO_PAGE_SIZE;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SellerProductRepository sellerProductRepository;
    @Autowired
    private SellerInfoRepository sellerInfoRepository;
    @Autowired
    private ProductApiClient productApiClient;
    @Autowired
    private TokensApiClient tokensApiClient;
    @Autowired
    private ProductCategoryApiClient productCategoryApiClient;
    @Autowired
    private ProductDiscountApiClient productDiscountApiClient;
    @Container
    private final static PostgreSQLContainer postgresContainer = new PostgreSQLContainer("postgres:14");

    @DynamicPropertySource
    public static void initProperties(DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        propertyRegistry.add("spring.datasource.username", postgresContainer::getUsername);
        propertyRegistry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeAll
    public static void init() {
        jsonMapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    public void mockSecurityJwtTokenFilter() {
        JwtAuthenticationTokenDataDto tokenDataDto = JwtAuthenticationTokenDataDtoBuilder
                .jwtAuthenticationTokenDataDto()
                .authenticated(true)
                .authorities(List.of(UsersRoles.ADMIN))
                .build();

        Mockito.when(tokensApiClient.validateAccessToken(Mockito.anyString()))
                .thenReturn(ResponseEntity.ok().body(TokenStatus.OK));
        Mockito.when(tokensApiClient.getAuthenticationFromToken(Mockito.anyString()))
                .thenReturn(ResponseEntity.ok().body(tokenDataDto));
    }

    @AfterEach
    public void clearDatabase() {
        sellerInfoRepository.deleteAll();
        sellerProductRepository.deleteAll();
    }

    @Test
    public void showAllSellersProductsDetailsTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        List<ProductDto> productDtos = buildPageOfProductsDto();
        SellerInfo sellerInfo = SellerInfoBuilder.sellerInfo()
                .id(null)
                .userId(userInfo.getUserId())
                .build();
        saveSellerProductsInDatabase(productDtos, sellerInfo);
        sellerInfoRepository.save(sellerInfo);

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok().body(userInfo));
        Mockito.when(productApiClient.getProductsByIds(1, productDtos.stream().map(ProductDto::getId).toList()))
                .thenReturn(ResponseEntity.ok().body(productDtos));

        String body = mockMvc.perform(MockMvcRequestBuilders.get("/seller/home/products/1")
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        List<SellerProductDetailsDto> result = jsonMapper.readValue(body, new TypeReference<>() {});

        Assertions.assertEquals(SELLERS_INFO_PAGE_SIZE, result.size());
        Assertions.assertTrue(result.stream().allMatch(prodDet ->
                productDtos.stream().anyMatch(productDto -> productDto.getId().equals(prodDet.getProductId()))
        ));
    }

    private void saveSellerProductsInDatabase(List<ProductDto> productDtos, SellerInfo sellerInfo) {
        productDtos.forEach(prod -> {
            SellerProduct product = SellerProductBuilder.sellerProduct().id(null).productId(prod.getId()).build();
            product = sellerProductRepository.save(product);
            sellerInfo.getProducts().add(product);
        });
    }

    @Test
    public void showAllSellersProductsDetailsZeroProductsTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        SellerInfo sellerInfo = SellerInfoBuilder.sellerInfo().userId(userInfo.getUserId()).build();
        sellerInfoRepository.save(sellerInfo);

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok().body(userInfo));
        Mockito.when(productApiClient.getProductsByIds(1, new ArrayList<>()))
                .thenReturn(ResponseEntity.ok().body(new ArrayList<>()));

        String body = mockMvc.perform(MockMvcRequestBuilders.get("/seller/home/products/1")
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<SellerProductDetailsDto> result = jsonMapper.readValue(body, new TypeReference<>() {});

        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void showAllSellersProductsDetailsClientExceptionTest() throws Exception {
        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenThrow(RuntimeException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/seller/home/products/1")
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    private List<ProductDto> buildPageOfProductsDto() {
        List<ProductDto> products = new ArrayList<>();
        for (long i = 1; i < SELLERS_INFO_PAGE_SIZE + 1; i++) {
            products.add(ProductDtoBuilder.productDto().id(i).build());
        }
        return products;
    }

    @Test
    public void showSellerProductDetailsTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        ProductDto productDto = ProductDtoBuilder.productDto().build();
        SellerProduct selProd = SellerProductBuilder.sellerProduct().productId(productDto.getId()).build();
        selProd = sellerProductRepository.save(selProd);
        SellerInfo sellerInfo = SellerInfoBuilder.sellerInfo()
                .userId(userInfo.getUserId())
                .products(List.of(selProd))
                .build();
        sellerInfoRepository.save(sellerInfo);

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok().body(userInfo));
        Mockito.when(productApiClient.getProduct(productDto.getId())).thenReturn(ResponseEntity.ok().body(productDto));

        String body = mockMvc.perform(MockMvcRequestBuilders.get("/seller/home/products/product/" + selProd.getId())
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        SellerProductDetailsDto result = jsonMapper.readValue(body, SellerProductDetailsDto.class);

        Assertions.assertEquals(selProd.getId(), result.getId());
        Assertions.assertEquals(productDto.getId(), result.getProductId());
        Assertions.assertTrue(sellerInfo.getProducts().stream().anyMatch(prod -> prod.getId().equals(result.getId())));
    }

    @Test
    public void showSellerProductDetailsNotBelongToSellerTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        ProductDto productDto = ProductDtoBuilder.productDto().build();
        SellerProduct selProd = SellerProductBuilder.sellerProduct().productId(productDto.getId()).build();
        selProd = sellerProductRepository.save(selProd);
        SellerInfo sellerInfo = SellerInfoBuilder.sellerInfo().userId(userInfo.getUserId()).build();
        sellerInfoRepository.save(sellerInfo);

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok().body(userInfo));

        mockMvc.perform(MockMvcRequestBuilders.get("/seller/home/products/product/" + selProd.getId())
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void showSellerProductDetailsSellerNotFoundTest() throws Exception {
        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenThrow(RuntimeException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/seller/home/products/product/1")
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void addNewProductTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        CreateProductForm form = CreateProductFormBuilder.createProductForm().build();
        SellerInfo sellerInfo = SellerInfoBuilder.sellerInfo().userId(userInfo.getUserId()).build();
        sellerInfoRepository.save(sellerInfo);

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString()))
                .thenReturn(ResponseEntity.ok().body(userInfo));
        Mockito.when(productApiClient.createProduct(Mockito.any(NewProductForm.class)))
                .thenAnswer(a -> {
                    ProductDto product = SellerControllerTestMapper.INSTANCE.mapNewProductFormToDto(a.getArgument(0));
                    product.setId(1L);
                    return ResponseEntity.ok().body(product);
                });

        String body = mockMvc.perform(MockMvcRequestBuilders.post("/seller/home/products")
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        SellerProductDetailsDto result = jsonMapper.readValue(body, SellerProductDetailsDto.class);

        Assertions.assertNotNull(result.getId());
        Assertions.assertNotNull(result.getProductId());
        Assertions.assertEquals(form.getName(), result.getName());
        Assertions.assertEquals(form.getPrice(), result.getPrice());
        Assertions.assertEquals(form.getCount(), result.getCount());
    }

    @Test
    public void addNewProductToNotExistedSellerTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        CreateProductForm form = CreateProductFormBuilder.createProductForm().build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString()))
                .thenReturn(ResponseEntity.ok().body(userInfo));
        Mockito.when(productApiClient.createProduct(Mockito.any(NewProductForm.class)))
                .thenAnswer(a -> {
                    ProductDto product = SellerControllerTestMapper.INSTANCE.mapNewProductFormToDto(a.getArgument(0));
                    product.setId(1L);
                    return ResponseEntity.ok().body(product);
                });

        mockMvc.perform(MockMvcRequestBuilders.post("/seller/home/products")
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void addNewProductClientExceptionTest() throws Exception {
        CreateProductForm form = CreateProductFormBuilder.createProductForm().build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenThrow(RuntimeException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/seller/home/products")
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void removeProductTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        SellerInfo sellerInfo = SellerInfoBuilder.sellerInfo()
                .userId(userInfo.getUserId())
                .products(List.of(
                        sellerProductRepository.save(SellerProductBuilder.sellerProduct().build()),
                        sellerProductRepository.save(SellerProductBuilder.sellerProduct().build())
                ))
                .build();
        Long idToRemove = sellerInfo.getProducts().get(0).getId();
        sellerInfoRepository.save(sellerInfo);

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok().body(userInfo));
        Mockito.when(productApiClient.removeProduct(Mockito.anyLong()))
                .thenAnswer(a -> ResponseEntity.ok().body(a.getArgument(0)));

        String body = mockMvc.perform(MockMvcRequestBuilders.delete("/seller/home/products/product/" + idToRemove)
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        Long result = jsonMapper.readValue(body, Long.class);

        Assertions.assertEquals(idToRemove, result);
    }

    @Test
    public void removeProductNotBelongSellerTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        SellerInfo sellerInfo = SellerInfoBuilder.sellerInfo()
                .userId(userInfo.getUserId())
                .products(List.of(
                        sellerProductRepository.save(SellerProductBuilder.sellerProduct().build())
                ))
                .build();
        long idToRemove = sellerInfo.getProducts().get(0).getId() + 1;
        sellerInfoRepository.save(sellerInfo);

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok().body(userInfo));
        Mockito.when(productApiClient.removeProduct(Mockito.anyLong()))
                .thenAnswer(a -> ResponseEntity.ok().body(a.getArgument(0)));

        mockMvc.perform(MockMvcRequestBuilders.delete("/seller/home/products/product/" + idToRemove)
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void removeProductProductClientExceptionTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        SellerInfo sellerInfo = SellerInfoBuilder.sellerInfo()
                .userId(userInfo.getUserId())
                .products(List.of(
                        sellerProductRepository.save(SellerProductBuilder.sellerProduct().build())
                ))
                .build();
        long idToRemove = sellerInfo.getProducts().get(0).getId() + 1;
        sellerInfoRepository.save(sellerInfo);

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok().body(userInfo));
        Mockito.when(productApiClient.removeProduct(Mockito.anyLong())).thenThrow(RuntimeException.class);

        mockMvc.perform(MockMvcRequestBuilders.delete("/seller/home/products/product/" + idToRemove)
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void removeProductTokenClientExceptionTest() throws Exception {
        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenThrow(RuntimeException.class);

        mockMvc.perform(MockMvcRequestBuilders.delete("/seller/home/products/product/1")
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void updateProductTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        ProductDto productDto = ProductDtoBuilder.productDto().build();
        SellerProduct product1 = sellerProductRepository.save(SellerProductBuilder.sellerProduct().productId(productDto.getId()).build());
        SellerProduct product2 = sellerProductRepository.save(SellerProductBuilder.sellerProduct().build());
        SellerInfo sellerInfo = SellerInfoBuilder.sellerInfo()
                .userId(userInfo.getUserId())
                .products(List.of(product1, product2))
                .build();
        sellerInfo = sellerInfoRepository.save(sellerInfo);
        List<CategoryDto> categories = List.of(CategoryDtoBuilder.categoryDto().build(), CategoryDtoBuilder.categoryDto().build());
        List<DiscountDto> discounts = List.of(DiscountDtoBuilder.discountDto().build(), DiscountDtoBuilder.discountDto().build());
        List<Long> categoryIds = categories.stream().map(CategoryDto::getId).toList();
        List<Long> discountIds = discounts.stream().map(DiscountDto::getId).toList();
        UpdateSellerProductForm form = UpdateSellerProductFormBuilder.updateSellerProductForm()
                .sellerProductId(sellerInfo.getProducts().get(0).getId())
                .discountIds(discountIds)
                .categoryIds(categoryIds)
                .build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok().body(userInfo));
        Mockito.when(productApiClient.getProduct(productDto.getId())).thenReturn(ResponseEntity.ok().body(productDto));
        Mockito.when(productCategoryApiClient.getCategoriesByIds(categoryIds)).thenReturn(ResponseEntity.ok().body(categories));
        Mockito.when(productDiscountApiClient.getDiscountsByIds(discountIds)).thenReturn(ResponseEntity.ok().body(discounts));
        Mockito.when(productApiClient.updateProduct(Mockito.eq(productDto.getId()), Mockito.any(ProductDto.class)))
                .thenAnswer(a -> ResponseEntity.ok().body(a.getArgument(1)));

        String body = mockMvc.perform(MockMvcRequestBuilders.patch("/seller/home/products/product/"
                                                                            + sellerInfo.getProducts().get(0).getId())
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(form)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        SellerProductDetailsDto result = jsonMapper.readValue(body, SellerProductDetailsDto.class);

        Assertions.assertEquals(form.getName(), result.getName());
        Assertions.assertEquals(form.getCount(), result.getCount());
        Assertions.assertEquals(form.getPrice(), result.getPrice());
        Assertions.assertEquals(form.getSellerProductId(), result.getId());
        Assertions.assertEquals(form.getDescription(), result.getDescription());
    }

    @Test
    public void updateProductClientExceptionTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        ProductDto productDto = ProductDtoBuilder.productDto().build();
        SellerProduct product1 = sellerProductRepository.save(SellerProductBuilder.sellerProduct().productId(productDto.getId()).build());
        SellerProduct product2 = sellerProductRepository.save(SellerProductBuilder.sellerProduct().build());
        SellerInfo sellerInfo = SellerInfoBuilder.sellerInfo()
                .userId(userInfo.getUserId())
                .products(List.of(product1, product2))
                .build();
        sellerInfo = sellerInfoRepository.save(sellerInfo);
        UpdateSellerProductForm form = UpdateSellerProductFormBuilder.updateSellerProductForm()
                .sellerProductId(sellerInfo.getProducts().get(0).getId())
                .build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok().body(userInfo));
        Mockito.when(productApiClient.getProduct(Mockito.anyLong())).thenThrow(RuntimeException.class);

        mockMvc.perform(MockMvcRequestBuilders.patch("/seller/home/products/product/"
                                + sellerInfo.getProducts().get(0).getId())
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(form)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void updateProductNotBelongSellerTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        SellerProduct product1 = sellerProductRepository.save(SellerProductBuilder.sellerProduct().build());
        SellerProduct product2 = sellerProductRepository.save(SellerProductBuilder.sellerProduct().build());
        SellerInfo sellerInfo = SellerInfoBuilder.sellerInfo()
                .userId(userInfo.getUserId())
                .products(List.of(product1, product2))
                .build();
        sellerInfoRepository.save(sellerInfo);
        UpdateSellerProductForm form = UpdateSellerProductFormBuilder.updateSellerProductForm().build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok().body(userInfo));
        Mockito.when(productApiClient.getProduct(Mockito.anyLong())).thenThrow(RuntimeException.class);

        mockMvc.perform(MockMvcRequestBuilders.patch("/seller/home/products/product/1001")
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(form)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void updateProductSellerNotFoundTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok().body(userInfo));

        mockMvc.perform(MockMvcRequestBuilders.patch("/seller/home/products/product/1001")
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(UpdateSellerProductFormBuilder.updateSellerProductForm().build())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

}
