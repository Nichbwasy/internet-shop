package com.shop.shop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "User's login in cart is mandatory!")
    @Size(min = 3, max = 64, message = "User's login in cart must contains from 3 to 64 characters!")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,64}$", message = "Invalid user's login  format!")
    @Column(name = "user_login", nullable = false, unique = true)
    private String userLogin;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "user_cart_cart_item",
            joinColumns = @JoinColumn(name = "user_cart_id"),
            inverseJoinColumns = @JoinColumn(name = "cart_item_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CartItem> cartItems;

}
