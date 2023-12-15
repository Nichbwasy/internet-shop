package com.shop.product.common.data.builder;

import com.shop.common.utils.all.generator.StringGenerator;
import com.shop.common.utils.all.test.data.builder.TestDataBuilder;
import com.shop.product.dto.CategoryDto;
import com.shop.product.dto.SubCategoryDto;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CategoryDtoBuilder extends TestDataBuilder<CategoryDto> {

    private Long id = random.nextLong(1, 1000);
    private String name = StringGenerator.generate(random.nextInt(8, 12));
    private List<SubCategoryDto> subCategories = new ArrayList<>();

    private CategoryDtoBuilder() {}

    private CategoryDtoBuilder(CategoryDtoBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.subCategories = builder.subCategories;
    }

    public static CategoryDtoBuilder categoryDto() {
        return new CategoryDtoBuilder();
    }

    public CategoryDtoBuilder id(Long id) {
        return copyWith(b -> b.id = id);
    }

    public CategoryDtoBuilder name(String name) {
        return copyWith(b -> b.name = name);
    }

    public CategoryDtoBuilder subCategories(List<SubCategoryDto> subCategories) {
        return copyWith(b -> b.subCategories = subCategories);
    }

    @Override
    public CategoryDto build() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(id);
        categoryDto.setName(name);
        categoryDto.setSubCategories(subCategories);
        return categoryDto;
    }

    private CategoryDtoBuilder copyWith(Consumer<CategoryDtoBuilder> consumer) {
        CategoryDtoBuilder categoryDtoBuilder = new CategoryDtoBuilder(this);
        consumer.accept(categoryDtoBuilder);
        return categoryDtoBuilder;
    }
}
