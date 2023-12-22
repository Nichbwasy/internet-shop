package com.shop.media.common.data.builder;

import com.shop.common.utils.all.generator.StringGenerator;
import com.shop.common.utils.all.test.data.builder.TestDataBuilder;
import com.shop.media.dto.form.GetFileForm;

import java.util.function.Consumer;

public class GetFileFormBuilder extends TestDataBuilder<GetFileForm> {

    private String fileName = StringGenerator.generate(random.nextInt(4, 8));
    private String bucketName = StringGenerator.generate(random.nextInt(4, 8));

    private GetFileFormBuilder() {}
    private GetFileFormBuilder(GetFileFormBuilder builder) {
        this.fileName = builder.fileName;
        this.bucketName = builder.bucketName;
    }

    public static GetFileFormBuilder getFileForm() {
        return new GetFileFormBuilder();
    }

    public GetFileFormBuilder fileName(String fileName) {
        return copyWith(b -> b.fileName = fileName);
    }

    public GetFileFormBuilder bucketName(String bucketName) {
        return copyWith(b -> b.bucketName = bucketName);
    }

    @Override
    public GetFileForm build() {
        GetFileForm form = new GetFileForm();
        form.setFileName(fileName);
        form.setBucketName(bucketName);
        return form;
    }

    private GetFileFormBuilder copyWith(Consumer<GetFileFormBuilder> consumer) {
        GetFileFormBuilder getFileFormBuilder = new GetFileFormBuilder(this);
        consumer.accept(getFileFormBuilder);
        return getFileFormBuilder;
    }
}
