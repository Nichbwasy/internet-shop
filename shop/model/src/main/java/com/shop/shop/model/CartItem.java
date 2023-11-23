package com.shop.shop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "cart_item")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_cart_id", nullable=false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserCart userCart;

    @NotNull(message = "Cart's item product id is mandatory!")
    @Min(value = 1, message = "Cart's item product can't be lesser than 1!")
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NotNull(message = "Cart's item addition time is mandatory!")
    @Column(name = "addition_time", nullable = false)
    private LocalDateTime additionTime;

    @NotNull(message = "Cart's item count is mandatory!")
    @Min(value = 1, message = "Cart's item count can't be lesser than 1!")
    @Column(name = "count", nullable = false, columnDefinition = "1")
    private Integer count;


}
