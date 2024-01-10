package com.shop.media.service;

import com.shop.media.common.data.builder.AddMediaToProductFormBuilder;
import com.shop.media.common.data.builder.FileExtensionBuilder;
import com.shop.media.common.data.builder.MediaElementBuilder;
import com.shop.media.common.data.builder.ProductMediaBuilder;
import com.shop.media.dao.FileExtensionRepository;
import com.shop.media.dao.MediaElementRepository;
import com.shop.media.dao.ProductMediaRepository;
import com.shop.media.dao.exception.MinIoFileGetException;
import com.shop.media.dao.exception.MinIoFileRemoveException;
import com.shop.media.dto.ProductMediaDto;
import com.shop.media.dto.form.AddMediaToProductForm;
import com.shop.media.dto.form.GetFileForm;
import com.shop.media.dto.form.RemoveFileForm;
import com.shop.media.dto.form.UploadFileForm;
import com.shop.media.dto.metadata.ImgMetadataDto;
import com.shop.media.model.FileExtension;
import com.shop.media.model.MediaElement;
import com.shop.media.model.ProductMedia;
import com.shop.media.service.config.ProductMediaApiServiceTestConfiguration;
import com.shop.media.service.exeption.NotSupportedFileExtensionException;
import com.shop.media.service.exeption.product.ImageNotBelongToProductException;
import com.shop.media.service.exeption.product.ProductMediaNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ProductMediaApiServiceTestConfiguration.class})
public class ProductMediaApiServiceTests {

    @Autowired
    private ProductMediaApiService productMediaApiService;
    @Autowired
    private ProductMediaRepository productMediaRepository;
    @Autowired
    private FileExtensionRepository fileExtensionRepository;
    @Autowired
    private MediaElementRepository mediaElementRepository;
    @Autowired
    private MinIoService minIoService;

    @Test
    public void loadImagesForProductTest() {
        ProductMedia productMedia = ProductMediaBuilder.productMedia()
                .mediaElements(List.of(
                        MediaElementBuilder.mediaElement().build(),
                        MediaElementBuilder.mediaElement().build()
                ))
                .build();

        Mockito.when(productMediaRepository.findById(productMedia.getId())).thenReturn(Optional.of(productMedia));
        Mockito.when(minIoService.getFile(Mockito.any(GetFileForm.class))).thenReturn(Mockito.mock(InputStream.class));

        List<byte[]> result = productMediaApiService.loadImagesForProduct(productMedia.getId());
        Assertions.assertEquals(2, result.size());
    }

