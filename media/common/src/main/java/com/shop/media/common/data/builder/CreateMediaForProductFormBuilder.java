package com.shop.media.common.data.builder;

import com.shop.common.utils.all.test.data.builder.CommonObjectBuilder;
import com.shop.media.dto.form.CreateMediaForProductForm;

import java.util.function.Consumer;

public class CreateMediaForProductFormBuilder extends CommonObjectBuilder<CreateMediaForProductForm> {

    private Long productId = random.nextLong(1, 1000);

    private CreateMediaForProductFormBuilder() {}

    private CreateMediaForProductFormBuilder(CreateMediaForProductFormBuilder builder) {
        this.productId = builder.productId;
    }
    public static CreateMediaForProductFormBuilder createMediaForProductForm() {
        return new CreateMediaForProductFormBuilder();
    }

    public CreateMediaForProductFormBuilder productId(Long productId) {
        return copyWith(b -> b.productId = productId);
    }

    @Override
    public CreateMediaForProductForm build() {
        CreateMediaForProductForm createMediaForProductForm = new CreateMediaForProductForm();
        createMediaForProductForm.setProductId(productId);
        return createMediaForProductForm;
    }

    private CreateMediaForProductFormBuilder copyWith(Consumer<CreateMediaForProductFormBuilder> consumer) {
        CreateMediaForProductFormBuilder createMediaForProductFormBuilder = new CreateMediaForProductFormBuilder(this);
        consumer.accept(createMediaForProductFormBuilder);
        return createMediaForProductFormBuilder;
    }
}
