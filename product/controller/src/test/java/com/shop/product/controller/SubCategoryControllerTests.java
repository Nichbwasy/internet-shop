package com.shop.product.controller;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.shop.product.controller.config.CommonProductControllerTestConfiguration;
import com.shop.product.controller.run.ProductControllerTestsRun;
import com.shop.product.dao.SubCategoryRepository;
import com.shop.product.dto.SubCategoryDto;
import com.shop.product.model.SubCategory;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(classes = {ProductControllerTestsRun.class, CommonProductControllerTestConfiguration.class})
public class SubCategoryControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SubCategoryRepository subCategoryRepository;

    private final static JsonMapper jsonMapper = new JsonMapper();

    @Container
    private final static PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer("postgres:14");

    @AfterEach
    public void resetDatabase() {
        subCategoryRepository.deleteAll();
    }

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    public void getAllSubCategories() throws Exception {
        subCategoryRepository.save(new SubCategory(null, "Sub1"));
        subCategoryRepository.save(new SubCategory(null, "Sub2"));

        mockMvc.perform(MockMvcRequestBuilders.get("/category/sub")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()",
                        Matchers.equalTo(2)));
    }

    @Test
    public void getSubCategoryTest() throws Exception {
        SubCategory subCategory = subCategoryRepository.save(new SubCategory(null, "Sub1"));

        mockMvc.perform(MockMvcRequestBuilders.get("/category/sub/" + subCategory.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(subCategory.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(subCategory.getName()));
    }

    @Test
    public void getNotExistedSubCategoryTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/category/sub/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void addSubCategoryTest() throws Exception {
        SubCategoryDto subCategoryDto = new SubCategoryDto(null, "NewSub");

        mockMvc.perform(MockMvcRequestBuilders.post("/category/sub")
                        .content(jsonMapper.writeValueAsString(subCategoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(subCategoryDto.getName()));
    }

    @Test
    public void addAlreadyExistedSubCategoryTest() throws Exception {
        SubCategory subCategory = subCategoryRepository.save(new SubCategory(1L, "Sub1"));
        SubCategoryDto subCategoryDto = new SubCategoryDto(null, subCategory.getName());

        mockMvc.perform(MockMvcRequestBuilders.post("/category/sub")
                        .content(jsonMapper.writeValueAsString(subCategoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void addNullDataCategoryTest() throws Exception {
        SubCategoryDto subCategoryDto = new SubCategoryDto();

        mockMvc.perform(MockMvcRequestBuilders.post("/category/sub")
                        .content(jsonMapper.writeValueAsString(subCategoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void addNullCategoryTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/category/sub")
                        .content(jsonMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void removeSubCategoryTest() throws Exception {
        SubCategory subCategory = subCategoryRepository.save(new SubCategory(null, "Sub1"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/category/sub/" + subCategory.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(subCategory.getId()));
    }

    @Test
    public void removeNotExistedSubCategoryTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/category/sub/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void updateSubCategoryTest() throws Exception {
        SubCategory subCategory = subCategoryRepository.save(new SubCategory(null, "Sub1"));
        SubCategoryDto subCategoryDto = new SubCategoryDto(subCategory.getId(), "NewSub1");

        mockMvc.perform(MockMvcRequestBuilders.put("/category/sub")
                        .content(jsonMapper.writeValueAsString(subCategoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(subCategoryDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(subCategoryDto.getName()));
    }

    @Test
    public void updateNotExistedSubCategoryTest() throws Exception {
        SubCategoryDto subCategoryDto = new SubCategoryDto(1L, "NewSub1");

        mockMvc.perform(MockMvcRequestBuilders.put("/category/sub")
                        .content(jsonMapper.writeValueAsString(subCategoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void updateNullDataSubCategoryTest() throws Exception {
        SubCategoryDto subCategoryDto = new SubCategoryDto();

        mockMvc.perform(MockMvcRequestBuilders.put("/category/sub")
                        .content(jsonMapper.writeValueAsString(subCategoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void updateNullSubCategoryTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/category/sub")
                        .content(jsonMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }


}

