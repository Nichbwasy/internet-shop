package com.shop.media.common.data.builder;

import com.shop.common.utils.all.generator.StringGenerator;
import com.shop.common.utils.all.test.data.builder.TestDataBuilder;
import com.shop.media.dto.form.UploadFileForm;
import org.springframework.web.multipart.MultipartFile;

import java.util.function.Consumer;

public class UploadFileFormBuilder extends TestDataBuilder<UploadFileForm> {

    private String fileName = StringGenerator.generate(random.nextInt(8, 16));
    private String bucketName = StringGenerator.generate(random.nextInt(4, 12));
    private MultipartFile multipartFile;

    private UploadFileFormBuilder() {}

    private UploadFileFormBuilder(UploadFileFormBuilder builder) {
        this.fileName = builder.fileName;
        this.bucketName = builder.bucketName;
        this.multipartFile = builder.multipartFile;
    }

    public static UploadFileFormBuilder uploadFileForm() {
        return new UploadFileFormBuilder();
    }

    public UploadFileFormBuilder fileName(String fileName) {
        return copyWith(b -> b.fileName = fileName);
    }

    public UploadFileFormBuilder bucketName(String bucketName) {
        return copyWith(b -> b.bucketName = bucketName);
    }

    public UploadFileFormBuilder multipartFile(MultipartFile multipartFile) {
        return copyWith(b -> b.multipartFile = multipartFile);
    }

    @Override
    public UploadFileForm build() {
        UploadFileForm form = new UploadFileForm();
        form.setFileName(fileName);
        form.setBucketName(bucketName);
        form.setMultipartFile(multipartFile);
        return form;
    }

    private UploadFileFormBuilder copyWith(Consumer<UploadFileFormBuilder> consumer) {
        UploadFileFormBuilder uploadFileFormBuilder = new UploadFileFormBuilder(this);
        consumer.accept(uploadFileFormBuilder);
        return uploadFileFormBuilder;
    }
}
