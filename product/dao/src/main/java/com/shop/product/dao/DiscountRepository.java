package com.shop.product.dao;

import com.shop.product.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
    List<Discount> findByIdIn(List<Long> ids);

}
