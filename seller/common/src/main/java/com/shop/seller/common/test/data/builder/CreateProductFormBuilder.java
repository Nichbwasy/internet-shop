package com.shop.seller.common.test.data.builder;

import com.shop.common.utils.all.generator.StringGenerator;
import com.shop.common.utils.all.test.data.builder.TestDataBuilder;
import com.shop.seller.dto.control.CreateProductForm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CreateProductFormBuilder extends TestDataBuilder<CreateProductForm> {

    private String name = StringGenerator.generate(random.nextInt(8, 12));
    private String description = StringGenerator.generate(random.nextInt(24, 48));
    private Integer count = random.nextInt(1, 100);
    private BigDecimal price = BigDecimal.valueOf(random.nextInt(1, 1000));
    private List<Long> categoryIds = new ArrayList<>();
    private List<Long> discountIds = new ArrayList<>();

    private CreateProductFormBuilder() {}

    private CreateProductFormBuilder(CreateProductFormBuilder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.count = builder.count;
        this.price = builder.price;
        this.categoryIds = builder.categoryIds;
        this.discountIds = builder.discountIds;
    }

    public static CreateProductFormBuilder createProductForm() {
        return new CreateProductFormBuilder();
    }

    public CreateProductFormBuilder name(String name) {
        return copyWith(b -> b.name = name);
    }

    public CreateProductFormBuilder count(Integer count) {
        return copyWith(b -> b.count = count);
    }

    public CreateProductFormBuilder price(BigDecimal bigDecimal) {
        return copyWith(b -> b.price = price);
    }

    public CreateProductFormBuilder description(String description) {
        return copyWith(b -> b.description = description);
    }

    public CreateProductFormBuilder categoryIds(List<Long> categoryIds) {
        return copyWith(b -> b.categoryIds = categoryIds);
    }

    public CreateProductFormBuilder discountIds(List<Long> discountIds) {
        return copyWith(d -> d.discountIds = discountIds);
    }

    @Override
    public CreateProductForm build() {
        CreateProductForm form = new CreateProductForm();
        form.setName(name);
        form.setCount(count);
        form.setPrice(price);
        form.setDescription(description);
        form.setCategoryIds(categoryIds);
        form.setDiscountIds(discountIds);
        return form;
    }

    private CreateProductFormBuilder copyWith(Consumer<CreateProductFormBuilder> consumer) {
        CreateProductFormBuilder createProductFormBuilder = new CreateProductFormBuilder(this);
        consumer.accept(createProductFormBuilder);
        return createProductFormBuilder;
    }

}
