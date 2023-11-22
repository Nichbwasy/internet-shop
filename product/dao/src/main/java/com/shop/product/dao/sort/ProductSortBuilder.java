package com.shop.product.dao.sort;

import com.shop.common.utils.all.consts.SortDirection;
import com.shop.product.model.Product_;
import org.springframework.data.domain.Sort;

public class ProductSortBuilder {

    private Sort sort;

    public ProductSortBuilder byName(SortDirection sortDirection) {
        if (sortDirection == SortDirection.NONE) return this;
        Sort.Direction direction = resolveDirection(sortDirection);
        sort = (sort == null) ? Sort.by(direction, Product_.NAME) : sort.and(Sort.by(direction, Product_.NAME));
        return this;
    }

    public ProductSortBuilder byPrice(SortDirection sortDirection) {
        if (sortDirection == SortDirection.NONE) return this;
        Sort.Direction direction = resolveDirection(sortDirection);
        sort = (sort == null) ? Sort.by(direction, Product_.PRICE) : sort.and(Sort.by(direction, Product_.PRICE));
        return this;
    }

    public ProductSortBuilder byCreationTime(SortDirection sortDirection) {
        if (sortDirection == SortDirection.NONE) return this;
        Sort.Direction direction = resolveDirection(sortDirection);
        sort = (sort == null) ? Sort.by(direction, Product_.CREATED_TIME) : sort.and(Sort.by(direction, Product_.CREATED_TIME));
        return this;
    }

    private Sort.Direction resolveDirection(SortDirection sortDirection) {
        Sort.Direction direction;
        switch (sortDirection) {
            case ASCENDING -> direction = Sort.Direction.ASC;
            case DESCENDING -> direction = Sort.Direction.DESC;
            default -> direction = Sort.DEFAULT_DIRECTION;
        }
        return direction;
    }

    public Sort build() {
        if (sort == null) sort = Sort.by(Sort.DEFAULT_DIRECTION, Product_.ID);
        return sort;
    }
}
