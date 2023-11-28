package com.shop.shop.dao;

import com.shop.shop.model.UserCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCartRepository extends JpaRepository<UserCart, Long> {

    Boolean existsByUserId(Long userId);
    UserCart getReferenceByUserId(Long userId);
}
