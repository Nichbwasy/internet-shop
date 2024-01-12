package com.shop.media.dao;

import com.shop.media.model.FileExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileExtensionRepository extends JpaRepository<FileExtension, Long> {

    Optional<FileExtension> findFileExtensionByName(String name);
    Boolean existsByName(String name);

}
