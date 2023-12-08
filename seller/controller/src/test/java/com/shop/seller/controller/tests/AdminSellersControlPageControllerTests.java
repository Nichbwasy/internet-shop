package com.shop.seller.controller.tests;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shop.authorization.client.TokensApiClient;
import com.shop.authorization.client.UserDataApiClient;
import com.shop.authorization.common.constant.UsersRoles;
import com.shop.authorization.common.constant.jwt.TokenStatus;
import com.shop.authorization.common.data.builder.JwtAuthenticationTokenDataDtoBuilder;
import com.shop.authorization.common.data.builder.SellerUserDataDtoBuilder;
import com.shop.authorization.dto.api.user.SellerUserDataDto;
import com.shop.authorization.dto.token.JwtAuthenticationTokenDataDto;
import com.shop.product.client.ProductApiClient;
import com.shop.seller.common.test.data.builder.RegisterNewSellerFormBuilder;
import com.shop.seller.common.test.data.builder.SellerInfoBuilder;
import com.shop.seller.common.test.data.builder.SellerProductBuilder;
import com.shop.seller.controller.RunSellerTestControllerApplication;
import com.shop.seller.controller.config.CommonSellerControllersTestConfiguration;
import com.shop.seller.dao.SellerInfoRepository;
import com.shop.seller.dao.SellerProductRepository;
import com.shop.seller.dto.SellerInfoDto;
import com.shop.seller.dto.control.RegisterNewSellerForm;
import com.shop.seller.dto.control.SellerDetailsDto;
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
public class AdminSellersControlPageControllerTests {
    private final static JsonMapper jsonMapper = new JsonMapper();
    @Value("${test.access.token}")
    private String TEST_ACCESS_TOKEN ;
    @Value("${admin.control.sellers.info.page.size}")
    private Integer ADMIN_SELLERS_INFO_PAGE_SIZE;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TokensApiClient tokensApiClient;
    @Autowired
    private UserDataApiClient userDataApiClient;
    @Autowired
    private ProductApiClient productApiClient;
    @Autowired
    private SellerInfoRepository sellerInfoRepository;
    @Autowired
    private SellerProductRepository sellerProductRepository;
    @Container
    private final static PostgreSQLContainer postgresContainer = new PostgreSQLContainer("postgres:14");

    @DynamicPropertySource
    public static void registerProperty(DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        propertyRegistry.add("spring.datasource.username", postgresContainer::getUsername);
        propertyRegistry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeAll
    public static void init() {
        jsonMapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    public void mockSecurityJwtTokenClient() {
        JwtAuthenticationTokenDataDto tokenDataDto = JwtAuthenticationTokenDataDtoBuilder.jwtAuthenticationTokenDataDto()
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
    public void getSellersPageTest() throws Exception{
        List<SellerInfo> sellerInfos = new ArrayList<>();
        for (int i = 0; i < ADMIN_SELLERS_INFO_PAGE_SIZE + 1; i++)
            sellerInfos.add(sellerInfoRepository.save(SellerInfoBuilder.sellerInfo().build()));

        String body = mockMvc.perform(MockMvcRequestBuilders.get("/admin/control/sellers/1")
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<SellerInfoDto> result = jsonMapper.readValue(body, new TypeReference<>() {});

        Assertions.assertEquals(ADMIN_SELLERS_INFO_PAGE_SIZE, result.size());
        result.forEach(sellerInfoDto ->
                Assertions.assertTrue(
                        sellerInfos.stream().anyMatch(sellerInfo -> sellerInfo.getId().equals(sellerInfoDto.getId()))
                )
        );
    }

    @Test
    public void getSellersNoDataPageTest() throws Exception{
        String body = mockMvc.perform(MockMvcRequestBuilders.get("/admin/control/sellers/1")
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<SellerInfoDto> result = jsonMapper.readValue(body, new TypeReference<>() {});

        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void getSellerInfoTest() throws Exception {
        SellerUserDataDto sellerUserDataDto = SellerUserDataDtoBuilder.sellerUserDataDto().build();
        SellerInfo sellerInfo = SellerInfoBuilder.sellerInfo().userId(sellerUserDataDto.getId()).build();
        sellerInfo = sellerInfoRepository.save(sellerInfo);

        Mockito.when(userDataApiClient.getSellerInfo(sellerInfo.getUserId()))
                .thenReturn(ResponseEntity.ok().body(sellerUserDataDto));

        String body = mockMvc.perform(MockMvcRequestBuilders.get("/admin/control/sellers/seller/" + sellerInfo.getId())
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        SellerDetailsDto result = jsonMapper.readValue(body, SellerDetailsDto.class);

        Assertions.assertEquals(sellerInfo.getId(), result.getId());
        Assertions.assertEquals(sellerUserDataDto.getId(), result.getUserId());
    }

    @Test
    public void getNotExistedSellerInfoTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/control/sellers/seller/1")
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void registerNewSellerTest() throws Exception {
        SellerUserDataDto sellerData = SellerUserDataDtoBuilder.sellerUserDataDto().build();
        RegisterNewSellerForm form = RegisterNewSellerFormBuilder.registerNewSellerForm()
                .userId(sellerData.getId()).build();

        Mockito.when(userDataApiClient.makeUserSeller(sellerData.getId())).thenReturn(ResponseEntity.ok().body(sellerData));

        String body = mockMvc.perform(MockMvcRequestBuilders.post("/admin/control/sellers/new")
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        SellerDetailsDto result = jsonMapper.readValue(body, SellerDetailsDto.class);

        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals(sellerData.getId(), result.getUserId());
    }

    @Test
    public void registerAlreadyExistedSellerTest() throws Exception {
        SellerUserDataDto sellerData = SellerUserDataDtoBuilder.sellerUserDataDto().build();
        SellerInfo sellerInfo = SellerInfoBuilder.sellerInfo().userId(sellerData.getId()).build();
        sellerInfoRepository.save(sellerInfo);
        RegisterNewSellerForm form = RegisterNewSellerFormBuilder.registerNewSellerForm()
                .userId(sellerData.getId()).build();

        Mockito.when(userDataApiClient.makeUserSeller(sellerData.getId())).thenReturn(ResponseEntity.ok().body(sellerData));

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/control/sellers/new")
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void registerSellerNullDataTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/control/sellers/new")
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void removeSellerFromSystemTest() throws Exception {
        SellerInfo sellerInfo = SellerInfoBuilder.sellerInfo().build();
        for (long i = 1; i  < 4; i++) {
            SellerProduct sellerProduct = SellerProductBuilder.sellerProduct().build();
            sellerProduct = sellerProductRepository.save(sellerProduct);
            sellerInfo.getProducts().add(sellerProduct);
        }
        sellerInfo = sellerInfoRepository.save(sellerInfo);

        Mockito.when(productApiClient.removeProducts(Mockito.anyList()))
                .thenAnswer(a -> ResponseEntity.ok().body(a.getArgument(0)));

        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/control/sellers/seller/" + sellerInfo.getId())
                .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(sellerInfo.getId()));

    }

    @Test
    public void removeNotExistedSellerFromSystemTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/control/sellers/seller/1")
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

    }

}