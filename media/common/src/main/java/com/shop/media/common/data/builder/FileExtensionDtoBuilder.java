package com.shop.media.common.data.builder;

import com.shop.common.utils.all.generator.StringGenerator;
import com.shop.common.utils.all.test.data.builder.CommonObjectBuilder;
import com.shop.media.dto.FileExtensionDto;

import java.util.function.Consumer;

public class FileExtensionDtoBuilder extends CommonObjectBuilder<FileExtensionDto> {

    private Long id = random.nextLong(1, 1000);
    private String name = StringGenerator.generate(random.nextInt(3, 6));
    private String mediaTypeName = StringGenerator.generate(random.nextInt(12, 24));

    private FileExtensionDtoBuilder() {}

    private FileExtensionDtoBuilder(FileExtensionDtoBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.mediaTypeName = builder.mediaTypeName;
    }

    public static FileExtensionDtoBuilder fileExtensionDto() {
        return new FileExtensionDtoBuilder();
    }

    public FileExtensionDtoBuilder id(Long id) {
        return copyWith(b -> b.id = id);
    }

    public FileExtensionDtoBuilder name(String name) {
        return copyWith(b -> b.name = name);
    }

    public FileExtensionDtoBuilder mediaTypeName(String name) {
        return copyWith(b -> b.mediaTypeName = mediaTypeName);
    }

    @Override
    public FileExtensionDto build() {
        FileExtensionDto fileExtensionDto = new FileExtensionDto();
        fileExtensionDto.setId(id);
        fileExtensionDto.setName(name);
        fileExtensionDto.setMediaTypeName(mediaTypeName);
        return fileExtensionDto;
    }

    private FileExtensionDtoBuilder copyWith(Consumer<FileExtensionDtoBuilder> consumer) {
        FileExtensionDtoBuilder fileExtensionDtoBuilder = new FileExtensionDtoBuilder(this);
        consumer.accept(fileExtensionDtoBuilder);
        return fileExtensionDtoBuilder;
    }

}
