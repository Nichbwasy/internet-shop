package com.shop.media.common.data.builder;

import com.shop.common.utils.all.test.data.builder.CommonObjectBuilder;
import com.shop.media.dto.form.AddMediaToProductForm;
import org.springframework.web.multipart.MultipartFile;

import java.util.function.Consumer;

public class AddMediaToProductFormBuilder extends CommonObjectBuilder<AddMediaToProductForm> {

    private Long productMediaId = random.nextLong(1, 1000);
    private MultipartFile multipartFile;

    private AddMediaToProductFormBuilder() {}

    private AddMediaToProductFormBuilder(AddMediaToProductFormBuilder builder) {
        this.productMediaId = builder.productMediaId;
        this.multipartFile = builder.multipartFile;
    }

    public static AddMediaToProductFormBuilder createProductMediaForm() {
        return new AddMediaToProductFormBuilder();
    }

    public AddMediaToProductFormBuilder productId(Long productId) {
        return copyWith(b -> b.productMediaId = productId);
    }

    public AddMediaToProductFormBuilder multipartFile(MultipartFile multipartFile) {
        return copyWith(b -> b.multipartFile = multipartFile);
    }

    @Override
    public AddMediaToProductForm build() {
        AddMediaToProductForm form = new AddMediaToProductForm();
        form.setProductMediaId(productMediaId);
        form.setMultipartFile(multipartFile);
        return form;
    }

    private AddMediaToProductFormBuilder copyWith(Consumer<AddMediaToProductFormBuilder> consumer) {
        AddMediaToProductFormBuilder createProductMediaFormBuilder = new AddMediaToProductFormBuilder(this);
        consumer.accept(createProductMediaFormBuilder);
        return createProductMediaFormBuilder;
    }

}
