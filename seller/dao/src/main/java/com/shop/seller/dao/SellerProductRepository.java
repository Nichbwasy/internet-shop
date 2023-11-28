package com.shop.seller.dao;

import com.shop.seller.model.SellerProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerProductRepository extends JpaRepository<SellerProduct, Long> {
}
