package com.shop.media.controller.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shop.authorization.client.TokensApiClient;
import com.shop.authorization.common.constant.UsersRoles;
import com.shop.authorization.common.constant.jwt.TokenStatus;
import com.shop.authorization.common.data.builder.JwtAuthenticationTokenDataDtoBuilder;
import com.shop.authorization.dto.token.JwtAuthenticationTokenDataDto;
import com.shop.common.utils.all.file.FilesUtils;
import com.shop.common.utils.all.generator.StringGenerator;
import com.shop.media.common.data.builder.AddMediaToProductFormBuilder;
import com.shop.media.common.data.builder.FileExtensionBuilder;
import com.shop.media.common.data.builder.MediaElementBuilder;
import com.shop.media.common.data.builder.ProductMediaBuilder;
import com.shop.media.controller.ProductMediaApiController;
import com.shop.media.controller.RunMediaTestsControllerApplication;
import com.shop.media.controller.config.CommonMediaMicroserviceTestConfiguration;
import com.shop.media.dao.FileExtensionRepository;
import com.shop.media.dao.MediaElementRepository;
import com.shop.media.dao.ProductMediaRepository;
import com.shop.media.dto.MediaElementDto;
import com.shop.media.dto.ProductMediaDto;
import com.shop.media.dto.form.AddMediaToProductForm;
import com.shop.media.dto.metadata.DockMetadataDto;
import com.shop.media.dto.metadata.ImgMetadataDto;
import com.shop.media.model.FileExtension;
import com.shop.media.model.MediaElement;
import com.shop.media.model.ProductMedia;
import io.minio.*;
import io.minio.messages.Item;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(classes = {RunMediaTestsControllerApplication.class, CommonMediaMicroserviceTestConfiguration.class})
public class ProductMediaApiControllerTests {

    @Value("${test.access.token}")
    private String testAccessToken;
    @Value("${minio.bucket.name}")
    private String bucketName;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductMediaApiController productMediaApiController;
    @Autowired
    private TokensApiClient tokensApiClient;
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private ProductMediaRepository productMediaRepository;
    @Autowired
    private MediaElementRepository mediaElementRepository;
    @Autowired
    private FileExtensionRepository fileExtensionRepository;

    private static String TEST_IMG_FILE_PATH;
    private static String TEST_DOC_FILE_PATH;
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

