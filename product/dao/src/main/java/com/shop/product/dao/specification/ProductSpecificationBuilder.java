package com.shop.product.dao.specification;

import com.shop.product.model.Product;
import com.shop.product.model.Product_;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductSpecificationBuilder {

    private Specification<Product> specification;

    public ProductSpecificationBuilder() {
        this.specification = Specification.where(null);
    }

    public ProductSpecificationBuilder andLikeName(String name) {
        if (!name.isBlank()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get(Product_.name), "%" + name + "%"));
        }
        return this;
    }

    public ProductSpecificationBuilder andBetweenCreationTime(LocalDateTime minTime, LocalDateTime maxTime) {
        if ((minTime != null && maxTime != null) && minTime.isBefore(maxTime)) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.between(root.get(Product_.createdTime), minTime, maxTime));
        }
        return this;
    }

    public ProductSpecificationBuilder andBetweenPrice(BigDecimal minPrice, BigDecimal maxPrice) {
        if ((minPrice != null && maxPrice != null) && minPrice.compareTo(maxPrice) < 0) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.between(root.get(Product_.price), minPrice, maxPrice));
        }
        return this;
    }

    public Specification<Product> build() {
        return specification;
    }

}
