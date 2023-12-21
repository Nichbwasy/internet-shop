package com.shop.media.common.data.builder;

import com.shop.common.utils.all.generator.StringGenerator;
import com.shop.common.utils.all.test.data.builder.TestDataBuilder;
import com.shop.media.dto.RemoveFileForm;

import java.util.function.Consumer;

public class RemoveFileFormBuilder extends TestDataBuilder<RemoveFileForm> {

    private String fileName = StringGenerator.generate(random.nextInt(8, 12));
    private String bucketName = StringGenerator.generate(random.nextInt(4, 8));

    private RemoveFileFormBuilder(RemoveFileFormBuilder builder) {
        this.fileName = builder.fileName;
        this.bucketName = builder.bucketName;
    }

    private RemoveFileFormBuilder() {}

    public static RemoveFileFormBuilder removeFileForm() {
        return new RemoveFileFormBuilder();
    }

    public RemoveFileFormBuilder fileName(String fileName) {
        return copyWith(b -> b.fileName = fileName);
    }

    public RemoveFileFormBuilder bucketName(String bucketName) {
        return copyWith(b -> b.bucketName = bucketName);
    }

    @Override
    public RemoveFileForm build() {
        RemoveFileForm form = new RemoveFileForm();
        form.setFileName(fileName);
        form.setBucketName(bucketName);
        return form;
    }

    private RemoveFileFormBuilder copyWith(Consumer<RemoveFileFormBuilder> consumer) {
        RemoveFileFormBuilder removeFileFormBuilder = new RemoveFileFormBuilder(this);
        consumer.accept(removeFileFormBuilder);
        return removeFileFormBuilder;
    }

}
