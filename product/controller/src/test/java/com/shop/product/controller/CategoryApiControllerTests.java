package com.shop.product.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shop.authorization.client.TokensApiClient;
import com.shop.authorization.common.constant.UsersRoles;
import com.shop.authorization.common.constant.jwt.TokenStatus;
import com.shop.authorization.dto.token.JwtAuthenticationTokenDataDto;
import com.shop.product.common.data.builder.CategoryBuilder;
import com.shop.product.controller.config.CommonProductControllerTestConfiguration;
import com.shop.product.controller.run.ProductControllerTestsRun;
import com.shop.product.dao.CategoryRepository;
import com.shop.product.dto.CategoryDto;
import com.shop.product.model.Category;
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
import java.util.stream.Collectors;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(classes = {ProductControllerTestsRun.class, CommonProductControllerTestConfiguration.class})
public class CategoryApiControllerTests {

    @Value("${test.access.token}")
    private String TEST_ACCESS_TOKEN ;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TokensApiClient tokensApiClient;

    private final static JsonMapper jsonMapper = new JsonMapper();

    @Container
    private final static PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer("postgres:14");

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
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
    public void clearDatabase() {
        categoryRepository.deleteAll();
    }

    @Test
    public void getCategoriesByIdsTest() throws Exception {
        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < 3; i++)
            categories.add(categoryRepository.save(CategoryBuilder.category().id(null).build()));
        List<Long> ids = categories.stream().map(Category::getId).limit(2).collect(Collectors.toList());
        ids.add(1001L);

        String body = mockMvc.perform(MockMvcRequestBuilders.get("/api/products/categories/selected")
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(ids)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<CategoryDto> result = jsonMapper.readValue(body, new TypeReference<>() {});

        Assertions.assertEquals(2, result.size());
        result.forEach(categoryDto ->
            Assertions.assertTrue(categories.stream().anyMatch(c -> c.getId().equals(categoryDto.getId())))
        );
    }

    @Test
    public void getCategoriesByNotExistedIdsTest() throws Exception {
        String body = mockMvc.perform(MockMvcRequestBuilders.get("/api/products/categories/selected")
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(List.of(1L, 2L))))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<CategoryDto> result = jsonMapper.readValue(body, new TypeReference<>() {});

        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void getCategoriesByIdsNullDataTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/categories/selected")
                        .header(HttpHeaders.AUTHORIZATION, TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(null)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }
}
