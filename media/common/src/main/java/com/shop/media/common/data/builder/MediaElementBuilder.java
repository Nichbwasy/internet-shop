package com.shop.media.common.data.builder;

import com.shop.common.utils.all.generator.StringGenerator;
import com.shop.common.utils.all.test.data.builder.CommonObjectBuilder;
import com.shop.media.model.FileExtension;
import com.shop.media.model.MediaElement;
import com.shop.media.model.ProductMedia;

import java.time.LocalDateTime;
import java.util.function.Consumer;

public class MediaElementBuilder extends CommonObjectBuilder<MediaElement> {

    private Long id = random.nextLong(1, 1000);
    private ProductMedia productMedia = new ProductMedia();
    private String bucketName = StringGenerator.generate(random.nextInt(4, 8));
    private String path = "/" + StringGenerator.generate(random.nextInt(4, 6));
    private String fileName = StringGenerator.generate(random.nextInt(4, 8)) + ".test";
    private Long fileSize = random.nextLong(0, Long.MAX_VALUE);
    private FileExtension fileExtension = new FileExtension();
    private LocalDateTime creationTime = LocalDateTime.now().minusMinutes(random.nextInt(500, 1000));
    private LocalDateTime lastTimeUpdate = LocalDateTime.now().minusMinutes(random.nextInt(1, 500));

    private MediaElementBuilder() {}

    private MediaElementBuilder(MediaElementBuilder builder) {
        this.id = builder.id;
        this.productMedia = builder.productMedia;
        this.bucketName = builder.bucketName;
        this.path = builder.path;
        this.fileName = builder.fileName;
        this.fileSize = builder.fileSize;
        this.fileExtension = builder.fileExtension;
        this.creationTime = builder.creationTime;
        this.lastTimeUpdate = builder.lastTimeUpdate;
    }

    public static MediaElementBuilder mediaElement() {
        return new MediaElementBuilder();
    }

    public MediaElementBuilder id(Long id) {
        return copyWith(b -> b.id = id);
    }

    public MediaElementBuilder productMedia(ProductMedia productMedia) {
        return copyWith(b -> b.productMedia = productMedia);
    }

    public MediaElementBuilder bucketName(String bucketName) {
        return copyWith(b -> b.bucketName = bucketName);
    }

    public MediaElementBuilder path(String path) {
        return copyWith(b -> b.path = path);
    }

    public MediaElementBuilder fileName(String fileName) {
        return copyWith(b -> b.fileName = fileName);
    }

    public MediaElementBuilder fileSize(long fileSize) {
        return copyWith(b -> b.fileSize = fileSize);
    }

    public MediaElementBuilder fileExtension(FileExtension fileExtension) {
        return copyWith(b -> b.fileExtension = fileExtension);
    }

    public MediaElementBuilder creationTime(LocalDateTime creationTime) {
        return copyWith(b -> b.creationTime = creationTime);
    }

    public MediaElementBuilder lastTimeUpdate(LocalDateTime lastTimeUpdate) {
        return copyWith(b -> b.lastTimeUpdate = lastTimeUpdate);
    }

    @Override
    public MediaElement build() {
        MediaElement mediaElement = new MediaElement();
        mediaElement.setId(id);
        mediaElement.setProductMedia(productMedia);
        mediaElement.setBucketName(bucketName);
        mediaElement.setPath(path);
        mediaElement.setFileName(fileName);
        mediaElement.setFileSize(fileSize);
        mediaElement.setFileExtension(fileExtension);
        mediaElement.setCreationTime(creationTime);
        mediaElement.setLastTimeUpdate(lastTimeUpdate);
        return mediaElement;
    }

    private MediaElementBuilder copyWith(Consumer<MediaElementBuilder> consumer) {
        MediaElementBuilder mediaElementBuilder = new MediaElementBuilder(this);
        consumer.accept(mediaElementBuilder);
        return mediaElementBuilder;
    }

}
