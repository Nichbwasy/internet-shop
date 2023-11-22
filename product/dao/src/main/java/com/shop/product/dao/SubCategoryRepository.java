package com.shop.product.dao;

import com.shop.product.model.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

    Boolean existsByIdOrName(Long id, String name);
    List<SubCategory> findByIdIn(List<Long> ids);

}
