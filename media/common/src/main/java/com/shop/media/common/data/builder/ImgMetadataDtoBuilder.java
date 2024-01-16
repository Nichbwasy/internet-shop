package com.shop.media.common.data.builder;

import com.shop.common.utils.all.generator.StringGenerator;
import com.shop.common.utils.all.test.data.builder.CommonObjectBuilder;
import com.shop.media.dto.metadata.ImgMetadataDto;

import java.time.LocalDateTime;
import java.util.function.Consumer;

public class ImgMetadataDtoBuilder extends CommonObjectBuilder<ImgMetadataDto> {

    private String fileName = StringGenerator.generate(random.nextInt(8, 12));
    private Long fileSize = random.nextLong(0, Long.MAX_VALUE);
    private String fileExtension = StringGenerator.generate(5);
    private LocalDateTime creationTime = LocalDateTime.now().minusMinutes(random.nextInt(400,1000));
    private LocalDateTime lastTimeUpdate = LocalDateTime.now().minusMinutes(random.nextInt(0, 400));

    private ImgMetadataDtoBuilder() {}

    private ImgMetadataDtoBuilder(ImgMetadataDtoBuilder builder) {
        this.fileName = builder.fileName;
        this.fileSize = builder.fileSize;
        this.fileExtension = builder.fileExtension;
        this.creationTime = builder.creationTime;
        this.lastTimeUpdate = builder.lastTimeUpdate;
    }

    public static ImgMetadataDtoBuilder imgMetadataDto() {
        return new ImgMetadataDtoBuilder();
    }

    public ImgMetadataDtoBuilder fileName(String fileName) {
        return copyWith(b -> b.fileName = fileName);
    }

    public ImgMetadataDtoBuilder fileSize(Long fileSize) {
        return copyWith(b -> b.fileSize = fileSize);
    }

    public ImgMetadataDtoBuilder fileExtension(String fileExtension) {
        return copyWith(b -> b.fileExtension = fileExtension);
    }

    public ImgMetadataDtoBuilder creationTime(LocalDateTime creationTime) {
        return copyWith(b -> b.creationTime = creationTime);
    }

    public ImgMetadataDtoBuilder lastTimeUpdate(LocalDateTime lastTimeUpdate) {
        return copyWith(b -> b.lastTimeUpdate = lastTimeUpdate);
    }

    @Override
    public ImgMetadataDto build() {
        ImgMetadataDto imgMetadataDto = new ImgMetadataDto();
        imgMetadataDto.setFileName(fileName);
        imgMetadataDto.setFileExtension(fileExtension);
        imgMetadataDto.setFileSize(fileSize);
        imgMetadataDto.setCreationTime(creationTime);
        imgMetadataDto.setLastTimeUpdate(lastTimeUpdate);
        return imgMetadataDto;
    }

    private ImgMetadataDtoBuilder copyWith(Consumer<ImgMetadataDtoBuilder> consumer) {
        ImgMetadataDtoBuilder imgMetadataDtoBuilder = new ImgMetadataDtoBuilder(this);
        consumer.accept(imgMetadataDtoBuilder);
        return imgMetadataDtoBuilder;
    }

}
