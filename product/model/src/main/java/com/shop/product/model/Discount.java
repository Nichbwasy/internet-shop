package com.shop.product.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "discount")
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Discount name is mandatory!")
    @Size(min = 3, max = 128, message = "Discount name must contains from 3 to 128 characters!")
    @Column(name = "name", length = 128, unique = true, nullable = false)
    private String name;

    @Size(max = 512, message = "Discount description can't be larger than 512 symbols!")
    @Column(name = "description", length = 512)
    private String description;

    @NotNull(message = "Discount created time is mandatory!")
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @NotNull(message = "Discount activation time is mandatory!")
    @Column(name = "activation_time", nullable = false)
    private LocalDateTime activationTime;

    @NotNull(message = "Discount ending time is mandatory!")
    @Column(name = "ending_time", nullable = false)
    private LocalDateTime endingTime;

    @NotNull(message = "Discount value is mandatory")
    @Column(name = "discount_value", nullable = false)
    private Float discountValue;

}
