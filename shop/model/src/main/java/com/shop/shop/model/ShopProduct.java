package com.shop.shop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "shop_product")
public class ShopProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull(message = "Shop's product code is mandatory!")
    @Size(min = 64, max = 64, message = "Shop's product code must contains 64 characters!")
    @Column(name = "code", length = 64, nullable = false, unique = true)
    private String code;

    @NotNull(message = "Shop's product id is mandatory!")
    @Min(value = 1, message = "Shop's product id can't be lesser than 1!")
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NotNull(message = "Shop's product seller id is mandatory!")
    @Min(value = 1, message = "Shop's product seller id can't be lesser than 1!")
    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @NotNull(message = "Shop's product creation time is mandatory!")
    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @NotNull(message = "Shop's product approval status is mandatory!")
    @Size(min = 3, max = 64, message = "Shop's product approval status must contains from 3 to 64 characters!")
    @Column(name = "approval_status", length = 64, nullable = false, columnDefinition = "false")
    private String approvalStatus;

}


