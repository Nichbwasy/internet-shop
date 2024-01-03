package com.shop.media.service.impl;

import com.shop.common.utils.all.file.FilesUtils;
import com.shop.common.utils.all.generator.StringGenerator;
import com.shop.common.utils.all.mapping.CommonCyclingAvoidingContext;
import com.shop.media.common.data.builder.*;
import com.shop.media.dao.FileExtensionRepository;
import com.shop.media.dao.MediaElementRepository;
import com.shop.media.dao.ProductMediaRepository;
import com.shop.media.dto.ProductMediaDto;
import com.shop.media.dto.form.CreateProductMediaForm;
import com.shop.media.model.FileExtension;
import com.shop.media.model.MediaElement;
import com.shop.media.model.ProductMedia;
import com.shop.media.service.MinIoService;
import com.shop.media.service.ProductMediaApiService;
import com.shop.media.service.exeption.FileReadingException;
import com.shop.media.service.exeption.NotSupportedFileExtensionException;
import com.shop.media.service.exeption.file.extension.FileExtensionNotFoundException;
import com.shop.media.service.exeption.product.ImageNotBelongToProductException;
import com.shop.media.service.exeption.product.ProductMediaNotFoundException;
import com.shop.media.service.mapper.ProductMediaMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductMediaApiServiceImpl implements ProductMediaApiService {

    @Value("${minio.buckets.products-media-bucket-name}")
    private String productsBucketName;
    @Value("#{'${minio.files.supported.img.extensions}'.split(',')}")
    private List<String> supportedImgsExtensions;

    private final ProductMediaRepository productMediaRepository;
    private final MediaElementRepository mediaElementRepository;
    private final FileExtensionRepository fileExtensionRepository;
    private final MinIoService minIoService;
    private final ProductMediaMapper productMediaMapper;

    @Override
    public List<byte[]> loadImagesForProduct(Long productId) {
        ProductMedia productMedia = getProductMediaIfPresent(productId);
        List<MediaElement> mediaElements = productMedia.getMediaElements();
        return mediaElements.stream()
                .map(element -> {
                    try {
                        return minIoService.getFile(
                                GetFileFormBuilder.getFileForm()
                                        .bucketName(productsBucketName)
                                        .fileName(element.getPath() + "/" + element.getFileName())
                                        .build()
                        ).readAllBytes();
                    } catch (IOException e) {
                        log.error("Exception while reading file! {}", e.getMessage());
                        throw new FileReadingException("Exception while reading file! %s".formatted(e.getMessage()));
                    }
                })
                .toList();
    }


    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ProductMediaDto saveProductImage(CreateProductMediaForm form) {
        String fileName = "/" + form.getProductId() + "/imgs/" + StringGenerator.generate(12);
        FileExtension fileExtension = getExistedFileExtension(form);
        String createdFileName = saveFileInMinIO(form, fileName);
        if (productMediaRepository.existsByProductId(form.getProductId())) {
            return addMediaElementToExistedProduct(form, createdFileName, fileExtension);
        } else {
            return createProductAndAddMediaElement(form, createdFileName, fileExtension);
        }
    }

    private ProductMediaDto createProductAndAddMediaElement(CreateProductMediaForm form, String createdFileName, FileExtension fileExtension) {
        ProductMedia productMedia = createNewProductMedia(form);
        MediaElement mediaElement = saveMediaElementInDatabase(form, productMedia, createdFileName, fileExtension);
        productMedia.getMediaElements().add(mediaElement);
        return productMediaMapper.mapToDto(productMedia, new CommonCyclingAvoidingContext());
    }

    private ProductMediaDto addMediaElementToExistedProduct(CreateProductMediaForm form, String createdFileName, FileExtension fileExtension) {
        ProductMedia productMedia = productMediaRepository.findByProductId(form.getProductId());
        saveMediaElementInDatabase(form, productMedia, createdFileName, fileExtension);
        return productMediaMapper.mapToDto(productMedia, new CommonCyclingAvoidingContext());
    }

    private ProductMedia createNewProductMedia(CreateProductMediaForm form) {
        return productMediaRepository.save(ProductMediaBuilder.productMedia()
                .id(null)
                .productId(form.getProductId())
                .build()
        );
    }

    private MediaElement saveMediaElementInDatabase(CreateProductMediaForm form,
                                                    ProductMedia productMedia,
                                                    String createdFileName,
                                                    FileExtension fileExtension) {
        return mediaElementRepository.save(MediaElementBuilder.mediaElement()
                .id(null)
                .productMedia(productMedia)
                .bucketName(productsBucketName)
                .path(FilesUtils.cropFileName(createdFileName))
                .fileName(FilesUtils.cropFilePath(createdFileName))
                .fileSize(form.getMultipartFile().getSize())
                .fileExtension(fileExtension)
                .creationTime(LocalDateTime.now())
                .lastTimeUpdate(LocalDateTime.now())
                .build()
        );
    }

    private String saveFileInMinIO(CreateProductMediaForm form, String fileName) {
        return minIoService.uploadFile(UploadFileFormBuilder.uploadFileForm()
                .bucketName(productsBucketName)
                .fileName(fileName)
                .multipartFile(form.getMultipartFile())
                .build());
    }

    private FileExtension getExistedFileExtension(CreateProductMediaForm form) {
        String extensionName = FilesUtils.extractFileExtensionName(form.getMultipartFile().getOriginalFilename());
        if (supportedImgsExtensions.stream().noneMatch(extensionName::equals)) {
            log.warn("Unable save media! Saved file has unsupported '{}' extension!", extensionName);
            throw new NotSupportedFileExtensionException(
                    "Unable save media! Saved file has unsupported '%s' extension!".formatted(extensionName)
            );
        }
        return getFileExtensionIfPresent(extensionName);
    }

    private FileExtension getFileExtensionIfPresent(String extensionName) {
        FileExtension fileExtension = fileExtensionRepository.findFileExtensionByName(extensionName);
        if (fileExtension == null) {
            log.error("Unable file extension '{}' in repository!", extensionName);
            throw new FileExtensionNotFoundException("Unable file extension '%s' in repository!".formatted(extensionName));
        }
        return fileExtension;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Long removeProductImage(Long productId, Long imageId) {
        ProductMedia productMedia = getProductMediaIfPresent(productId);
        MediaElement mediaElement = productMedia.getMediaElements().stream()
                .filter(element -> element.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Image with id '{}' not belong to the product '{}'!", imageId, productId);
                    return new ImageNotBelongToProductException(
                            "Image with id '%s' not belong to the product '%s'!".formatted(imageId, productId)
                    );
                });
        minIoService.removeFile(RemoveFileFormBuilder.removeFileForm()
                .bucketName(productsBucketName)
                .fileName(mediaElement.getPath() + "/" + mediaElement.getFileName())
                .build());
        mediaElementRepository.delete(mediaElement);
        return imageId;
    }
    private ProductMedia getProductMediaIfPresent(Long productId) {
        ProductMedia productMedia = productMediaRepository.findByProductId(productId);
        if (productMedia == null) {
            log.warn("Unable find the product media with id '{}'!", productId);
            throw new ProductMediaNotFoundException("Unable find the product media with id '%s'!".formatted(productId));
        }
        return productMedia;
    }

}
