package com.shop.media.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "product_media")
public class ProductMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Product id is mandatory!")
    @Min(value = 1, message = "Product id can't be lesser than 1!")
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @EqualsAndHashCode.Exclude
    @OneToMany(targetEntity = MediaElement.class, fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<MediaElement> mediaElements;

}
