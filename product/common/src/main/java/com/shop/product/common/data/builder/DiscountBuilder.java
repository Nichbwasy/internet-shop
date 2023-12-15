package com.shop.product.common.data.builder;

import com.shop.common.utils.all.generator.StringGenerator;
import com.shop.common.utils.all.test.data.builder.TestDataBuilder;
import com.shop.product.model.Discount;

import java.time.LocalDateTime;
import java.util.function.Consumer;

public class DiscountBuilder extends TestDataBuilder<Discount> {

    private Long id = random.nextLong(1, 1000);
    private String name = StringGenerator.generate(random.nextInt(12, 24));
    private String description = StringGenerator.generate(random.nextInt(24, 48));
    private LocalDateTime createdTime = LocalDateTime.now().minusMinutes(random.nextInt(800, 1000));
    private LocalDateTime activationTime = LocalDateTime.now().minusMinutes(random.nextInt(200, 800));
    private LocalDateTime endingTime = LocalDateTime.now().plusMinutes(random.nextInt(-200, 200));
    private Float discountValue = random.nextFloat();

    private DiscountBuilder() {}

    private DiscountBuilder(DiscountBuilder discountBuilder) {
        this.id = discountBuilder.id;
        this.name = discountBuilder.name;
        this.description = discountBuilder.description;
        this.createdTime = discountBuilder.createdTime;
        this.activationTime = discountBuilder.activationTime;
        this.endingTime = discountBuilder.endingTime;
        this.discountValue = discountBuilder.discountValue;
    }

    public static DiscountBuilder discount() {
        return new DiscountBuilder();
    }

    public DiscountBuilder id(Long id) {
        return copyWith(b -> b.id = id);
    }

    public DiscountBuilder name(String name) {
        return copyWith(b -> b.name = name);
    }

    public DiscountBuilder description(String description) {
        return copyWith(b -> b.description = description);
    }

    public DiscountBuilder createdTime(LocalDateTime createdTime) {
        return copyWith(b -> b.createdTime = createdTime);
    }

    public DiscountBuilder activationTime(LocalDateTime activationTime) {
        return copyWith(b -> b.activationTime = activationTime);
    }

    public DiscountBuilder endingTime(LocalDateTime endingTime) {
        return copyWith(b -> b.endingTime = endingTime);
    }

    public DiscountBuilder discountValue(Float discountValue) {
        return copyWith(b -> b.discountValue = discountValue);
    }


    @Override
    public Discount build() {
        Discount discount = new Discount();
        discount.setId(id);
        discount.setName(name);
        discount.setDescription(description);
        discount.setCreatedTime(createdTime);
        discount.setActivationTime(activationTime);
        discount.setEndingTime(endingTime);
        discount.setDiscountValue(discountValue);
        return discount;
    }

    private DiscountBuilder copyWith(Consumer<DiscountBuilder> consumer) {
        DiscountBuilder discountBuilder = new DiscountBuilder(this);
        consumer.accept(discountBuilder);
        return discountBuilder;
    }
}
