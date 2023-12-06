package com.shop.seller.controller.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shop.authorization.client.TokensApiClient;
import com.shop.authorization.common.constant.UsersRoles;
import com.shop.authorization.common.constant.jwt.TokenStatus;
import com.shop.authorization.dto.token.AccessTokenUserInfoDto;
import com.shop.authorization.dto.token.JwtAuthenticationTokenDataDto;
import com.shop.product.client.ProductApiClient;
import com.shop.product.dto.ProductDto;
import com.shop.seller.controller.RunSellerTestControllerApplication;
import com.shop.seller.controller.config.CommonSellerControllersTestConfiguration;
import com.shop.seller.controller.utils.GenTestData;
import com.shop.seller.dao.SellerInfoRepository;
import com.shop.seller.dao.SellerProductRepository;
import com.shop.seller.dto.SellerInfoDto;
import com.shop.seller.dto.control.SellerProductDetailsDto;
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
    public void clearDatabase() {
        sellerInfoRepository.deleteAll();
        sellerProductRepository.deleteAll();
    }

    @Test
    public void showAllSellersProductsDetailsTest() throws Exception {
        AccessTokenUserInfoDto userInfo = GenTestData.generateUserInfo();
        SellerInfo sellerInfo = GenTestData.generateSellerInfo();
        sellerInfo.setUserId(userInfo.getUserId());
        List<ProductDto> products = new ArrayList<>();
        addProductsToSeller(products, sellerInfo);
        sellerInfoRepository.save(sellerInfo);

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok().body(userInfo));
        Mockito.when(productApiClient.getProductsByIds(1, products.stream().map(ProductDto::getId).toList()))
                .thenReturn(ResponseEntity.ok().body(products));

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
                products.stream().anyMatch(productDto -> productDto.getId().equals(prodDet.getProductId()))
        ));
    }

    @Test
    public void showAllSellersProductsDetailsZeroProductsTest() throws Exception {
        AccessTokenUserInfoDto userInfo = GenTestData.generateUserInfo();
        SellerInfo sellerInfo = GenTestData.generateSellerInfo();
        sellerInfo.setUserId(userInfo.getUserId());
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

    private void addProductsToSeller(List<ProductDto> products, SellerInfo sellerInfo) {
        for (int i = 0; i < SELLERS_INFO_PAGE_SIZE; i++) {
            SellerProduct sellerProduct = GenTestData.generateSellerProduct();
            ProductDto productDto = GenTestData.generateProduct();
            sellerProduct.setProductId(productDto.getId());
            sellerProductRepository.save(sellerProduct);
            products.add(productDto);
            sellerInfo.getProducts().add(sellerProduct);
        }
    }

    @Test
    public void showSellerProductDetailsTest() throws Exception {
        AccessTokenUserInfoDto userInfo = GenTestData.generateUserInfo();
        ProductDto productDto = GenTestData.generateProduct();
        SellerProduct selProd = GenTestData.generateSellerProduct();
        selProd.setProductId(productDto.getId());
        selProd = sellerProductRepository.save(selProd);
        SellerInfo sellerInfo = GenTestData.generateSellerInfo();
        sellerInfo.setUserId(userInfo.getUserId());
        sellerInfo.setProducts(List.of(selProd));
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
        AccessTokenUserInfoDto userInfo = GenTestData.generateUserInfo();
        ProductDto productDto = GenTestData.generateProduct();
        SellerProduct selProd = GenTestData.generateSellerProduct();
        selProd.setProductId(productDto.getId());
        selProd = sellerProductRepository.save(selProd);
        SellerInfo sellerInfo = GenTestData.generateSellerInfo();
        sellerInfo.setUserId(userInfo.getUserId());
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

}
