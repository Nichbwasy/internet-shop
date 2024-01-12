package com.shop.media.common.data.builder;

import com.shop.common.utils.all.test.data.builder.CommonObjectBuilder;
import com.shop.media.dto.MediaElementDto;
import com.shop.media.dto.ProductMediaDto;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ProductMediaDtoBuilder extends CommonObjectBuilder<ProductMediaDto> {

    private Long id = random.nextLong(1, 1000);
    private Long productId = random.nextLong(1, 1000);
    private List<MediaElementDto> mediaElements = new ArrayList<>();

    private ProductMediaDtoBuilder() {}

    private ProductMediaDtoBuilder(ProductMediaDtoBuilder builder) {
        this.id = builder.id;
        this.productId = builder.productId;
        this.mediaElements = builder.mediaElements;
    }

    public static ProductMediaDtoBuilder productMediaDto() {
        return new ProductMediaDtoBuilder();
    }

    public ProductMediaDtoBuilder id(Long id) {
        return copyWith(b -> b.id = id);
    }

    public ProductMediaDtoBuilder productId(Long productId) {
        return copyWith(b -> b.productId = productId);
    }

    public ProductMediaDtoBuilder mediaElements(List<MediaElementDto> mediaElements) {
        return copyWith(b -> b.mediaElements = mediaElements);
    }

    @Override
    public ProductMediaDto build() {
        ProductMediaDto productMediaDto = new ProductMediaDto();
        productMediaDto.setId(id);
        productMediaDto.setProductId(productId);
        productMediaDto.setMediaElements(mediaElements);
        return productMediaDto;
    }

    private ProductMediaDtoBuilder copyWith(Consumer<ProductMediaDtoBuilder> consumer) {
        ProductMediaDtoBuilder productMediaDtoBuilder = new ProductMediaDtoBuilder(this);
        consumer.accept(productMediaDtoBuilder);
        return productMediaDtoBuilder;
    }
}
