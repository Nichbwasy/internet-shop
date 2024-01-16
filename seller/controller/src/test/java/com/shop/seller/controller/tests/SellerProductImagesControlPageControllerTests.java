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
import com.shop.media.common.data.builder.ImgMetadataDtoBuilder;
import com.shop.media.dto.ProductMediaDto;
import com.shop.media.dto.form.AddMediaToProductForm;
import com.shop.product.client.ProductApiClient;
import com.shop.seller.common.test.data.builder.SellerInfoBuilder;
import com.shop.seller.common.test.data.builder.SellerProductBuilder;
import com.shop.seller.controller.RunSellerTestControllerApplication;
import com.shop.seller.controller.config.CommonSellerControllersTestConfiguration;
import com.shop.seller.dao.SellerInfoRepository;
import com.shop.seller.dao.SellerProductRepository;
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
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(classes = {RunSellerTestControllerApplication.class, CommonSellerControllersTestConfiguration.class})
public class SellerProductImagesControlPageControllerTests {

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
    public void getAllProductImgsTest() throws Exception {
        SellerProduct product = sellerProductRepository.save(SellerProductBuilder.sellerProduct().build());
        SellerInfo sellerInfo = sellerInfoRepository.save(SellerInfoBuilder.sellerInfo().products(List.of(product)).build());
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().userId(sellerInfo.getUserId()).build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));
        Mockito.when(productMediaApiClient.loadAllProductsImages(product.getProductId()))
                .thenReturn(ResponseEntity.ok(List.of(new byte[1])));

        mockMvc.perform(MockMvcRequestBuilders.get("/seller/home/products/" + product.getId() + "/imgs")
                .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));
    }

    @Test
    public void getAllProductImgsNotBelongTest() throws Exception {
        SellerInfo sellerInfo = sellerInfoRepository.save(SellerInfoBuilder.sellerInfo().build());
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().userId(sellerInfo.getUserId()).build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));

        mockMvc.perform(MockMvcRequestBuilders.get("/seller/home/products/1001/imgs")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getAllProductImgsNotExistsTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));

        mockMvc.perform(MockMvcRequestBuilders.get("/seller/home/products/1001/imgs")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void uploadImageTest() throws Exception {
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
        Mockito.when(productMediaApiClient.addImageToProduct(Mockito.eq(product.getProductId()), Mockito.any(MultipartFile.class)))
                .thenReturn(ResponseEntity.ok(new ProductMediaDto()));

        String body = mockMvc.perform(MockMvcRequestBuilders.post("/seller/home/products/" + product.getId() + "/imgs")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .flashAttr("form", form))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        ProductMediaDto result = jsonMapper.readValue(body, ProductMediaDto.class);

        Assertions.assertNotNull(result);
    }

    @Test
    public void uploadImageNotBelongTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        sellerInfoRepository.save(SellerInfoBuilder.sellerInfo()
                .userId(userInfo.getUserId())
                .build()
        );
        AddMediaToProductForm form = AddMediaToProductFormBuilder.createProductMediaForm()
                .multipartFile(Mockito.mock(MultipartFile.class))
                .build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));

       mockMvc.perform(MockMvcRequestBuilders.post("/seller/home/products/" + form.getProductMediaId() + "/imgs")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .flashAttr("form", form))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void uploadImageNotExistsTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        AddMediaToProductForm form = AddMediaToProductFormBuilder.createProductMediaForm()
                .multipartFile(Mockito.mock(MultipartFile.class))
                .build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));

        mockMvc.perform(MockMvcRequestBuilders.post("/seller/home/products/" + form.getProductMediaId() + "/imgs")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .flashAttr("form", form))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void deleteProductImageTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        SellerProduct product = sellerProductRepository.save(SellerProductBuilder.sellerProduct().build());
        sellerInfoRepository.save(SellerInfoBuilder.sellerInfo()
                .userId(userInfo.getUserId())
                .products(List.of(product))
                .build()
        );

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));
        Mockito.when(productMediaApiClient.removeImageFromProduct(Mockito.eq(product.getProductId()), Mockito.anyLong()))
                .thenAnswer(a -> ResponseEntity.ok(a.getArgument(1)));

        mockMvc.perform(MockMvcRequestBuilders.delete("/seller/home/products/" + product.getId() + "/imgs/1001")
                .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(1001L));
    }

    @Test
    public void deleteProductImageNotBelongTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        sellerInfoRepository.save(SellerInfoBuilder.sellerInfo()
                .userId(userInfo.getUserId())
                .build()
        );

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));

        mockMvc.perform(MockMvcRequestBuilders.delete("/seller/home/products/1001/imgs/1001")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void deleteProductImageNotExistsTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));

        mockMvc.perform(MockMvcRequestBuilders.delete("/seller/home/products/1001/imgs/1001")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getProductImagesMetadataTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        SellerProduct product = sellerProductRepository.save(SellerProductBuilder.sellerProduct().build());
        sellerInfoRepository.save(SellerInfoBuilder.sellerInfo()
                .userId(userInfo.getUserId())
                .products(List.of(product))
                .build()
        );

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));
        Mockito.when(productMediaApiClient.getProductImagesMetadata(product.getProductId()))
                .thenReturn(ResponseEntity.ok(List.of(ImgMetadataDtoBuilder.imgMetadataDto().build())));

        mockMvc.perform(MockMvcRequestBuilders.get("/seller/home/products/" + product.getId() + "/imgs/data")
                .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));
    }

    @Test
    public void getProductImagesMetadataNotBelongTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        sellerInfoRepository.save(SellerInfoBuilder.sellerInfo()
                .userId(userInfo.getUserId())
                .build()
        );

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));

        mockMvc.perform(MockMvcRequestBuilders.get("/seller/home/products/1001/imgs/data")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getProductImagesMetadataNotExistsTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));

        mockMvc.perform(MockMvcRequestBuilders.get("/seller/home/products/1001/imgs/data")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getProductImageMetadataTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        SellerProduct product = sellerProductRepository.save(SellerProductBuilder.sellerProduct().build());
        sellerInfoRepository.save(SellerInfoBuilder.sellerInfo()
                .userId(userInfo.getUserId())
                .products(List.of(product))
                .build()
        );

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));
        Mockito.when(productMediaApiClient.getProductImageMetadata(Mockito.eq(product.getProductId()), Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok(ImgMetadataDtoBuilder.imgMetadataDto().build()));

        mockMvc.perform(MockMvcRequestBuilders.get("/seller/home/products/" + product.getId() + "/imgs/data/1001")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty());
    }

    @Test
    public void getProductImageMetadataNotBelongTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        sellerInfoRepository.save(SellerInfoBuilder.sellerInfo().userId(userInfo.getUserId()).build());

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));

        mockMvc.perform(MockMvcRequestBuilders.get("/seller/home/products/1001/imgs/data/1001")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getProductImageMetadataNotExistsTest() throws Exception {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok(userInfo));

        mockMvc.perform(MockMvcRequestBuilders.get("/seller/home/products/1001/imgs/data/1001")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

}
