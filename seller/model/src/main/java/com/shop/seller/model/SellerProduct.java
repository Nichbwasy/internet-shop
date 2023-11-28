package com.shop.seller.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "seller_product")
public class SellerProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Seller's product id is mandatory!")
    @Min(value = 1, message = "Seller's product id can't be lesser than 1!")
    @Column(name = "product_id", nullable = false, unique = true)
    private Long productId;

}
