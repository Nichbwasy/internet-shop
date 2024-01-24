package com.shop.seller.dao;

import com.shop.seller.model.SellerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerInfoRepository extends JpaRepository<SellerInfo, Long> {
    SellerInfo getByUserId(Long userId);
    Boolean existsByUserId(Long userId);
    Optional<SellerInfo> findByUserId(Long userId);
}