    @BeforeAll
    public static void init() {
        jsonMapper.registerModule(new JavaTimeModule());
        TEST_IMG_FILE_PATH = new File(Objects.requireNonNull(MinIoApiControllerTests.class
                        .getClassLoader()
                        .getResource("test_img.png"))
                .getFile()).getAbsolutePath();
        TEST_DOC_FILE_PATH = new File(Objects.requireNonNull(MinIoApiControllerTests.class
                        .getClassLoader()
                        .getResource("test_doc.docx"))
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
    public void clearDatabase() {
        mediaElementRepository.deleteAll();
        productMediaRepository.deleteAll();
        fileExtensionRepository.deleteAll();
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
    public void loadImagesForProductTest() throws Exception {
        Long id = saveProductMediaWithRandomDataInRepository(List.of("FILE1", "FILE2"), "png", "image/png", TEST_IMG_FILE_PATH);

        String body = mockMvc.perform(MockMvcRequestBuilders.get("/api/media/products/" + id + "/imgs")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<byte[]> result = jsonMapper.readValue(body, new TypeReference<>() {});

        Assertions.assertEquals(2, result.size());
    }

    @Test
    public void loadImagesForNotExistedProductTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/media/products/1001/imgs")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void loadImagesForProductWithoutImgsTest() throws Exception {
        ProductMedia productMedia = productMediaRepository.save(ProductMediaBuilder.productMedia().id(null).build());

        String body = mockMvc.perform(MockMvcRequestBuilders.get("/api/media/products/" + productMedia.getId() + "/imgs")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<byte[]> result = jsonMapper.readValue(body, new TypeReference<>() {});

        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void saveImageToExistedProductTest() throws Exception {
        File file = new File(TEST_IMG_FILE_PATH);
        FileInputStream fileData = new FileInputStream(file);
        MockMultipartFile mFile = new MockMultipartFile("file", file.getName(), MediaType.APPLICATION_JSON_VALUE, fileData);
        saveFileExtension("png", "image/png");
        ProductMedia productMedia = productMediaRepository.save(ProductMediaBuilder.productMedia().id(null).build());

        String body = mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, "/api/media/products/" + productMedia.getId() + "/imgs")
                        .file(mFile)
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        ProductMediaDto result = jsonMapper.readValue(body, ProductMediaDto.class);
        MediaElementDto savedElement = result.getMediaElements().get(0);

        Assertions.assertEquals(1, result.getMediaElements().size());
        Assertions.assertEquals("/" + productMedia.getId() + "/imgs", savedElement.getPath());
        Assertions.assertEquals(bucketName, savedElement.getBucketName());
        Assertions.assertEquals(".png", FilesUtils.extractFileExtension(savedElement.getFileName()));
        Assertions.assertEquals("png", savedElement.getFileExtension().getName());
        Assertions.assertEquals("image/png", savedElement.getFileExtension().getMediaTypeName());
    }

    @Test
    public void saveImageToNotExistedProductTest() throws Exception {
        MockMultipartFile mFile = new MockMultipartFile("file", "name.png", "image/png", new byte[0]);

        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,"/api/media/products/1001/imgs")
                        .file(mFile)
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void saveImageToProductNullFileTest() throws Exception {
        AddMediaToProductForm form = AddMediaToProductFormBuilder.createProductMediaForm().build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/media/products/" + form.getProductMediaId() + "/imgs")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .flashAttr("createProductMediaForm", form))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void removeProductImageTest() throws Exception {
        FileExtension fileExtension = saveFileExtension("png", "image/png");
        ProductMedia productMedia = productMediaRepository.save(ProductMediaBuilder.productMedia().id(null).build());
        ObjectWriteResponse response = saveFileInMinIo(productMedia.getId(), "imgs", ".png", "image/png");
        MediaElement mediaElement = saveMediaElement(productMedia, response, fileExtension, "imgs");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/media/products/" + productMedia.getId() +
                                                                 "/imgs/" + mediaElement.getId())
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(mediaElement.getId()));
    }

    @Test
    public void removeProductImageNotBelongOrNorExistsTest() throws Exception {
        ProductMedia productMedia = productMediaRepository.save(ProductMediaBuilder.productMedia().id(null).build());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/media/products/" + productMedia.getId() +
                                "/imgs/1001 ")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getProductImagesMetadataTest() throws Exception {
        List<String> fileNames = List.of("FILE1", "FILE2");
        Long id = saveProductMediaWithRandomDataInRepository(fileNames, "png", "image/png", TEST_IMG_FILE_PATH);

        String body = mockMvc.perform(MockMvcRequestBuilders.get("/api/media/products/" + id + "/imgs/data")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        List<ImgMetadataDto> result = jsonMapper.readValue(body, new TypeReference<>() {});

        LocalDateTime now = LocalDateTime.now();
        Assertions.assertEquals(2, result.size());
        result.forEach(r -> {
                Assertions.assertTrue(fileNames.stream().anyMatch(n -> (n + ".png").equals(r.getFileName())));
                Assertions.assertEquals("image/png", r.getFileExtension());
                Assertions.assertTrue(r.getCreationTime().isBefore(now));
                Assertions.assertTrue(r.getLastTimeUpdate().isBefore(now));
        });
    }

    @Test
    public void loadProductDockTest() throws Exception {
        FileExtension fileExtension = saveFileExtension("docx", "document/docx");
        ProductMedia productMedia = productMediaRepository.save(ProductMediaBuilder.productMedia().id(null).build());
        ObjectWriteResponse response = saveFileInMinIo(productMedia.getId(), "docs", ".docx", "document/docx");
        MediaElement mediaElement = saveMediaElement(productMedia, response, fileExtension, "docs");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/media/products/" + productMedia.getId() + "/docks/" + mediaElement.getId())
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty());
    }

    @Test
    public void loadProductDockNotExistsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/media/products/1001/docks/1001")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getProductDocksMetadataTest() throws Exception {
        List<String> filesNames = List.of("FILE1", "FILE2");
        Long id = saveProductMediaWithRandomDataInRepository(
                filesNames, "docx", "document/docx", TEST_DOC_FILE_PATH);

        String body = mockMvc.perform(MockMvcRequestBuilders.get("/api/media/products/" + id + "/docks/data")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        List<DockMetadataDto> result = jsonMapper.readValue(body, new TypeReference<>() {});

        Assertions.assertEquals(filesNames.size(), result.size());
        result.forEach(m -> {
            Assertions.assertTrue(filesNames.stream().anyMatch(n -> m.getFileName().equals(n + ".docx")));
            Assertions.assertEquals("document/docx", m.getFileExtension());
            Assertions.assertTrue(m.getCreationTime().isBefore(LocalDateTime.now()));
            Assertions.assertTrue(m.getLastTimeUpdate().isBefore(LocalDateTime.now()));
        });
    }

    @Test
    public void getProductDocksMetadataNotExistsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/media/products/1001/docks/data")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getProductDockMetadataTest() throws Exception {
        FileExtension fileExtension = saveFileExtension("docx", "document/docx");
        ProductMedia productMedia = productMediaRepository.save(ProductMediaBuilder.productMedia().id(null).build());
        ObjectWriteResponse response = saveFileInMinIo(productMedia.getId(), "docs", ".docx", "document/docx");
        MediaElement mediaElement = saveMediaElement(productMedia, response, fileExtension, "docs");

        String body = mockMvc.perform(MockMvcRequestBuilders.get("/api/media/products/" + productMedia.getId() + "/docks/data/" + mediaElement.getId())
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        DockMetadataDto result = jsonMapper.readValue(body, DockMetadataDto.class);

        Assertions.assertTrue(result.getFileName().contains(".docx"));
        Assertions.assertEquals("document/docx", result.getFileExtension());
        Assertions.assertTrue(result.getCreationTime().isBefore(LocalDateTime.now()));
        Assertions.assertTrue(result.getLastTimeUpdate().isBefore(LocalDateTime.now()));
    }

    @Test
    public void getProductDockMetadataNoElementExistsTest() throws Exception {
        ProductMedia productMedia = productMediaRepository.save(ProductMediaBuilder.productMedia().id(null).build());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/media/products/" + productMedia.getId() + "/docks/data/1001")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getProductDockMetadataNotExistsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/media/products/1001/docks/data/1001")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void addDockToProductTest() throws Exception {
        File file = new File(TEST_DOC_FILE_PATH);
        FileInputStream fileData = new FileInputStream(file);
        MockMultipartFile mFile = new MockMultipartFile("file", file.getName(), MediaType.APPLICATION_JSON_VALUE, fileData);
        saveFileExtension("docx", "document/docx");
        Long id = productMediaRepository.save(ProductMediaBuilder.productMedia().id(null).build()).getId();


        String body = mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, "/api/media/products/" + id + "/docks")
                        .file(mFile)
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        ProductMediaDto result = jsonMapper.readValue(body, ProductMediaDto.class);
        MediaElementDto savedElement = result.getMediaElements().get(0);

        Assertions.assertEquals(1, result.getMediaElements().size());
        Assertions.assertEquals("/" + id + "/docks", savedElement.getPath());
        Assertions.assertEquals(bucketName, savedElement.getBucketName());
        Assertions.assertEquals(".docx", FilesUtils.extractFileExtension(savedElement.getFileName()));
        Assertions.assertEquals("docx", savedElement.getFileExtension().getName());
        Assertions.assertEquals("document/docx", savedElement.getFileExtension().getMediaTypeName());
    }

    @Test
    public void addDockToNotExistedProductTest() throws Exception {
        File file = new File(TEST_DOC_FILE_PATH);
        FileInputStream fileData = new FileInputStream(file);
        MockMultipartFile mFile = new MockMultipartFile("file", file.getName(), MediaType.APPLICATION_JSON_VALUE, fileData);

        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, "/api/media/products/1001/docks")
                        .file(mFile)
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void removeDockFormProductTest() throws Exception {
        FileExtension fileExtension = saveFileExtension("docx", "document/docx");
        ProductMedia productMedia = productMediaRepository.save(ProductMediaBuilder.productMedia().id(null).build());
        ObjectWriteResponse response = saveFileInMinIo(productMedia.getId(), "docs", ".docx", "document/docx");
        MediaElement mediaElement = saveMediaElement(productMedia, response, fileExtension, "docs");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/media/products/" + productMedia.getId() + "/docks/" + mediaElement.getId())
                .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(mediaElement.getId()));
    }

