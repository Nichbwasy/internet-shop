package com.shop.seller.dao;

import com.shop.seller.model.SellerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerInfoRepository extends JpaRepository<SellerInfo, Long> {
    Boolean existsByUserId(Long userId);
}
