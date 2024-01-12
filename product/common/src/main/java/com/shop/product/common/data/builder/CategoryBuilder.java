package com.shop.product.common.data.builder;

import com.shop.common.utils.all.generator.StringGenerator;
import com.shop.common.utils.all.test.data.builder.CommonObjectBuilder;
import com.shop.product.model.Category;
import com.shop.product.model.SubCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CategoryBuilder extends CommonObjectBuilder<Category> {

    private Long id = random.nextLong(1, 1000);
    private String name = StringGenerator.generate(random.nextInt(8, 12));
    private List<SubCategory> subCategories = new ArrayList<>();

    private CategoryBuilder() {}

    private CategoryBuilder(CategoryBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.subCategories = builder.subCategories;
    }

    public static CategoryBuilder category() {
        return new CategoryBuilder();
    }

    public CategoryBuilder id(Long id) {
        return copyWith(b -> b.id = id);
    }

    public CategoryBuilder name(String name) {
        return copyWith(b -> b.name = name);
    }

    public CategoryBuilder subCategories(List<SubCategory> subCategories) {
        return copyWith(b -> b.subCategories = subCategories);
    }

    @Override
    public Category build() {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setSubCategories(subCategories);
        return category;
    }

    private CategoryBuilder copyWith(Consumer<CategoryBuilder> consumer) {
        CategoryBuilder categoryBuilder = new CategoryBuilder(this);
        consumer.accept(categoryBuilder);
        return categoryBuilder;
    }

}
