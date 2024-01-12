package com.shop.product.common.data.builder;

import com.shop.common.utils.all.consts.ApprovalStatuses;
import com.shop.common.utils.all.generator.StringGenerator;
import com.shop.common.utils.all.test.data.builder.CommonObjectBuilder;
import com.shop.product.dto.CategoryDto;
import com.shop.product.dto.DiscountDto;
import com.shop.product.dto.ProductDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ProductDtoBuilder extends CommonObjectBuilder<ProductDto> {

    private Long id = random.nextLong(1, 1000);
    private String code = StringGenerator.generate(64);
    private String name = StringGenerator.generate(random.nextInt(8, 12));
    private String description = StringGenerator.generate(random.nextInt(24, 48));
    private LocalDateTime createdTime = LocalDateTime.now().minusHours(random.nextInt(1, 1000));
    private Integer count = random.nextInt(1, 100);
    private BigDecimal price = BigDecimal.valueOf(random.nextDouble(1, 1000));
    private String approvalStatus = ApprovalStatuses.NONE;
    private Long mediaId = random.nextLong(1, 1000);
    private List<CategoryDto> categories = new ArrayList<>();
    private List<DiscountDto> discounts = new ArrayList<>();

    private ProductDtoBuilder() {}
    private ProductDtoBuilder(ProductDtoBuilder builder) {
        this.id = builder.id;
        this.code = builder.code;
        this.name = builder.name;
        this.description = builder.description;
        this.createdTime = builder.createdTime;
        this.count = builder.count;
        this.price = builder.price;
        this.approvalStatus = builder.approvalStatus;
        this.mediaId = builder.mediaId;
        this.categories = builder.categories;
        this.discounts = builder.discounts;
    }

    public static ProductDtoBuilder productDto() {
        return new ProductDtoBuilder();
    }

    public ProductDtoBuilder id(Long id) {
        return copyWith(b -> b.id = id);
    }

    public ProductDtoBuilder name(String name) {
        return copyWith(b -> b.name = name);
    }

    public ProductDtoBuilder code(String code) {
        return copyWith(b -> b.code = code);
    }

    public ProductDtoBuilder description(String description) {
        return copyWith(b -> b.description = description);
    }

    public ProductDtoBuilder createdTime(LocalDateTime createdTime) {
        return copyWith(b -> b.createdTime = createdTime);
    }

    public ProductDtoBuilder count(Integer count) {
        return copyWith(b -> b.count = count);
    }

    public ProductDtoBuilder price(BigDecimal price) {
        return copyWith(b -> b.price = price);
    }

    public ProductDtoBuilder approvalStatus(String approvalStatus) {
        return copyWith(b -> b.approvalStatus = approvalStatus);
    }

    public ProductDtoBuilder mediaId(Long mediaId) {
        return copyWith(b -> b.mediaId = mediaId);
    }

    public ProductDtoBuilder categories(List<CategoryDto> categories) {
        return copyWith(b -> b.categories = categories);
    }

    public ProductDtoBuilder discounts(List<DiscountDto> discounts) {
        return copyWith(b -> b.discounts = discounts);
    }

    @Override
    public ProductDto build() {
        ProductDto productDto = new ProductDto();
        productDto.setId(id);
        productDto.setCode(code);
        productDto.setName(name);
        productDto.setCount(count);
        productDto.setPrice(price);
        productDto.setCreatedTime(createdTime);
        productDto.setCategories(categories);
        productDto.setDiscounts(discounts);
        productDto.setApprovalStatus(approvalStatus);
        return productDto;
    }

    private ProductDtoBuilder copyWith(Consumer<ProductDtoBuilder> consumer) {
        ProductDtoBuilder productDtoBuilder = new ProductDtoBuilder(this);
        consumer.accept(productDtoBuilder);
        return productDtoBuilder;
    }
}
