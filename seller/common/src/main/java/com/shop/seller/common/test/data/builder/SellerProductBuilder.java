package com.shop.seller.common.test.data.builder;

import com.shop.common.utils.all.test.data.builder.TestDataBuilder;
import com.shop.seller.model.SellerProduct;

import java.util.function.Consumer;

public class SellerProductBuilder extends TestDataBuilder<SellerProduct> {

    private Long id = random.nextLong(1, 1000);
    private Long productId = random.nextLong(1, 1000);

    private SellerProductBuilder () {}
    private SellerProductBuilder(SellerProductBuilder builder) {
        this.id = builder.id;
        this.productId = builder.productId;
    }

    public static SellerProductBuilder sellerProduct() {
        return new SellerProductBuilder();
    }

    public SellerProductBuilder id(Long id) {
        return copyWith(b -> b.id = id);
    }

    public SellerProductBuilder productId(Long productId) {
        return copyWith(b -> b.productId = productId);
    }

    @Override
    public SellerProduct build() {
        SellerProduct sellerProduct = new SellerProduct();
        sellerProduct.setId(id);
        sellerProduct.setProductId(productId);
        return sellerProduct;
    }

    private SellerProductBuilder copyWith(Consumer<SellerProductBuilder> consumer) {
        SellerProductBuilder sellerProductBuilder = new SellerProductBuilder(this);
        consumer.accept(sellerProductBuilder);
        return sellerProductBuilder;
    }
}
