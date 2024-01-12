package com.shop.seller.common.test.data.builder;

import com.shop.common.utils.all.generator.StringGenerator;
import com.shop.common.utils.all.test.data.builder.CommonObjectBuilder;
import com.shop.seller.model.SellerInfo;
import com.shop.seller.model.SellerProduct;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SellerInfoBuilder extends CommonObjectBuilder<SellerInfo> {
    private Long id = random.nextLong(1, 1000);
    private Long userId = random.nextLong(1, 1000);
    private LocalDateTime registrationDate = LocalDateTime.now().minusHours(random.nextInt(1, 1000));
    private Float rating = random.nextFloat(0, 10);
    private String description = StringGenerator.generate(random.nextInt(24, 48));
    private List<SellerProduct> products = new ArrayList<>();

    private SellerInfoBuilder() {}

    private SellerInfoBuilder(SellerInfoBuilder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.registrationDate = builder.registrationDate;
        this.rating = builder.rating;
        this.description = builder.description;
        this.products = builder.products;
    }

    public static SellerInfoBuilder sellerInfo() {
        return new SellerInfoBuilder();
    }

    public SellerInfoBuilder id(Long id) {
        return copyWith(b -> b.id = id);
    }

    public SellerInfoBuilder userId(Long userId) {
        return copyWith(b -> b.userId = userId);
    }

    public SellerInfoBuilder registrationDate(LocalDateTime registrationDate) {
        return copyWith(b -> b.registrationDate = registrationDate);
    }

    public SellerInfoBuilder rating(Float rating) {
        return copyWith(b -> b.rating = rating);
    }

    public SellerInfoBuilder description(String description) {
        return copyWith(b -> b.description = description);
    }

    public SellerInfoBuilder products(List<SellerProduct> products) {
        return copyWith(b -> b.products = products);
    }

    @Override
    public SellerInfo build() {
        SellerInfo sellerInfo = new SellerInfo();
        sellerInfo.setId(id);
        sellerInfo.setUserId(userId);
        sellerInfo.setRating(rating);
        sellerInfo.setProducts(products);
        sellerInfo.setDescription(description);
        sellerInfo.setRegistrationDate(registrationDate);
        return sellerInfo;
    }

    private SellerInfoBuilder copyWith(Consumer<SellerInfoBuilder> consumer) {
        SellerInfoBuilder sellerInfoBuilder = new SellerInfoBuilder(this);
        consumer.accept(sellerInfoBuilder);
        return sellerInfoBuilder;
    }
}
