package com.shop.media.service;

import com.shop.media.common.data.builder.CreateProductMediaFormBuilder;
import com.shop.media.common.data.builder.FileExtensionBuilder;
import com.shop.media.common.data.builder.MediaElementBuilder;
import com.shop.media.common.data.builder.ProductMediaBuilder;
import com.shop.media.dao.FileExtensionRepository;
import com.shop.media.dao.MediaElementRepository;
import com.shop.media.dao.ProductMediaRepository;
import com.shop.media.dao.exception.MinIoFileGetException;
import com.shop.media.dao.exception.MinIoFileRemoveException;
import com.shop.media.dto.ProductMediaDto;
import com.shop.media.dto.form.CreateProductMediaForm;
import com.shop.media.dto.form.GetFileForm;
import com.shop.media.dto.form.RemoveFileForm;
import com.shop.media.dto.form.UploadFileForm;
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
import java.util.concurrent.atomic.AtomicReference;

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

        Mockito.when(productMediaRepository.findByProductId(productMedia.getId())).thenReturn(productMedia);
        Mockito.when(minIoService.getFile(Mockito.any(GetFileForm.class))).thenReturn(Mockito.mock(InputStream.class));

        List<byte[]> result = productMediaApiService.loadImagesForProduct(productMedia.getId());
        Assertions.assertEquals(2, result.size());
    }

    @Test
    public void loadImagesForProductNoMediaTest() {
        ProductMedia productMedia = ProductMediaBuilder.productMedia().build();

        Mockito.when(productMediaRepository.findByProductId(productMedia.getId())).thenReturn(productMedia);
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

        Mockito.when(productMediaRepository.findByProductId(productMedia.getId())).thenReturn(productMedia);
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

        Mockito.when(productMediaRepository.findByProductId(productMedia.getId())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(RuntimeException.class,
                () -> productMediaApiService.loadImagesForProduct(productMedia.getId()));
    }

    @Test
    public void loadImagesForProductMediaNullTest() {
        Mockito.when(productMediaRepository.findByProductId(Mockito.nullable(Long.class)))
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
        CreateProductMediaForm form = CreateProductMediaFormBuilder.createProductMediaForm()
                .multipartFile(file)
                .build();
        ProductMedia productMedia = ProductMediaBuilder.productMedia().productId(form.getProductId()).build();

        Mockito.when(file.getOriginalFilename()).thenReturn("some_file.txt");
        Mockito.when(file.getInputStream()).thenReturn(Mockito.mock(InputStream.class));
        Mockito.when(file.getContentType()).thenReturn("text");
        Mockito.when(productMediaRepository.existsByProductId(form.getProductId())).thenReturn(true);
        Mockito.when(productMediaRepository.findByProductId(form.getProductId())).thenReturn(productMedia);
        Mockito.when(fileExtensionRepository.findFileExtensionByName(fileExtension.getName())).thenReturn(fileExtension);
        Mockito.when(minIoService.uploadFile(Mockito.any(UploadFileForm.class))).thenAnswer(a -> {
            UploadFileForm uploadFileForm = a.getArgument(0);
            return uploadFileForm.getFileName() + "." + fileExtension.getName();
        });
        Mockito.when(mediaElementRepository.save(Mockito.any(MediaElement.class))).thenAnswer(a -> {
            MediaElement mediaElement = a.getArgument(0);
            productMedia.getMediaElements().add(mediaElement);
            return mediaElement;
        });

        ProductMediaDto result = productMediaApiService.saveProductImage(form);

        Assertions.assertEquals(form.getProductId(), result.getProductId());
        Assertions.assertEquals(1, result.getMediaElements().size());
        Assertions.assertEquals(fileExtension.getName(), result.getMediaElements().get(0).getFileExtension().getName());
    }

    @Test
    public void saveImageToNotExistedTest() throws Exception {
        AtomicReference<ProductMedia> product = new AtomicReference<>();
        FileExtension fileExtension = FileExtensionBuilder.fileExtension()
                .name("txt")
                .build();
        MultipartFile file = Mockito.mock(MultipartFile.class);
        CreateProductMediaForm form = CreateProductMediaFormBuilder.createProductMediaForm()
                .multipartFile(file)
                .build();

        Mockito.when(file.getOriginalFilename()).thenReturn("some_file.txt");
        Mockito.when(file.getInputStream()).thenReturn(Mockito.mock(InputStream.class));
        Mockito.when(file.getContentType()).thenReturn("text");
        Mockito.when(productMediaRepository.existsByProductId(form.getProductId())).thenReturn(false);
        Mockito.when(productMediaRepository.save(Mockito.any(ProductMedia.class))).thenAnswer(a -> {
            ProductMedia productMedia = a.getArgument(0);
            productMedia.setId(1001L);
            product.set(productMedia);
            return productMedia;
        });
        Mockito.when(fileExtensionRepository.findFileExtensionByName(fileExtension.getName())).thenReturn(fileExtension);
        Mockito.when(minIoService.uploadFile(Mockito.any(UploadFileForm.class))).thenAnswer(a -> {
            UploadFileForm uploadFileForm = a.getArgument(0);
            return uploadFileForm.getFileName() + "." + fileExtension.getName();
        });
        Mockito.when(mediaElementRepository.save(Mockito.any(MediaElement.class))).thenAnswer(a -> {

            MediaElement mediaElement = a.getArgument(0);
            mediaElement.setProductMedia(product.get());
            return mediaElement;
        });

        ProductMediaDto result = productMediaApiService.saveProductImage(form);

        Assertions.assertEquals(form.getProductId(), result.getProductId());
        Assertions.assertEquals(1, result.getMediaElements().size());
        Assertions.assertEquals(fileExtension.getName(), result.getMediaElements().get(0).getFileExtension().getName());
    }

    @Test
    public void saveImageRepositoryExceptionTest() throws Exception {
        FileExtension fileExtension = FileExtensionBuilder.fileExtension()
                .name("txt")
                .build();
        MultipartFile file = Mockito.mock(MultipartFile.class);
        CreateProductMediaForm form = CreateProductMediaFormBuilder.createProductMediaForm()
                .multipartFile(file)
                .build();
        ProductMedia productMedia = ProductMediaBuilder.productMedia().productId(form.getProductId()).build();

        Mockito.when(file.getOriginalFilename()).thenReturn("some_file.txt");
        Mockito.when(file.getInputStream()).thenReturn(Mockito.mock(InputStream.class));
        Mockito.when(file.getContentType()).thenReturn("text");
        Mockito.when(productMediaRepository.existsByProductId(form.getProductId())).thenReturn(true);
        Mockito.when(productMediaRepository.findByProductId(form.getProductId())).thenReturn(productMedia);
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
        CreateProductMediaForm form = CreateProductMediaFormBuilder.createProductMediaForm()
                .multipartFile(file)
                .build();
        ProductMedia productMedia = ProductMediaBuilder.productMedia().productId(form.getProductId()).build();

        Mockito.when(file.getOriginalFilename()).thenReturn("some_file.wrong");
        Mockito.when(file.getInputStream()).thenReturn(Mockito.mock(InputStream.class));
        Mockito.when(file.getContentType()).thenReturn("text");
        Mockito.when(productMediaRepository.existsByProductId(form.getProductId())).thenReturn(true);
        Mockito.when(productMediaRepository.findByProductId(form.getProductId())).thenReturn(productMedia);
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
        Mockito.when(productMediaRepository.existsByProductId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(productMediaRepository.findByProductId(Mockito.anyLong())).thenReturn(productMedia);
        Mockito.when(fileExtensionRepository.findFileExtensionByName(fileExtension.getName())).thenReturn(fileExtension);

        Assertions.assertThrows(NullPointerException.class,
                () -> productMediaApiService.saveProductImage(new CreateProductMediaForm()));
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

        Mockito.when(productMediaRepository.findByProductId(productMedia.getProductId())).thenReturn(productMedia);
        Mockito.when(minIoService.removeFile(Mockito.any(RemoveFileForm.class))).thenReturn(mediaElement.getFileName());
        Mockito.doNothing().when(mediaElementRepository).delete(Mockito.any(MediaElement.class));

        Assertions.assertEquals(mediaElement.getId(),
                productMediaApiService.removeProductImage(productMedia.getProductId(), mediaElement.getId()));
    }

    @Test
    public void removeProductImageRepositoryExceptionTest() {
        ProductMedia productMedia = ProductMediaBuilder.productMedia().build();
        productMedia.setMediaElements(List.of(
                MediaElementBuilder.mediaElement().productMedia(productMedia).build(),
                MediaElementBuilder.mediaElement().productMedia(productMedia).build()
        ));
        MediaElement mediaElement = productMedia.getMediaElements().get(0);

        Mockito.when(productMediaRepository.findByProductId(productMedia.getProductId())).thenReturn(productMedia);
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

        Mockito.when(productMediaRepository.findByProductId(productMedia.getProductId())).thenReturn(productMedia);
        Mockito.when(minIoService.removeFile(Mockito.any(RemoveFileForm.class))).thenThrow(MinIoFileRemoveException.class);

        Assertions.assertThrows(MinIoFileRemoveException.class,
                () -> productMediaApiService.removeProductImage(productMedia.getProductId(), mediaElement.getId()));
    }

    @Test
    public void removeProductNotBelongImageTest() {
        ProductMedia productMedia = ProductMediaBuilder.productMedia().build();

        Mockito.when(productMediaRepository.findByProductId(productMedia.getProductId())).thenReturn(productMedia);

        Assertions.assertThrows(ImageNotBelongToProductException.class,
                () -> productMediaApiService.removeProductImage(productMedia.getProductId(), 1001L));
    }

    @Test
    public void removeNotExistedProductImageTest() {
        ProductMedia productMedia = ProductMediaBuilder.productMedia().build();

        Mockito.when(productMediaRepository.findByProductId(Mockito.anyLong())).thenReturn(null);

        Assertions.assertThrows(ProductMediaNotFoundException.class,
                () -> productMediaApiService.removeProductImage(productMedia.getProductId(), 1001L));
    }

}
