package com.shop.product.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Product code is mandatory!")
    @Size(min = 64, max = 64, message = "Product's code must contains 64 characters!")
    @Column(name = "code", length = 64, nullable = false, unique = true)
    private String code;

    @NotNull(message = "Product name is mandatory!")
    @Size(min = 5, max = 255, message = "Product name must contains from 5 to 255 characters!")
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Size(max = 2048, message = "Description can't be larger then 2048 symbols!")
    @Column(name = "description", length = 2048)
    private String description;

    @NotNull(message = "Product creation time is mandatory!")
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @NotNull(message = "Product count is mandatory!")
    @Column(name = "count", nullable = false, columnDefinition = "integer default 0")
    private Integer count;

    @NotNull(message = "Product price is mandatory!")
    @Column(name = "price", nullable = false, columnDefinition = "numeric(32,9) default 0.0")
    private BigDecimal price;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Category> categories;

    @NotNull(message = "Product approval status is mandatory!")
    @Size(min = 3, max = 64, message = "Product approval status must contains from 3 to 64 characters!")
    @Column(name = "approval_status", length = 64, nullable = false, columnDefinition = "false")
    private String approvalStatus;

    @Column(name = "media_id", columnDefinition = "null")
    private Long mediaId;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "product_discount",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "discount_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Discount> discounts;

}
