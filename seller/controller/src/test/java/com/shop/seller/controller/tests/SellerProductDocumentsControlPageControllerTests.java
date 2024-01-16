package com.shop.seller.controller.tests;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shop.authorization.client.TokensApiClient;
import com.shop.authorization.common.constant.UsersRoles;
import com.shop.authorization.common.constant.jwt.TokenStatus;
import com.shop.authorization.common.data.builder.AccessTokenUserInfoBuilder;
import com.shop.authorization.common.data.builder.JwtAuthenticationTokenDataDtoBuilder;
import com.shop.authorization.dto.token.AccessTokenUserInfoDto;
import com.shop.authorization.dto.token.JwtAuthenticationTokenDataDto;
import com.shop.media.client.ProductMediaApiClient;
import com.shop.media.common.data.builder.AddMediaToProductFormBuilder;
import com.shop.media.common.data.builder.DockMetadataDtoBuilder;
import com.shop.media.common.data.builder.ProductMediaDtoBuilder;
import com.shop.media.dto.form.AddMediaToProductForm;
import com.shop.product.client.ProductApiClient;
import com.shop.seller.common.test.data.builder.SellerInfoBuilder;
import com.shop.seller.common.test.data.builder.SellerProductBuilder;
import com.shop.seller.controller.RunSellerTestControllerApplication;
import com.shop.seller.controller.config.CommonSellerControllersTestConfiguration;
import com.shop.seller.dao.SellerInfoRepository;
import com.shop.seller.dao.SellerProductRepository;
import com.shop.seller.model.SellerProduct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(classes = {RunSellerTestControllerApplication.class, CommonSellerControllersTestConfiguration.class})
public class SellerProductDocumentsControlPageControllerTests {