    @Test
    public void loadImagesForProductNoMediaTest() {
        ProductMedia productMedia = ProductMediaBuilder.productMedia().build();

        Mockito.when(productMediaRepository.findById(productMedia.getId())).thenReturn(Optional.of(productMedia));
        Mockito.when(minIoService.getFile(Mockito.any(GetFileForm.class))).thenReturn(Mockito.mock(InputStream.class));

        List<byte[]> result = productMediaApiService.loadImagesForProduct(productMedia.getId());
        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void loadImagesForProductMediaMinIoExceptionTest() {
        ProductMedia productMedia = ProductMediaBuilder.productMedia()
                .mediaElements(List.of(
                        MediaElementBuilder.mediaElement().build(),
                        MediaElementBuilder.mediaElement().build()
                ))
                .build();

        Mockito.when(productMediaRepository.findById(productMedia.getId())).thenReturn(Optional.of(productMedia));
        Mockito.when(minIoService.getFile(Mockito.any(GetFileForm.class))).thenThrow(MinIoFileGetException.class);

        Assertions.assertThrows(MinIoFileGetException.class,
                () -> productMediaApiService.loadImagesForProduct(productMedia.getId()));
    }

    @Test
    public void loadImagesForProductMediaRepositoryExceptionTest() {
        ProductMedia productMedia = ProductMediaBuilder.productMedia()
                .mediaElements(List.of(
                        MediaElementBuilder.mediaElement().build(),
                        MediaElementBuilder.mediaElement().build()
                ))
                .build();

        Mockito.when(productMediaRepository.findById(productMedia.getId())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(RuntimeException.class,
                () -> productMediaApiService.loadImagesForProduct(productMedia.getId()));
    }

    @Test
    public void loadImagesForProductMediaNullTest() {
        Mockito.when(productMediaRepository.findById(Mockito.nullable(Long.class)))
                .thenThrow(IllegalArgumentException.class);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> productMediaApiService.loadImagesForProduct(null));
    }

    @Test
    public void saveImageToExistedTest() throws Exception {
        FileExtension fileExtension = FileExtensionBuilder.fileExtension()
                .name("txt")
                .build();
        MultipartFile file = Mockito.mock(MultipartFile.class);
        AddMediaToProductForm form = AddMediaToProductFormBuilder.createProductMediaForm()
                .multipartFile(file)
                .build();
        ProductMedia productMedia = ProductMediaBuilder.productMedia().productId(form.getProductMediaId()).build();

        Mockito.when(file.getOriginalFilename()).thenReturn("some_file.txt");
        Mockito.when(file.getInputStream()).thenReturn(Mockito.mock(InputStream.class));
        Mockito.when(file.getContentType()).thenReturn("text");
        Mockito.when(productMediaRepository.findById(form.getProductMediaId())).thenReturn(Optional.of(productMedia));
        Mockito.when(fileExtensionRepository.findFileExtensionByName(fileExtension.getName())).thenReturn(fileExtension);
        Mockito.when(minIoService.uploadFile(Mockito.any(UploadFileForm.class))).thenAnswer(a -> {
            UploadFileForm uploadFileForm = a.getArgument(0);
            return uploadFileForm.getFileName() + "." + fileExtension.getName();
        });
        Mockito.when(mediaElementRepository.save(Mockito.any(MediaElement.class))).thenAnswer(a -> a.getArgument(0));

        ProductMediaDto result = productMediaApiService.saveProductImage(form);

        Assertions.assertEquals(form.getProductMediaId(), result.getProductId());
        Assertions.assertEquals(1, result.getMediaElements().size());
        Assertions.assertEquals(fileExtension.getName(), result.getMediaElements().get(0).getFileExtension().getName());
    }

    @Test
    public void saveImageToNotExistedTest() throws Exception {
        AddMediaToProductForm form = AddMediaToProductFormBuilder.createProductMediaForm().build();

        Mockito.when(productMediaRepository.findById(form.getProductMediaId())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductMediaNotFoundException.class, () -> productMediaApiService.saveProductImage(form));
    }

    @Test
    public void saveImageRepositoryExceptionTest() throws Exception {
        FileExtension fileExtension = FileExtensionBuilder.fileExtension()
                .name("txt")
                .build();
        MultipartFile file = Mockito.mock(MultipartFile.class);
        AddMediaToProductForm form = AddMediaToProductFormBuilder.createProductMediaForm()
                .multipartFile(file)
                .build();
        ProductMedia productMedia = ProductMediaBuilder.productMedia().productId(form.getProductMediaId()).build();

        Mockito.when(file.getOriginalFilename()).thenReturn("some_file.txt");
        Mockito.when(file.getInputStream()).thenReturn(Mockito.mock(InputStream.class));
        Mockito.when(file.getContentType()).thenReturn("text");
        Mockito.when(productMediaRepository.findById(form.getProductMediaId())).thenReturn(Optional.ofNullable(productMedia));
        Mockito.when(fileExtensionRepository.findFileExtensionByName(fileExtension.getName())).thenReturn(fileExtension);
        Mockito.when(minIoService.uploadFile(Mockito.any(UploadFileForm.class))).thenAnswer(a -> {
            UploadFileForm uploadFileForm = a.getArgument(0);
            return uploadFileForm.getFileName() + "." + fileExtension.getName();
        });
        Mockito.when(mediaElementRepository.save(Mockito.any(MediaElement.class))).thenThrow(RuntimeException.class);

        Assertions.assertThrows(RuntimeException.class, () -> productMediaApiService.saveProductImage(form));
    }

    @Test
    public void saveImageWrongFileExtensionTest() throws Exception {
        FileExtension fileExtension = FileExtensionBuilder.fileExtension()
                .name("text")
                .build();
        MultipartFile file = Mockito.mock(MultipartFile.class);
        AddMediaToProductForm form = AddMediaToProductFormBuilder.createProductMediaForm()
                .multipartFile(file)
                .build();
        ProductMedia productMedia = ProductMediaBuilder.productMedia().productId(form.getProductMediaId()).build();

        Mockito.when(file.getOriginalFilename()).thenReturn("some_file.wrong");
        Mockito.when(file.getInputStream()).thenReturn(Mockito.mock(InputStream.class));
        Mockito.when(file.getContentType()).thenReturn("text");
        Mockito.when(productMediaRepository.findById(form.getProductMediaId())).thenReturn(Optional.ofNullable(productMedia));
        Mockito.when(fileExtensionRepository.findFileExtensionByName(fileExtension.getName())).thenReturn(fileExtension);

        Assertions.assertThrows(NotSupportedFileExtensionException.class,
                () -> productMediaApiService.saveProductImage(form));
    }

    @Test
    public void saveImageWrongNullDataTest() throws Exception {
        FileExtension fileExtension = FileExtensionBuilder.fileExtension()
                .name("text")
                .build();
        MultipartFile file = Mockito.mock(MultipartFile.class);
        ProductMedia productMedia = ProductMediaBuilder.productMedia().build();

        Mockito.when(file.getOriginalFilename()).thenReturn("some_file.wrong");
        Mockito.when(file.getInputStream()).thenReturn(Mockito.mock(InputStream.class));
        Mockito.when(file.getContentType()).thenReturn("text");
        Mockito.when(productMediaRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(productMedia));
        Mockito.when(fileExtensionRepository.findFileExtensionByName(fileExtension.getName())).thenReturn(fileExtension);

        Assertions.assertThrows(ProductMediaNotFoundException.class,
                () -> productMediaApiService.saveProductImage(new AddMediaToProductForm()));
    }

    @Test
    public void saveImageWrongNullTest() {
        Assertions.assertThrows(NullPointerException.class, () -> productMediaApiService.saveProductImage(null));
    }

    @Test
    public void removeProductImageTest() {
        ProductMedia productMedia = ProductMediaBuilder.productMedia().build();
        productMedia.setMediaElements(List.of(
                MediaElementBuilder.mediaElement().productMedia(productMedia).build(),
                MediaElementBuilder.mediaElement().productMedia(productMedia).build()
        ));
        MediaElement mediaElement = productMedia.getMediaElements().get(0);

        Mockito.when(productMediaRepository.findById(productMedia.getId())).thenReturn(Optional.of(productMedia));
        Mockito.when(minIoService.removeFile(Mockito.any(RemoveFileForm.class))).thenReturn(mediaElement.getFileName());
        Mockito.doNothing().when(mediaElementRepository).delete(Mockito.any(MediaElement.class));

        Assertions.assertEquals(mediaElement.getId(),
                productMediaApiService.removeProductImage(productMedia.getId(), mediaElement.getId()));
    }

    @Test
    public void removeProductImageRepositoryExceptionTest() {
        ProductMedia productMedia = ProductMediaBuilder.productMedia().build();
        productMedia.setMediaElements(List.of(
                MediaElementBuilder.mediaElement().productMedia(productMedia).build(),
                MediaElementBuilder.mediaElement().productMedia(productMedia).build()
        ));
        MediaElement mediaElement = productMedia.getMediaElements().get(0);

        Mockito.when(productMediaRepository.findByProductId(productMedia.getProductId())).thenReturn(Optional.of(productMedia));
        Mockito.when(minIoService.removeFile(Mockito.any(RemoveFileForm.class))).thenReturn(mediaElement.getFileName());
        Mockito.doThrow(RuntimeException.class).when(mediaElementRepository).delete(Mockito.any(MediaElement.class));

        Assertions.assertThrows(RuntimeException.class,
                () -> productMediaApiService.removeProductImage(productMedia.getProductId(), mediaElement.getId()));
    }

    @Test
    public void removeProductImageMinIOExceptionTest() {
        ProductMedia productMedia = ProductMediaBuilder.productMedia().build();
        productMedia.setMediaElements(List.of(
                MediaElementBuilder.mediaElement().productMedia(productMedia).build(),
                MediaElementBuilder.mediaElement().productMedia(productMedia).build()
        ));
        MediaElement mediaElement = productMedia.getMediaElements().get(0);

        Mockito.when(productMediaRepository.findById(productMedia.getProductId())).thenReturn(Optional.of(productMedia));
        Mockito.when(minIoService.removeFile(Mockito.any(RemoveFileForm.class))).thenThrow(MinIoFileRemoveException.class);

        Assertions.assertThrows(MinIoFileRemoveException.class,
                () -> productMediaApiService.removeProductImage(productMedia.getProductId(), mediaElement.getId()));
    }

    @Test
    public void removeProductNotBelongImageTest() {
        ProductMedia productMedia = ProductMediaBuilder.productMedia().build();

        Mockito.when(productMediaRepository.findById(productMedia.getProductId())).thenReturn(Optional.of(productMedia));

        Assertions.assertThrows(ImageNotBelongToProductException.class,
                () -> productMediaApiService.removeProductImage(productMedia.getProductId(), 1001L));
    }

    @Test
    public void removeNotExistedProductImageTest() {
        ProductMedia productMedia = ProductMediaBuilder.productMedia().build();

        Mockito.when(productMediaRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductMediaNotFoundException.class,
                () -> productMediaApiService.removeProductImage(productMedia.getProductId(), 1001L));
    }

    @Test
    public void getProductImagesMetadataTest() {
        ProductMedia productMedia = ProductMediaBuilder.productMedia()
                .mediaElements(List.of(
                        MediaElementBuilder.mediaElement()
                                .fileExtension(FileExtensionBuilder.fileExtension().build())
                                .build(),
                        MediaElementBuilder.mediaElement()
                                .fileExtension(FileExtensionBuilder.fileExtension().build())
                                .build()
                ))
                .build();

        Mockito.when(productMediaRepository.findById(productMedia.getId())).thenReturn(Optional.of(productMedia));

        List<ImgMetadataDto> result = productMediaApiService.getProductImagesMetadata(productMedia.getId());

        Assertions.assertEquals(productMedia.getMediaElements().size(), result.size());
        result.forEach(r -> {
            Assertions.assertTrue(productMedia.getMediaElements().stream()
                    .anyMatch(me -> compareMediaElementAndImageMetadata(me, r)));
        });
    }

    @Test
    public void getProductImagesMetadataRepositoryExceptionTest() {
        Mockito.when(productMediaRepository.findById(Mockito.anyLong())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(RuntimeException.class,
                () -> productMediaApiService.getProductImagesMetadata(1L));
    }

    @Test
    public void getProductImagesMetadataNullDataTest() {
        Mockito.when(productMediaRepository.findById(Mockito.nullable(Long.class))).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductMediaNotFoundException.class,
                () -> productMediaApiService.getProductImagesMetadata(null));
    }

    @Test
    public void getProductImageMetadataTest() {
        ProductMedia productMedia = ProductMediaBuilder.productMedia()
                .mediaElements(List.of(
                        MediaElementBuilder.mediaElement()
                                .fileExtension(FileExtensionBuilder.fileExtension().build())
                                .build(),
                        MediaElementBuilder.mediaElement()
                                .fileExtension(FileExtensionBuilder.fileExtension().build())
                                .build()
                ))
                .build();
        MediaElement mediaElement = productMedia.getMediaElements().get(0);

        Mockito.when(productMediaRepository.findById(productMedia.getId())).thenReturn(Optional.of(productMedia));

        ImgMetadataDto result = productMediaApiService.getProductImageMetadata(productMedia.getId(), mediaElement.getId());

        Assertions.assertTrue(compareMediaElementAndImageMetadata(mediaElement, result));
    }

    @Test
    public void getProductImageMetadataRepositoryExceptionTest() {
        Mockito.when(productMediaRepository.findById(Mockito.anyLong())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(RuntimeException.class,
                () -> productMediaApiService.getProductImageMetadata(1L, 1L));
    }

    @Test
    public void getProductImageMetadataImageNotBelongProductTest() {
        ProductMedia productMedia = ProductMediaBuilder.productMedia()
                .mediaElements(List.of(
                        MediaElementBuilder.mediaElement().build(),
                        MediaElementBuilder.mediaElement().build()
                ))
                .build();
        Mockito.when(productMediaRepository.findById(productMedia.getId())).thenReturn(Optional.of(productMedia));

        Assertions.assertThrows(ImageNotBelongToProductException.class,
                () -> productMediaApiService.getProductImageMetadata(productMedia.getId(), 1001L));
    }

    private boolean compareMediaElementAndImageMetadata(MediaElement me, ImgMetadataDto im) {
        return me.getFileName().equals(im.getFileName()) &&
                me.getFileExtension().getMediaTypeName().equals(im.getFileExtension()) &&
                me.getCreationTime().isEqual(im.getCreationTime()) &&
                me.getLastTimeUpdate().isEqual(im.getLastTimeUpdate());
    }

}
