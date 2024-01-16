package com.shop.media.common.data.builder;

import com.shop.common.utils.all.generator.StringGenerator;
import com.shop.common.utils.all.test.data.builder.CommonObjectBuilder;
import com.shop.media.dto.metadata.DockMetadataDto;

import java.time.LocalDateTime;
import java.util.function.Consumer;

public class DockMetadataDtoBuilder extends CommonObjectBuilder<DockMetadataDto> {

    private String fileName = StringGenerator.generate(random.nextInt(8, 12));
    private Long fileSize = random.nextLong(0, Long.MAX_VALUE);
    private String fileExtension = StringGenerator.generate(5);
    private LocalDateTime creationTime = LocalDateTime.now().minusMinutes(random.nextInt(400,1000));
    private LocalDateTime lastTimeUpdate = LocalDateTime.now().minusMinutes(random.nextInt(0, 400));

    private DockMetadataDtoBuilder() {}

    private DockMetadataDtoBuilder(DockMetadataDtoBuilder builder) {
        this.fileName = builder.fileName;
        this.fileSize = builder.fileSize;
        this.fileExtension = builder.fileExtension;
        this.creationTime = builder.creationTime;
        this.lastTimeUpdate = builder.lastTimeUpdate;
    }

    public static DockMetadataDtoBuilder dockMetadataDto() {
        return new DockMetadataDtoBuilder();
    }

    public DockMetadataDtoBuilder fileName(String fileName) {
        return copyWith(b -> b.fileName = fileName);
    }

    public DockMetadataDtoBuilder fileSize(Long fileSize) {
        return copyWith(b -> b.fileSize = fileSize);
    }

    public DockMetadataDtoBuilder fileExtension(String fileExtension) {
        return copyWith(b -> b.fileExtension = fileExtension);
    }

    public DockMetadataDtoBuilder creationTime(LocalDateTime creationTime) {
        return copyWith(b -> b.creationTime = creationTime);
    }

    public DockMetadataDtoBuilder lastTimeUpdate(LocalDateTime lastTimeUpdate) {
        return copyWith(b -> b.lastTimeUpdate = lastTimeUpdate);
    }

    @Override
    public DockMetadataDto build() {
        DockMetadataDto dockMetadataDto = new DockMetadataDto();
        dockMetadataDto.setFileName(fileName);
        dockMetadataDto.setFileSize(fileSize);
        dockMetadataDto.setFileExtension(fileExtension);
        dockMetadataDto.setCreationTime(creationTime);
        dockMetadataDto.setLastTimeUpdate(lastTimeUpdate);
        return dockMetadataDto;
    }

    private DockMetadataDtoBuilder copyWith(Consumer<DockMetadataDtoBuilder> consumer) {
        DockMetadataDtoBuilder dockMetadataDtoBuilder = new DockMetadataDtoBuilder(this);
        consumer.accept(dockMetadataDtoBuilder);
        return dockMetadataDtoBuilder;
    }

}
