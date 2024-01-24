package com.shop.media.controller.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.shop.authorization.client.TokensApiClient;
import com.shop.authorization.common.constant.UsersRoles;
import com.shop.authorization.common.constant.jwt.TokenStatus;
import com.shop.authorization.common.data.builder.JwtAuthenticationTokenDataDtoBuilder;
import com.shop.authorization.dto.token.JwtAuthenticationTokenDataDto;
import com.shop.media.common.data.builder.FileExtensionBuilder;
import com.shop.media.common.data.builder.FileExtensionDtoBuilder;
import com.shop.media.controller.RunMediaTestsControllerApplication;
import com.shop.media.controller.config.CommonMediaMicroserviceTestConfiguration;
import com.shop.media.dao.FileExtensionRepository;
import com.shop.media.dto.FileExtensionDto;
import com.shop.media.model.FileExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(classes = {RunMediaTestsControllerApplication.class, CommonMediaMicroserviceTestConfiguration.class})
public class FileExtensionControllerTests {

    @Value("${test.access.token}")
    private String testAccessToken;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TokensApiClient tokensApiClient;
    @Autowired
    private FileExtensionRepository fileExtensionRepository;

    private final static JsonMapper jsonMapper = new JsonMapper();

    @Container
    private final static MinIOContainer minIoContainer = new MinIOContainer("minio/minio");
    @Container
    private final static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14");

    @DynamicPropertySource
    public static void registerProperty(DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        propertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        propertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);

        propertyRegistry.add("minio.url", minIoContainer::getS3URL);
        propertyRegistry.add("minio.credentials.login", minIoContainer::getUserName);
        propertyRegistry.add("minio.credentials.password", minIoContainer::getPassword);
    }

    @BeforeEach
    public void mockTokensClient() {
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
        fileExtensionRepository.deleteAll();
    }

    @Test
    public void getAllFilesExtensionsTest() throws Exception {
        List<FileExtension> fileExtensions = List.of(
                fileExtensionRepository.save(FileExtensionBuilder.fileExtension().id(null).build()),
                fileExtensionRepository.save(FileExtensionBuilder.fileExtension().id(null).build())
        );

        String body = mockMvc.perform(MockMvcRequestBuilders.get("/files/extensions")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<FileExtensionDto> result = jsonMapper.readValue(body, new TypeReference<>() {});
        Assertions.assertEquals(2, result.size());
        result.forEach(resultEl ->
                Assertions.assertTrue(fileExtensions.stream().anyMatch(fe -> compareFileExtensions(fe, resultEl)))
        );
    }

    @Test
    public void getFileExtensionTest() throws Exception {
        FileExtension fileExtension = fileExtensionRepository.save(FileExtensionBuilder.fileExtension().id(null).build());

        String body = mockMvc.perform(MockMvcRequestBuilders.get("/files/extensions/" + fileExtension.getId())
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        FileExtensionDto result = jsonMapper.readValue(body, FileExtensionDto.class);

        Assertions.assertTrue(compareFileExtensions(fileExtension, result));
    }

    @Test
    public void getNotExistedFileExtensionTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/files/extensions/1001")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void createFileExtensionTest() throws Exception {
        FileExtensionDto fileExtensionDto = FileExtensionDtoBuilder.fileExtensionDto().id(null).build();

        String body = mockMvc.perform(MockMvcRequestBuilders.post("/files/extensions")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(fileExtensionDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        FileExtensionDto result = jsonMapper.readValue(body, FileExtensionDto.class);

        Assertions.assertEquals(fileExtensionDto.getName(), result.getName());
        Assertions.assertEquals(fileExtensionDto.getMediaTypeName(), result.getMediaTypeName());
    }

    @Test
    public void createAlreadyExistedFileExtensionTest() throws Exception {
        FileExtension fileExtension = fileExtensionRepository.save(FileExtensionBuilder.fileExtension().id(null).build());
        FileExtensionDto fileExtensionDto = FileExtensionDtoBuilder.fileExtensionDto()
                .name(fileExtension.getName())
                .mediaTypeName(fileExtension.getMediaTypeName())
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/files/extensions")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(fileExtensionDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }


    @Test
    public void updateFileExtensionTest() throws Exception {
        FileExtension fileExtension = fileExtensionRepository.save(FileExtensionBuilder.fileExtension().id(null).build());
        FileExtensionDto fileExtensionDto = FileExtensionDtoBuilder.fileExtensionDto().build();

        String body = mockMvc.perform(MockMvcRequestBuilders.put("/files/extensions/" + fileExtension.getId())
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(fileExtensionDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        FileExtensionDto result = jsonMapper.readValue(body, FileExtensionDto.class);

        Assertions.assertEquals(fileExtension.getId(), result.getId());
        Assertions.assertEquals(fileExtensionDto.getName(), result.getName());
        Assertions.assertEquals(fileExtensionDto.getMediaTypeName(), result.getMediaTypeName());
    }

    @Test
    public void updateNotExistedFileExtensionTest() throws Exception {
        FileExtensionDto fileExtensionDto = FileExtensionDtoBuilder.fileExtensionDto().build();

        mockMvc.perform(MockMvcRequestBuilders.put("/files/extensions/1001")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(fileExtensionDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void removeFileExtensionTest() throws Exception {
        Long removeId = fileExtensionRepository.save(FileExtensionBuilder.fileExtension().id(null).build()).getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/files/extensions/" + removeId)
                .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(removeId));
    }

    @Test
    public void removeNotExistedFileExtensionTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/files/extensions/1001")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(1001));
    }

    private Boolean compareFileExtensions(FileExtension model, FileExtensionDto dto) {
        return model.getId().equals(dto.getId()) &&
                model.getName().equals(dto.getName()) &&
                model.getMediaTypeName().equals(dto.getMediaTypeName());
    }

}
