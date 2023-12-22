package com.shop.media.controller.test;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.shop.authorization.client.TokensApiClient;
import com.shop.authorization.common.constant.UsersRoles;
import com.shop.authorization.common.constant.jwt.TokenStatus;
import com.shop.authorization.common.data.builder.JwtAuthenticationTokenDataDtoBuilder;
import com.shop.authorization.dto.token.JwtAuthenticationTokenDataDto;
import com.shop.common.utils.all.generator.StringGenerator;
import com.shop.media.common.data.builder.GetFileFormBuilder;
import com.shop.media.common.data.builder.RemoveFileFormBuilder;
import com.shop.media.common.data.builder.UploadFileFormBuilder;
import com.shop.media.controller.RunMediaTestsControllerApplication;
import com.shop.media.controller.config.CommonMediaMicroserviceTestConfiguration;
import com.shop.media.dto.form.GetFileForm;
import com.shop.media.dto.form.RemoveFileForm;
import com.shop.media.dto.form.UploadFileForm;
import io.minio.*;
import io.minio.messages.Item;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.util.List;
import java.util.Objects;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(classes = {RunMediaTestsControllerApplication.class, CommonMediaMicroserviceTestConfiguration.class})
public class MinIoApiControllerTests {

    @Value("${test.access.token}")
    private String testAccessToken;
    @Value("${minio.bucket.name}")
    private String bucketName;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TokensApiClient tokensApiClient;
    @Autowired
    private MinioClient minioClient;

    private static String TEST_FILE_PATH;
    private final static JsonMapper jsonMapper = new JsonMapper();
    private final static MinIOContainer minIoContainer = new MinIOContainer("minio/minio");

    @DynamicPropertySource
    public static void registerProperty(DynamicPropertyRegistry propertyRegistry) {
        minIoContainer.start();

        propertyRegistry.add("minio.url", minIoContainer::getS3URL);
        propertyRegistry.add("minio.credentials.login", minIoContainer::getUserName);
        propertyRegistry.add("minio.credentials.password", minIoContainer::getPassword);
    }

    @BeforeAll
    public static void init() {
        TEST_FILE_PATH = new File(Objects.requireNonNull(MinIoApiControllerTests.class
                        .getClassLoader()
                        .getResource("test_file.txt"))
                .getFile()).getAbsolutePath();
    }

    @BeforeEach
    public void createBucketIfNotExists() throws Exception {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
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
    public void clearMinIo() {
        Iterable<Result<Item>> objects = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
        objects.forEach(obj -> {
            try {
                String objectName = obj.get().objectName();
                minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
            } catch (Exception e) {
                throw new RuntimeException("Unable get the object! %s".formatted(e.getMessage()));
            }
        });
    }

    @Test
    public void getFileTest() throws Exception {
        String objectName = StringGenerator.generate(4) + ".txt";
        minioClient.uploadObject(UploadObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .filename(TEST_FILE_PATH)
                .build());
        GetFileForm form = GetFileFormBuilder.getFileForm().fileName(objectName).build();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/media/minio/" + bucketName)
                    .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(form)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty());
    }

    @Test
    public void getNotExistedFileTest() throws Exception {
        String objectName = StringGenerator.generate(4) + ".txt";
        GetFileForm form = GetFileFormBuilder.getFileForm().fileName(objectName).build();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/media/minio/" + bucketName)
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(form)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getFileNullDataTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/media/minio/" + bucketName)
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(null)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void uploadFileTest() throws Exception {
        UploadFileForm form = UploadFileFormBuilder.uploadFileForm()
                .fileName(StringGenerator.generate(4))
                .build();
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "test_file.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "TEST TEXT".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,"/api/media/minio/" + bucketName)
                        .file(mockMultipartFile)
                        .flashAttr("uploadFileForm", form)
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(form.getFileName() + ".txt"));
    }

    @Test
    public void uploadExistedFileTest() throws Exception {
        ObjectWriteResponse response = minioClient.uploadObject(UploadObjectArgs.builder()
                .object("existedFile.txt")
                .bucket(bucketName)
                .filename(TEST_FILE_PATH)
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .build());
        UploadFileForm form = UploadFileFormBuilder.uploadFileForm()
                .fileName("existedFile")
                .build();
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                response.object(),
                MediaType.TEXT_PLAIN_VALUE,
                "TEST TEXT".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,"/api/media/minio/" + bucketName)
                        .file(mockMultipartFile)
                        .flashAttr("uploadFileForm", form)
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(form.getFileName() + ".txt"));
    }

    @Test
    public void removeFileTest() throws Exception {
        ObjectWriteResponse response = minioClient.uploadObject(UploadObjectArgs.builder()
                .object("existedFile.txt")
                .bucket(bucketName)
                .filename(TEST_FILE_PATH)
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .build());
        RemoveFileForm form = RemoveFileFormBuilder.removeFileForm().fileName(response.object()).build();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/media/minio/" + bucketName)
                    .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(form)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(response.object()));
    }

    @Test
    public void removeNotExistedFileTest() throws Exception {
        String fileName = "someNotExistedFile.abc";
        RemoveFileForm form = RemoveFileFormBuilder.removeFileForm().fileName(fileName).build();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/media/minio/" + bucketName)
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(form)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(fileName));
    }

    @Test
    public void removeFileNullDataTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/media/minio/" + bucketName)
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(null)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

}
