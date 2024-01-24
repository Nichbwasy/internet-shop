package com.shop.media.service.impl;

import com.shop.common.utils.all.file.FilesUtils;
import com.shop.common.utils.all.generator.StringGenerator;
import com.shop.common.utils.all.mapping.CommonCyclingAvoidingContext;
import com.shop.media.common.data.builder.*;
import com.shop.media.dao.FileExtensionRepository;
import com.shop.media.dao.MediaElementRepository;
import com.shop.media.dao.ProductMediaRepository;
import com.shop.media.dto.ProductMediaDto;
import com.shop.media.dto.form.AddMediaToProductForm;
import com.shop.media.dto.form.CreateMediaForProductForm;
import com.shop.media.dto.metadata.DockMetadataDto;
import com.shop.media.dto.metadata.ImgMetadataDto;
import com.shop.media.model.FileExtension;
import com.shop.media.model.MediaElement;
import com.shop.media.model.ProductMedia;
import com.shop.media.service.MinIoService;
import com.shop.media.service.ProductMediaApiService;
import com.shop.media.service.exeption.FileReadingException;
import com.shop.media.service.exeption.NotSupportedFileExtensionException;
import com.shop.media.service.exeption.file.extension.FileExtensionNotFoundException;
import com.shop.media.service.exeption.product.MediaElementNotBelongToProductException;
import com.shop.media.service.exeption.product.ProductMediaAlreadyExistsException;
import com.shop.media.service.exeption.product.ProductMediaNotFoundException;
import com.shop.media.service.mapper.DockMetadataMapper;
import com.shop.media.service.mapper.ImgMetadataMapper;
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
    private final ImgMetadataMapper imgMetadataMapper;
    private final DockMetadataMapper dockMetadataMapper;

    @Override
    public ProductMediaDto createNewMediaForProduct(CreateMediaForProductForm form) {
        checkIfMediaForProductAlreadyExists(form.getProductId());
        return productMediaMapper.mapToDto(productMediaRepository.save(ProductMediaBuilder.productMedia()
                .id(null)
                .productId(form.getProductId())
                .build()
        ), new CommonCyclingAvoidingContext());
    }

    private void checkIfMediaForProductAlreadyExists(Long productId) {
        if (productMediaRepository.existsByProductId(productId)) {
            log.error("Media for the product '{}' already exists!", productId);
            throw new ProductMediaAlreadyExistsException(
                    "Media for the product '%s' already exists!".formatted(productId)
            );
        }
    }

    @Override
    public List<byte[]> loadProductImages(Long productMediaId) {
        ProductMedia productMedia = getProductMediaIfPresent(productMediaId);
        return productMedia.getMediaElements().stream()
                .map(element -> {
                    try {
                        return minIoService.getFile(
                                GetFileFormBuilder.getFileForm()
                                        .bucketName(element.getBucketName())
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
    public ProductMediaDto saveProductImage(AddMediaToProductForm form) {
        ProductMedia productMedia = getProductMediaIfPresent(form.getProductMediaId());
        FileExtension fileExtension = getExistedFileExtension(form);

        String createdFileName = saveFileInMinIO(form, productMedia, "imgs");

        saveMediaElementInDatabase(form, productMedia, createdFileName, fileExtension);

        return productMediaMapper.mapToDto(productMedia, new CommonCyclingAvoidingContext());
    }
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Long removeProductImage(Long productId, Long imageId) {
        ProductMedia productMedia = getProductMediaIfPresent(productId);
        MediaElement mediaElement = findMediaElementInProduct(productMedia, imageId);
        removeMediaElementFromMinIO(mediaElement);
        mediaElementRepository.delete(mediaElement);
        return imageId;
    }

    @Override
    public List<ImgMetadataDto> getProductImagesMetadata(Long productId) {
        ProductMedia productMedia = getProductMediaIfPresent(productId);
        return productMedia.getMediaElements().stream()
                .map(imgMetadataMapper::mapToDto)
                .toList();
    }

    @Override
    public ImgMetadataDto getProductImageMetadata(Long productId, Long imageId) {
        ProductMedia productMedia = getProductMediaIfPresent(productId);
        MediaElement mediaElement = findMediaElementInProduct(productMedia, imageId);

        return imgMetadataMapper.mapToDto(mediaElement);
    }

    @Override
    public byte[] loadProductDock(Long productId, Long dockId) {
        ProductMedia productMedia = getProductMediaIfPresent(productId);
        MediaElement mediaElement = findMediaElementInProduct(productMedia, dockId);
        try {
            return minIoService.getFile(GetFileFormBuilder.getFileForm()
                    .bucketName(mediaElement.getBucketName())
                    .fileName(mediaElement.getPath() + "/" + mediaElement.getFileName())
                    .build()
            ).readAllBytes();
        } catch (IOException e) {
            log.error("Exception while reading product document file! {}", e.getMessage());
            throw new FileReadingException("Exception while reading product document file! %s".formatted(e.getMessage()));
        }
    }

    @Override
    public List<DockMetadataDto> getProductDocksMetadata(Long productId) {
        ProductMedia productMedia = getProductMediaIfPresent(productId);
        return productMedia.getMediaElements().stream()
                .map(dockMetadataMapper::mapToDto)
                .toList();
    }

    @Override
    public DockMetadataDto getProductDockMetadata(Long productId, Long dockId) {
        ProductMedia productMedia = getProductMediaIfPresent(productId);
        MediaElement mediaElement = findMediaElementInProduct(productMedia, dockId);
        return dockMetadataMapper.mapToDto(mediaElement);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ProductMediaDto saveProductDock(AddMediaToProductForm form) {
        ProductMedia productMedia = getProductMediaIfPresent(form.getProductMediaId());
        FileExtension fileExtension = getExistedFileExtension(form);

        String createdFileName = saveFileInMinIO(form, productMedia, "docks");

        saveMediaElementInDatabase(form, productMedia, createdFileName, fileExtension);

        return productMediaMapper.mapToDto(productMedia, new CommonCyclingAvoidingContext());
    }


    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Long removeProductDock(Long productId, Long dockId) {
        ProductMedia productMedia = getProductMediaIfPresent(productId);
        MediaElement mediaElement = findMediaElementInProduct(productMedia, dockId);
        removeMediaElementFromMinIO(mediaElement);
        mediaElementRepository.delete(mediaElement);
        return dockId;
    }

    private MediaElement findMediaElementInProduct(ProductMedia productMedia, Long elementId) {
        return productMedia.getMediaElements().stream()
                .filter(pm -> pm.getId().equals(elementId))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Element with id '{}' doesn't belong to the product media '{}'!", elementId, productMedia.getId());
                    return new MediaElementNotBelongToProductException(
                            "Element with id '%s' doesn't belong to the product media '%s'!"
                                    .formatted(elementId, productMedia.getId())
                    );
                });
    }

    private MediaElement saveMediaElementInDatabase(AddMediaToProductForm form,
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

    private String saveFileInMinIO(AddMediaToProductForm form, ProductMedia media, String folder) {
        String fileName = "/" + media.getId() + "/" + folder + "/" + StringGenerator.generate(12);
        return minIoService.uploadFile(UploadFileFormBuilder.uploadFileForm()
                .bucketName(productsBucketName)
                .fileName(fileName)
                .multipartFile(form.getMultipartFile())
                .build());
    }

    private void removeMediaElementFromMinIO(MediaElement mediaElement) {
        minIoService.removeFile(RemoveFileFormBuilder.removeFileForm()
                .bucketName(mediaElement.getBucketName())
                .fileName(mediaElement.getPath() + "/" + mediaElement.getFileName())
                .build());
    }

    private FileExtension getExistedFileExtension(AddMediaToProductForm form) {
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
        return fileExtensionRepository.findFileExtensionByName(extensionName)
                .orElseThrow(() -> {
                    log.error("Unable file extension '{}' in repository!", extensionName);
                    return new FileExtensionNotFoundException(
                            "Unable file extension '%s' in repository!".formatted(extensionName)
                    );
                });
    }

    private ProductMedia getProductMediaIfPresent(Long id) {
        return productMediaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Unable find the product media with id '{}'!", id);
                    return new ProductMediaNotFoundException(
                            "Unable find the product media with id '%s'!".formatted(id)
                    );
                });
    }

}
