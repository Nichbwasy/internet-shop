package com.shop.seller.dao;

import com.shop.seller.model.SellerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerInfoRepository extends JpaRepository<SellerInfo, Long> {
    SellerInfo getByUserId(Long userId);
    Boolean existsByUserId(Long userId);
}
