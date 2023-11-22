package com.shop.shop.dao;

import com.shop.shop.model.ShopProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopProductRepository extends JpaRepository<ShopProduct, Long> {
}
