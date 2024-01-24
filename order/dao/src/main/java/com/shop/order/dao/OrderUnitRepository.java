package com.shop.order.dao;

import com.shop.order.model.OrderUnit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderUnitRepository extends JpaRepository<OrderUnit, Long> {
}