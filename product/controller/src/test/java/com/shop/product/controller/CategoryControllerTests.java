package com.shop.product.controller;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.shop.product.controller.config.CommonProductControllerTestConfiguration;
import com.shop.product.controller.run.ProductControllerTestsRun;
import com.shop.product.dao.CategoryRepository;
import com.shop.product.dao.SubCategoryRepository;
import com.shop.product.dto.CategoryDto;
import com.shop.product.dto.form.AddOrRemoveForm;
import com.shop.product.model.Category;
import com.shop.product.model.SubCategory;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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

import java.util.List;

@Testcontainers
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(classes = {ProductControllerTestsRun.class, CommonProductControllerTestConfiguration.class})
public class CategoryControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SubCategoryRepository subCategoryRepository;
    @Autowired
    private CategoryRepository categoryRepository;

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

    @AfterEach
    public void resetDatabase() {
        subCategoryRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    public void getAllCategoriesTest() throws Exception {
        categoryRepository.save(new Category(null, "Cat1", null));
        categoryRepository.save(new Category(null, "Cat2", null));

        mockMvc.perform(MockMvcRequestBuilders.get("/category")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()",
                        Matchers.equalTo(2)));
    }

    @Test
    public void getCategoryTest() throws Exception {
        Category category = categoryRepository.save(new Category(null, "Cat1", null));

        mockMvc.perform(MockMvcRequestBuilders.get("/category/" + category.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(category.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(category.getName()));
    }

    @Test
    public void getNotExistedCategoryTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/category/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void addCategoryTest() throws Exception {
        CategoryDto categoryDto = new CategoryDto(null, "NewCat1", null);

        mockMvc.perform(MockMvcRequestBuilders.post("/category")
                        .content(jsonMapper.writeValueAsString(categoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(categoryDto.getName()));
    }

    @Test
    public void addAlreadyExistedCategoryTest() throws Exception {
        categoryRepository.save(new Category(null, "Cat1", null));
        CategoryDto categoryDto = new CategoryDto(null, "Cat1", null);

        mockMvc.perform(MockMvcRequestBuilders.post("/category")
                        .content(jsonMapper.writeValueAsString(categoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void addNullDataCategoryTest() throws Exception {
        CategoryDto categoryDto = new CategoryDto();

        mockMvc.perform(MockMvcRequestBuilders.post("/category")
                        .content(jsonMapper.writeValueAsString(categoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void addNullCategoryTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/category")
                        .content(jsonMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void removeCategoryTest() throws Exception {
        Category category = categoryRepository.save(new Category(null, "Cat1", null));

        mockMvc.perform(MockMvcRequestBuilders.delete("/category/" + category.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(category.getId()));
    }

    @Test
    public void removeNotExistedCategoryTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/category/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void updateCategoryTest() throws Exception {
        Category category = categoryRepository.save(new Category(null, "Cat1", null));
        CategoryDto categoryDto = new CategoryDto(category.getId(), "Cat1", null);

        mockMvc.perform(MockMvcRequestBuilders.put("/category")
                        .content(jsonMapper.writeValueAsString(categoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(categoryDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(categoryDto.getName()));
    }

    @Test
    public void updateNotExistedCategoryTest() throws Exception {
        CategoryDto categoryDto = new CategoryDto(1L, "Cat1", null);

        mockMvc.perform(MockMvcRequestBuilders.put("/category")
                        .content(jsonMapper.writeValueAsString(categoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void updateNullDataCategoryTest() throws Exception {
        CategoryDto categoryDto = new CategoryDto();

        mockMvc.perform(MockMvcRequestBuilders.put("/category")
                        .content(jsonMapper.writeValueAsString(categoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void updateNullCategoryTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/category")
                        .content(jsonMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void addNewSubCategoryTest() throws Exception {
        SubCategory sub1 = subCategoryRepository.save(new SubCategory(null, "Sub1"));
        SubCategory sub2 = subCategoryRepository.save(new SubCategory(null, "Sub2"));
        Category category = categoryRepository.save(new Category(null, "Cat1", null));

        AddOrRemoveForm form = new AddOrRemoveForm(category.getId(), List.of(0L, sub1.getId(), sub2.getId()));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/category/new/sub")
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(category.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(category.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subCategories.length()", Matchers.equalTo(2)))
                .andReturn();

        CategoryDto resultBody = jsonMapper.readValue(result.getResponse().getContentAsString(), CategoryDto.class);
        resultBody.getSubCategories().forEach(sc ->
                Assertions.assertTrue(form.getAddedOrRemovedIds().stream().anyMatch(id -> id.equals(sc.getId())))
        );
    }

    @Test
    public void addNotExistedSubCategoryTest() throws Exception {
        AddOrRemoveForm form = new AddOrRemoveForm(1L, List.of(1L, 2L));

        mockMvc.perform(MockMvcRequestBuilders.post("/category/new/sub")
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void addNullDataSubCategoryTest() throws Exception {
        AddOrRemoveForm form = new AddOrRemoveForm();

        mockMvc.perform(MockMvcRequestBuilders.post("/category/new/sub")
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void addNullSubCategoryTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/category/new/sub")
                        .content(jsonMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void removeSubCategoriesTest() throws Exception {
        SubCategory sub1 = subCategoryRepository.save(new SubCategory(null, "Sub1"));
        SubCategory sub2 = subCategoryRepository.save(new SubCategory(null, "Sub2"));
        Category category = categoryRepository.save(new Category(null, "Cat1", List.of(sub1, sub2)));

        AddOrRemoveForm form = new AddOrRemoveForm(category.getId(), List.of(0L, sub1.getId(), sub2.getId()));

        mockMvc.perform(MockMvcRequestBuilders.delete("/category/removing/sub")
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(category.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(category.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subCategories.length()", Matchers.equalTo(0)));
    }

    @Test
    public void removeNotExistedSubCategoriesTest() throws Exception {
        AddOrRemoveForm form = new AddOrRemoveForm(1L, List.of(1L, 2L));

        mockMvc.perform(MockMvcRequestBuilders.delete("/category/removing/sub")
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void removeNullDataSubCategoriesTest() throws Exception {
        AddOrRemoveForm form = new AddOrRemoveForm();

        mockMvc.perform(MockMvcRequestBuilders.delete("/category/removing/sub")
                        .content(jsonMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void removeNullSubCategoriesTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/category/removing/sub")
                        .content(jsonMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

}
