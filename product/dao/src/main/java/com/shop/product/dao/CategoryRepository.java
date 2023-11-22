package com.shop.product.dao;

import com.shop.product.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Boolean existsByIdOrName(Long id, String name);
    List<Category> findByIdIn(List<Long> ids);

}
