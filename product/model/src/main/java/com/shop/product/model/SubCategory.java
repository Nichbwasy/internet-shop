package com.shop.product.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "sub_category")
public class SubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Sub category name is mandatory!")
    @Size(min = 3, max = 128, message = "Sub category name must contains from 3 to 128 characters!")
    @Column(name = "name", length = 128, nullable = false, unique = true)
    private String name;

}
