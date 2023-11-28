package com.shop.seller.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "seller_info")
public class SellerInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Seller's user id is mandatory!")
    @Min(value = 1, message = "Seller's id can't be lesser than 1!")
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @NotNull(message = "Seller's registration date is mandatory!")
    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    @NotNull(message = "Seller's rating is mandatory")
    @DecimalMin(value = "0", message = "Seller's rating can't be lesser than 0!")
    @DecimalMax(value = "10", message = "Seller's rating can't be more than 10!")
    @Column(name = "rating", nullable = false, columnDefinition = "5.0")
    private Float rating;

    @Size(max = 4092, message = "Seller's description can't be larger than 4092 symbols!")
    @Column(name = "description", length = 4092)
    private String description;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinTable(name = "seller_info_seller_product",
            joinColumns = @JoinColumn(name = "seller_info_id"),
            inverseJoinColumns = @JoinColumn(name = "seller_product_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<SellerProduct> products;

}
