package com.shop.order.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "order_unit")
public class OrderUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Product id in order unit is mandatory!")
    @Min(value = 1, message = "Product id of order unit can't be lesser than 1!")
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NotNull(message = "Price for order unit is mandatory!")
    @Min(value = 0, message = "Price for the order unit can't be negative!")
    @Column(name = "price", nullable = false, columnDefinition = "0.0")
    private BigDecimal price;

    @NotNull(message = "Products count for order unit is mandatory!")
    @Min(value = 0, message = "Products count in order element cant be negative!")
    @Column(name = "count", nullable = false)
    private Integer count;

}
