package com.shop.media.dao;

import com.shop.media.model.ProductMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductMediaRepository extends JpaRepository<ProductMedia, Long> {

    Boolean existsByProductId(Long productId);
    Optional<ProductMedia> findByProductId(Long productId);


}