    private final static JsonMapper jsonMapper = new JsonMapper();
    @Value("${test.access.token}")
    private String testAccessToken;
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
    private ProductMediaApiClient productMediaApiClient;
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
    public void loadProductDockFileTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        SellerProduct product = sellerProductRepository.save(SellerProductBuilder.sellerProduct().build());
        sellerInfoRepository.save(SellerInfoBuilder.sellerInfo()
                .userId(userInfo.getUserId())
                .products(List.of(product))
                .build()
        );

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));
        Mockito.when(productMediaApiClient.loadProductDock(Mockito.eq(product.getProductId()), Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok(new byte[1]));

        mockMvc.perform(MockMvcRequestBuilders.get("/seller/home/products/" + product.getId() + "/docs/1001")
                .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty());
    }

    @Test
    public void loadProductDockFileNotBelongTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        sellerInfoRepository.save(SellerInfoBuilder.sellerInfo()
                .userId(userInfo.getUserId())
                .build()
        );

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));

        mockMvc.perform(MockMvcRequestBuilders.get("/seller/home/products/1001/docs/1001")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void loadProductDockFileNotExistsTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));

        mockMvc.perform(MockMvcRequestBuilders.get("/seller/home/products/1001/docs/1001")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getAllSellersProductDocksMetadataTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        SellerProduct product = sellerProductRepository.save(SellerProductBuilder.sellerProduct().build());
        sellerInfoRepository.save(SellerInfoBuilder.sellerInfo()
                .userId(userInfo.getUserId())
                .products(List.of(product))
                .build()
        );

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));
        Mockito.when(productMediaApiClient.getProductDocksMetadata(product.getProductId()))
                .thenReturn(ResponseEntity.ok(List.of(DockMetadataDtoBuilder.dockMetadataDto().build())));

        mockMvc.perform(MockMvcRequestBuilders.get("/seller/home/products/" + product.getId() + "/docs/data")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));
    }

    @Test
    public void getAllSellersProductDocksMetadataNotBelongTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        sellerInfoRepository.save(SellerInfoBuilder.sellerInfo()
                .userId(userInfo.getUserId())
                .build()
        );

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));

        mockMvc.perform(MockMvcRequestBuilders.get("/seller/home/products/1001/docs/data")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getAllSellersProductDocksMetadataNotExistsTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));

        mockMvc.perform(MockMvcRequestBuilders.get("/seller/home/products/1001/docs/data")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getSellersProductDockMetadataTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        SellerProduct product = sellerProductRepository.save(SellerProductBuilder.sellerProduct().build());
        sellerInfoRepository.save(SellerInfoBuilder.sellerInfo()
                .userId(userInfo.getUserId())
                .products(List.of(product))
                .build()
        );

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));
        Mockito.when(productMediaApiClient.getProductDockMetadata(Mockito.eq(product.getProductId()), Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok(DockMetadataDtoBuilder.dockMetadataDto().build()));

        mockMvc.perform(MockMvcRequestBuilders.get("/seller/home/products/" + product.getId() + "/docs/data/1001")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty());
    }

    @Test
    public void getSellersProductDockMetadataNotBelongTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        sellerInfoRepository.save(SellerInfoBuilder.sellerInfo()
                .userId(userInfo.getUserId())
                .build()
        );

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));

        mockMvc.perform(MockMvcRequestBuilders.get("/seller/home/products/1001/docs/data/1001")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getSellersProductDockMetadataNotExistsTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));

        mockMvc.perform(MockMvcRequestBuilders.get("/seller/home/products/1001/docs/data/1001")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void uploadProductDockTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        SellerProduct product = sellerProductRepository.save(SellerProductBuilder.sellerProduct().build());
        sellerInfoRepository.save(SellerInfoBuilder.sellerInfo()
                .userId(userInfo.getUserId())
                .products(List.of(product))
                .build()
        );
        AddMediaToProductForm form = AddMediaToProductFormBuilder.createProductMediaForm()
                .productId(product.getId())
                .multipartFile(Mockito.mock(MultipartFile.class))
                .build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));
        Mockito.when(productMediaApiClient.addDockToProduct(Mockito.eq(product.getProductId()), Mockito.any(MultipartFile.class)))
                .thenReturn(ResponseEntity.ok(ProductMediaDtoBuilder.productMediaDto().build()));

        mockMvc.perform(MockMvcRequestBuilders.post("/seller/home/products/" + product.getId() + "/docs")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("form", form))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty());
    }

    @Test
    public void uploadProductDockNotBelongTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        sellerInfoRepository.save(SellerInfoBuilder.sellerInfo().userId(userInfo.getUserId()).build());
        AddMediaToProductForm form = AddMediaToProductFormBuilder.createProductMediaForm()
                .multipartFile(Mockito.mock(MultipartFile.class))
                .build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));

        mockMvc.perform(MockMvcRequestBuilders.post("/seller/home/products/1001/docs")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("form", form))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void uploadProductDockNotExistsTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        AddMediaToProductForm form = AddMediaToProductFormBuilder.createProductMediaForm()
                .multipartFile(Mockito.mock(MultipartFile.class))
                .build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));

        mockMvc.perform(MockMvcRequestBuilders.post("/seller/home/products/1001/docs")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("form", form))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void deleteProductDockTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        SellerProduct product = sellerProductRepository.save(SellerProductBuilder.sellerProduct().build());
        sellerInfoRepository.save(SellerInfoBuilder.sellerInfo()
                .userId(userInfo.getUserId())
                .products(List.of(product))
                .build()
        );

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));
        Mockito.when(productMediaApiClient.removeDockFormProduct(Mockito.eq(product.getProductId()), Mockito.anyLong()))
                .thenAnswer(a -> ResponseEntity.ok(a.getArgument(1)));

        mockMvc.perform(MockMvcRequestBuilders.delete("/seller/home/products/" + product.getId() + "/docs/1001")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(1001L));
    }

    @Test
    public void deleteProductDockNotBelongTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        sellerInfoRepository.save(SellerInfoBuilder.sellerInfo().userId(userInfo.getUserId()).build());

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));

        mockMvc.perform(MockMvcRequestBuilders.delete("/seller/home/products/1001/docs/1001")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void deleteProductDockNotExistsTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));

        mockMvc.perform(MockMvcRequestBuilders.delete("/seller/home/products/1001/docs/1001")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

}
