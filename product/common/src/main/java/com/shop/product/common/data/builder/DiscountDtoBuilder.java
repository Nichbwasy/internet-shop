package com.shop.product.common.data.builder;

import com.shop.common.utils.all.generator.StringGenerator;
import com.shop.common.utils.all.test.data.builder.CommonObjectBuilder;
import com.shop.product.dto.DiscountDto;

import java.time.LocalDateTime;
import java.util.function.Consumer;

public class DiscountDtoBuilder extends CommonObjectBuilder<DiscountDto> {

    private Long id = random.nextLong(1, 1000);
    private String name = StringGenerator.generate(random.nextInt(12, 24));
    private String description = StringGenerator.generate(random.nextInt(24, 48));
    private LocalDateTime createdTime = LocalDateTime.now().minusMinutes(random.nextInt(800, 1000));
    private LocalDateTime activationTime = LocalDateTime.now().minusMinutes(random.nextInt(200, 800));
    private LocalDateTime endingTime = LocalDateTime.now().plusMinutes(random.nextInt(-200, 200));
    private Float discountValue = random.nextFloat();

    private DiscountDtoBuilder() {}

    public DiscountDtoBuilder(DiscountDtoBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.createdTime = builder.createdTime;
        this.activationTime = builder.activationTime;
        this.endingTime = builder.endingTime;
        this.discountValue = builder.discountValue;
    }

    public static DiscountDtoBuilder discountDto() {
        return new DiscountDtoBuilder();
    }

    public DiscountDtoBuilder id(Long id) {
        return copyWith(b -> b.id = id);
    }

    public DiscountDtoBuilder name(String name) {
        return copyWith(b -> b.name = name);
    }

    public DiscountDtoBuilder description(String description) {
        return copyWith(b -> b.description = description);
    }

    public DiscountDtoBuilder createdTime(LocalDateTime createdTime) {
        return copyWith(b -> b.createdTime = createdTime);
    }

    public DiscountDtoBuilder activationTime(LocalDateTime activationTime) {
        return copyWith(b -> b.activationTime = activationTime);
    }

    public DiscountDtoBuilder endingTime(LocalDateTime endingTime) {
        return copyWith(b -> b.endingTime = endingTime);
    }

    public DiscountDtoBuilder discountValue(Float discountValue) {
        return copyWith(b -> b.discountValue = discountValue);
    }

    @Override
    public DiscountDto build() {
        DiscountDto discountDto = new DiscountDto();
        discountDto.setId(id);
        discountDto.setName(name);
        discountDto.setDescription(description);
        discountDto.setCreatedTime(createdTime);
        discountDto.setActivationTime(activationTime);
        discountDto.setEndingTime(endingTime);
        discountDto.setDiscountValue(discountValue);
        return discountDto;
    }

    private DiscountDtoBuilder copyWith(Consumer<DiscountDtoBuilder> consumer) {
        DiscountDtoBuilder discountDtoBuilder = new DiscountDtoBuilder(this);
        consumer.accept(discountDtoBuilder);
        return discountDtoBuilder;
    }
}
