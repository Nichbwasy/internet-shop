package com.shop.product.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Category name is mandatory!")
    @Size(min = 3, max = 128, message = "Category name must contains from 3 to 128 characters!")
    @Column(name = "name", length = 128, nullable = false, unique = true)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "category_sub_category",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "sub_category_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<SubCategory> subCategories;

}
