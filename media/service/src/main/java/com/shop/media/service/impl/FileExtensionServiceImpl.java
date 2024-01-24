package com.shop.media.service.impl;

import com.shop.common.utils.all.exception.dao.EntityAlreadyExistsException;
import com.shop.common.utils.all.exception.dao.EntityNotFoundRepositoryException;
import com.shop.media.dao.FileExtensionRepository;
import com.shop.media.dto.FileExtensionDto;
import com.shop.media.model.FileExtension;
import com.shop.media.service.FileExtensionService;
import com.shop.media.service.mapper.FileExtensionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileExtensionServiceImpl implements FileExtensionService {

    private final FileExtensionRepository fileExtensionRepository;
    private final FileExtensionMapper fileExtensionMapper;

    @Override
    public List<FileExtensionDto> getAllFileExtensions() {
        return fileExtensionRepository.findAll().stream()
                .map(fileExtensionMapper::mapToDto)
                .toList();
    }

    @Override
    public FileExtensionDto getFileExtension(Long id) {
        return fileExtensionMapper.mapToDto(getFileExtensionById(id));
    }

    @Override
    @Transactional
    public FileExtensionDto addFileExtension(FileExtensionDto fileExtensionDto) {
        checkIfFileExtensionAlreadyExists(fileExtensionDto.getName());
        return fileExtensionMapper.mapToDto(fileExtensionRepository.save(fileExtensionMapper.mapToModel(fileExtensionDto)));
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public FileExtensionDto updateFileExtension(FileExtensionDto fileExtensionDto) {
        FileExtension fileExtension = getFileExtensionById(fileExtensionDto.getId());
        fileExtensionMapper.updateModel(fileExtensionDto, fileExtension);
        return fileExtensionDto;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Long removeFileExtension(Long id) {
        fileExtensionRepository.deleteById(id);
        return id;
    }
    private void checkIfFileExtensionAlreadyExists(String name) {
        if (fileExtensionRepository.existsByName(name)) {
            log.warn("Unable save '{}' file extension! Extension with such name already exists!", name);
            throw new EntityAlreadyExistsException(
                    "Unable save '%s' file extension! Extension with such name already exists!".formatted(name)
            );
        }
    }

    private FileExtension getFileExtensionById(Long id) {
        return fileExtensionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Unable find file extension with id '{}'!", id);
                    return new EntityNotFoundRepositoryException(
                            "Unable find file extension with id '%s'!".formatted(id)
                    );
                });
    }
}
