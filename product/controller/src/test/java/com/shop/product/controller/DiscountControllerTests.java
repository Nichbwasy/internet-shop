package com.shop.product.controller;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shop.authorization.client.TokensApiClient;
import com.shop.authorization.common.constant.UsersRoles;
import com.shop.authorization.common.constant.jwt.TokenStatus;
import com.shop.authorization.dto.token.JwtAuthenticationTokenDataDto;
import com.shop.common.utils.all.generator.StringGenerator;
import com.shop.product.controller.config.CommonProductControllerTestConfiguration;
import com.shop.product.controller.run.ProductControllerTestsRun;
import com.shop.product.dao.DiscountRepository;
import com.shop.product.dto.DiscountDto;
import com.shop.product.model.Discount;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(classes = {ProductControllerTestsRun.class, CommonProductControllerTestConfiguration.class})
public class DiscountControllerTests {

    @Value("${test.access.token}")
    private String TEST_ACCESS_TOKEN ;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DiscountController discountController;
    @Autowired
    private DiscountRepository discountRepository;
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
    public void resetDatabase() {
        discountRepository.deleteAll();
    }

    @Test
    public void getAllDiscountsTest() throws Exception{
        discountRepository.save(generateRandomDiscount());
        discountRepository.save(generateRandomDiscount());

        mockMvc.perform(MockMvcRequestBuilders.get("/discount")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()",
                        Matchers.equalTo(2)));
    }

    @Test
    public void getDiscountTest() throws Exception {
        Discount discount = discountRepository.save(generateRandomDiscount());

        mockMvc.perform(MockMvcRequestBuilders.get("/discount/" + discount.getId())
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(discount.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(discount.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(discount.getDescription()));
    }

    @Test
    public void getNotExistedDiscountTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/discount/1")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void addDiscountTest() throws Exception {
        DiscountDto discountDto = generateRandomDiscountDto();

        mockMvc.perform(MockMvcRequestBuilders.post("/discount")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(discountDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(discountDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(discountDto.getDescription()));
    }

    @Test
    public void addAlreadyExistedDiscountTest() throws Exception {
        Discount discount = discountRepository.save(generateRandomDiscount());
        DiscountDto discountDto = generateRandomDiscountDto();
        discountDto.setId(discount.getId());
        discountDto.setName(discount.getName());

        mockMvc.perform(MockMvcRequestBuilders.post("/discount")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(discountDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(discountDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(discountDto.getDescription()));
    }

    @Test
    public void addNullDataDiscountTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/discount")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(new DiscountDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void addNullDiscountTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/discount")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void removeDiscountTest() throws Exception {
        Discount discount = discountRepository.save(generateRandomDiscount());

        mockMvc.perform(MockMvcRequestBuilders.delete("/discount/" + discount.getId())
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(discount.getId()));
    }

    @Test
    public void removeNotExistedDiscountTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/discount/1")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void updateDiscountTest() throws Exception {
        Discount discount = discountRepository.save(generateRandomDiscount());
        DiscountDto discountDto = generateRandomDiscountDto();
        discountDto.setId(discount.getId());

        mockMvc.perform(MockMvcRequestBuilders.put("/discount")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(discountDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(discountDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(discountDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(discountDto.getDescription()));
    }

    @Test
    public void updateNotExistedDiscountTest() throws Exception {
        DiscountDto discountDto = generateRandomDiscountDto();
        discountDto.setId(1L);

        mockMvc.perform(MockMvcRequestBuilders.put("/discount")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(discountDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void updateNullDataDiscountTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/discount")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(new DiscountDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void updateNullDiscountTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/discount")
                        .header("Authorization", TEST_ACCESS_TOKEN)
                        .content(jsonMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @NotNull
    private static Discount generateRandomDiscount() {
        Discount discount = new Discount();
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();
        discount.setName(StringGenerator.generate(10));
        discount.setDescription(StringGenerator.generate(30));
        discount.setDiscountValue(random.nextFloat());
        discount.setCreatedTime(now);
        discount.setActivationTime(now.plusSeconds(random.nextInt(1, 10)));
        discount.setEndingTime(now.plusSeconds(random.nextInt(11, 20)));
        return discount;
    }

    @NotNull
    private static DiscountDto generateRandomDiscountDto() {
        DiscountDto discountDto = new DiscountDto();
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();
        discountDto.setName(StringGenerator.generate(10));
        discountDto.setDescription(StringGenerator.generate(30));
        discountDto.setDiscountValue(random.nextFloat());
        discountDto.setCreatedTime(now);
        discountDto.setActivationTime(now.plusSeconds(random.nextInt(1, 10)));
        discountDto.setEndingTime(now.plusSeconds(random.nextInt(11, 20)));
        return discountDto;
    }

}