    @Test
    public void removeDockFormProductElementNotExistsTest() throws Exception {
        ProductMedia productMedia = productMediaRepository.save(ProductMediaBuilder.productMedia().id(null).build());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/media/products/" + productMedia.getId() + "/docks/1001")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void removeDockFormProductNotExistsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/media/products/1001/docks/1001")
                        .header(HttpHeaders.AUTHORIZATION, testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @NotNull
    private FileExtension saveFileExtension(String name, String mediaTypeName) {
        return fileExtensionRepository.save(FileExtensionBuilder.fileExtension()
                .name(name)
                .mediaTypeName(mediaTypeName)
                .build()
        );
    }

    private ObjectWriteResponse saveFileInMinIo(Long productMediaId, String folder, String extension, String contentType) throws Exception {
        return minioClient.uploadObject(UploadObjectArgs.builder()
                .bucket(bucketName)
                .object(productMediaId + "/" + folder + "/" + StringGenerator.generate(8) + extension)
                .filename(TEST_DOC_FILE_PATH)
                .contentType(contentType)
                .build()
        );
    }

    @NotNull
    private MediaElement saveMediaElement(ProductMedia productMedia, ObjectWriteResponse response, FileExtension fileExtension, String folder) {
        return mediaElementRepository.save(MediaElementBuilder.mediaElement()
                .id(null)
                .productMedia(productMedia)
                .path("/" + productMedia.getProductId() + "/" + folder)
                .bucketName(response.bucket())
                .path(FilesUtils.cropFileName(response.object()))
                .fileName(FilesUtils.cropFilePath(response.object()))
                .fileExtension(fileExtension)
                .fileSize(1L)
                .creationTime(LocalDateTime.now())
                .lastTimeUpdate(LocalDateTime.now())
                .build()
        );
    }

    @NotNull
    private Long saveProductMediaWithRandomDataInRepository(List<String> fileNames,
                                                            String extensionName,
                                                            String mediaTypeName,
                                                            String fileAbsolutePath) throws Exception {
        ProductMedia productMedia = productMediaRepository.save(ProductMediaBuilder.productMedia().id(null).build());
        FileExtension fileExtension = saveFileExtension(extensionName, mediaTypeName);
        for (String fileName: fileNames) {
            ObjectWriteResponse response = saveFileInMinIoForProduct(productMedia, fileName, extensionName, mediaTypeName, fileAbsolutePath);
            MediaElement mediaElement = saveMediaElementForFileInMinIo(response, fileExtension);
            mediaElement.setProductMedia(productMedia);
            productMedia.getMediaElements().add(mediaElementRepository.save(mediaElement));
        }
        return productMediaRepository.save(productMedia).getId();
    }

    private MediaElement saveMediaElementForFileInMinIo(ObjectWriteResponse response, FileExtension fileExtension) {
        return MediaElementBuilder.mediaElement()
                .id(null)
                .path(FilesUtils.cropFileName(response.object()))
                .fileName(FilesUtils.cropFilePath(response.object()))
                .bucketName(bucketName)
                .fileExtension(fileExtension)
                .build();
    }

    private ObjectWriteResponse saveFileInMinIoForProduct(ProductMedia productMedia,
                                                          String fileName,
                                                          String extension,
                                                          String contentType,
                                                          String fileAbsolutePath) throws Exception {
        return minioClient.uploadObject(UploadObjectArgs.builder()
                .bucket(bucketName)
                .object(productMedia.getId() + "/imgs/" + fileName + "." + extension)
                .filename(fileAbsolutePath)
                .contentType(contentType)
                .build());
    }


}
