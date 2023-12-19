package com.shop.seller.common.test.data.builder;

import com.shop.common.utils.all.generator.StringGenerator;
import com.shop.common.utils.all.test.data.builder.TestDataBuilder;
import com.shop.seller.dto.control.UpdateSellerProductForm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UpdateSellerProductFormBuilder extends TestDataBuilder<UpdateSellerProductForm> {

    private Long sellerProductId = random.nextLong(1, 1000);
    private String name = StringGenerator.generate(random.nextInt(8, 12));
    private String description = StringGenerator.generate(random.nextInt(24, 48));
    private Integer count = random.nextInt(1, 100);
    private BigDecimal price = BigDecimal.valueOf(random.nextDouble(1, 1000));
    private List<Long> categoryIds = new ArrayList<>();
    private List<Long> discountIds = new ArrayList<>();

    private UpdateSellerProductFormBuilder() {}

    private UpdateSellerProductFormBuilder(UpdateSellerProductFormBuilder builder) {
        this.sellerProductId = builder.sellerProductId;
        this.name = builder.name;
        this.description = builder.description;
        this.count = builder.count;
        this.price = builder.price;
        this.categoryIds = builder.categoryIds;
        this.discountIds = builder.discountIds;
    }

    public static UpdateSellerProductFormBuilder updateSellerProductForm() {
        return new UpdateSellerProductFormBuilder();
    }

    public UpdateSellerProductFormBuilder sellerProductId(Long sellerProductId) {
        return copyWith(b -> b.sellerProductId = sellerProductId);
    }

    public UpdateSellerProductFormBuilder name(String name) {
        return copyWith(b -> b.name = name);
    }

    public UpdateSellerProductFormBuilder description(String description) {
        return copyWith(b -> b.description = description);
    }

    public UpdateSellerProductFormBuilder count(Integer count) {
        return copyWith(b -> b.count = count);
    }

    public UpdateSellerProductFormBuilder price(BigDecimal price) {
        return copyWith(b -> b.price = price);
    }

    public UpdateSellerProductFormBuilder categoryIds(List<Long> categoryIds) {
        return copyWith(b -> b.categoryIds = categoryIds);
    }

    public UpdateSellerProductFormBuilder discountIds(List<Long> discountIds) {
        return copyWith(b -> b.discountIds = discountIds);
    }

    @Override
    public UpdateSellerProductForm build() {
        UpdateSellerProductForm form = new UpdateSellerProductForm();
        form.setName(name);
        form.setCount(count);
        form.setPrice(price);
        form.setSellerProductId(sellerProductId);
        form.setDescription(description);
        form.setCategoryIds(categoryIds);
        form.setDiscountIds(discountIds);
        return form;
    }

    private UpdateSellerProductFormBuilder copyWith(Consumer<UpdateSellerProductFormBuilder> consumer) {
        UpdateSellerProductFormBuilder updateSellerProductFormBuilder = new UpdateSellerProductFormBuilder(this);
        consumer.accept(updateSellerProductFormBuilder);
        return updateSellerProductFormBuilder;
    }

}
