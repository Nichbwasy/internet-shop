package com.shop.shop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "user_cart")
public class UserCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User's cart user id is mandatory!")
    @Min(value = 1, message = "User's cart user id can't be lesser than 1!")
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "user_cart_shop_product",
            joinColumns = @JoinColumn(name = "user_cart_id"),
            inverseJoinColumns = @JoinColumn(name = "shop_product_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<ShopProduct> products;

}
