package com.shop.media.common.data.builder;

import com.shop.common.utils.all.generator.StringGenerator;
import com.shop.common.utils.all.test.data.builder.CommonObjectBuilder;
import com.shop.media.model.FileExtension;

import java.util.function.Consumer;

public class FileExtensionBuilder extends CommonObjectBuilder<FileExtension> {

    private Long id = random.nextLong(1, 1000);
    private String name = StringGenerator.generate(random.nextInt(3, 6));
    private String mediaTypeName = StringGenerator.generate(random.nextInt(12, 24));

    private FileExtensionBuilder() {}

    private FileExtensionBuilder(FileExtensionBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.mediaTypeName = builder.mediaTypeName;
    }

    public FileExtensionBuilder id(Long id) {
        return copyWith(b -> b.id = id);
    }

    public FileExtensionBuilder name(String name) {
        return copyWith(b -> b.name = name);
    }

    public FileExtensionBuilder mediaTypeName(String mediaTypeName) {
        return copyWith(b -> b.mediaTypeName = mediaTypeName);
    }

    public static FileExtensionBuilder fileExtension() {
        return new FileExtensionBuilder();
    }

    @Override
    public FileExtension build() {
        FileExtension fileExtension = new FileExtension();
        fileExtension.setId(id);
        fileExtension.setName(name);
        fileExtension.setMediaTypeName(mediaTypeName);
        return fileExtension;
    }

    private FileExtensionBuilder copyWith(Consumer<FileExtensionBuilder> consumer) {
        FileExtensionBuilder fileExtensionBuilder = new FileExtensionBuilder(this);
        consumer.accept(fileExtensionBuilder);
        return fileExtensionBuilder;
    }

}
