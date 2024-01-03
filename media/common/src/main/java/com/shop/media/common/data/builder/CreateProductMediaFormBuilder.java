package com.shop.media.common.data.builder;

import com.shop.common.utils.all.test.data.builder.TestDataBuilder;
import com.shop.media.dto.form.CreateProductMediaForm;
import org.springframework.web.multipart.MultipartFile;

import java.util.function.Consumer;

public class CreateProductMediaFormBuilder extends TestDataBuilder<CreateProductMediaForm> {

    private Long productId = random.nextLong(1, 1000);
    private MultipartFile multipartFile;

    private CreateProductMediaFormBuilder() {}

    private CreateProductMediaFormBuilder(CreateProductMediaFormBuilder builder) {
        this.productId = builder.productId;
        this.multipartFile = builder.multipartFile;
    }

    public static CreateProductMediaFormBuilder createProductMediaForm() {
        return new CreateProductMediaFormBuilder();
    }

    public CreateProductMediaFormBuilder productId(Long productId) {
        return copyWith(b -> b.productId = productId);
    }

    public CreateProductMediaFormBuilder multipartFile(MultipartFile multipartFile) {
        return copyWith(b -> b.multipartFile = multipartFile);
    }

    @Override
    public CreateProductMediaForm build() {
        CreateProductMediaForm form = new CreateProductMediaForm();
        form.setProductId(productId);
        form.setMultipartFile(multipartFile);
        return form;
    }

    private CreateProductMediaFormBuilder copyWith(Consumer<CreateProductMediaFormBuilder> consumer) {
        CreateProductMediaFormBuilder createProductMediaFormBuilder = new CreateProductMediaFormBuilder(this);
        consumer.accept(createProductMediaFormBuilder);
        return createProductMediaFormBuilder;
    }

}
