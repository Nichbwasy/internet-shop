package com.shop.media.service;

import com.shop.media.dto.FileExtensionDto;

import java.util.List;

public interface FileExtensionService {

    List<FileExtensionDto> getAllFileExtensions();
    FileExtensionDto getFileExtension(Long id);
    FileExtensionDto addFileExtension(FileExtensionDto fileExtensionDto);
    FileExtensionDto updateFileExtension(FileExtensionDto fileExtensionDto);
    Long removeFileExtension(Long id);

}
