package com.shop.media.dao;

import com.shop.media.model.FileExtension;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileExtensionRepository extends JpaRepository<FileExtension, Long> {

    FileExtension getFileExtensionByName(String name);

}
