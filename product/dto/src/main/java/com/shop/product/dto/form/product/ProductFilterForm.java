package com.shop.product.dto.form.product;

import com.shop.common.utils.all.consts.SortDirection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterForm {

        private String name;
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
        private LocalDateTime minCreatedTime;
        private LocalDateTime maxCreatedTime;

        private SortDirection sortByName;
        private SortDirection sortByPrice;
        private SortDirection sortByCreatedTime;

}
