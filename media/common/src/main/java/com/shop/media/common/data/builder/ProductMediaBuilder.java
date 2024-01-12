package com.shop.media.common.data.builder;

import com.shop.common.utils.all.test.data.builder.CommonObjectBuilder;
import com.shop.media.model.MediaElement;
import com.shop.media.model.ProductMedia;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ProductMediaBuilder extends CommonObjectBuilder<ProductMedia> {

    private Long id = random.nextLong(1, 1000);
    private Long productId = random.nextLong(1, 1000);
    private List<MediaElement> mediaElements = new ArrayList<>();

    private ProductMediaBuilder() {}

    private ProductMediaBuilder(ProductMediaBuilder builder) {
        this.id = builder.id;
        this.productId = builder.productId;
        this.mediaElements = builder.mediaElements;
    }

    public ProductMediaBuilder id(Long id) {
        return copyWith(b -> b.id = id);
    }

    public ProductMediaBuilder productId(Long productId) {
        return copyWith(b -> b.productId = productId);
    }

    public ProductMediaBuilder mediaElements(List<MediaElement> mediaElements) {
        return copyWith(b -> b.mediaElements = mediaElements);
    }

    public static ProductMediaBuilder productMedia() {
        return new ProductMediaBuilder();
    }

    @Override
    public ProductMedia build() {
        ProductMedia productMedia = new ProductMedia();
        productMedia.setId(id);
        productMedia.setProductId(productId);
        productMedia.setMediaElements(mediaElements);
        return productMedia;
    }

    private ProductMediaBuilder copyWith(Consumer<ProductMediaBuilder> consumer) {
        ProductMediaBuilder productMediaBuilder = new ProductMediaBuilder(this);
        consumer.accept(productMediaBuilder);
        return productMediaBuilder;
    }

}
