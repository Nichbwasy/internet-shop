package com.shop.media.controller;

import com.shop.media.dto.FileExtensionDto;
import com.shop.media.service.FileExtensionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/files/extensions")
public class FileExtensionController {

    private final FileExtensionService fileExtensionService;

    @GetMapping
    public ResponseEntity<List<FileExtensionDto>> getAllFilesExtensions() {
        log.info("Trying to get all file extensions...");
        return ResponseEntity.ok().body(fileExtensionService.getAllFileExtensions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileExtensionDto> getFileExtension(@PathVariable Long id) {
        log.info("Trying to get file extension with id '{}'...", id);
        return ResponseEntity.ok().body(fileExtensionService.getFileExtension(id));
    }

    @PostMapping
    public ResponseEntity<FileExtensionDto> createFileExtension(@RequestBody FileExtensionDto dto) {
        log.info("Trying to create a new file extension...");
        return ResponseEntity.ok().body(fileExtensionService.addFileExtension(dto));
    }

    @PutMapping
    public ResponseEntity<FileExtensionDto> updateFileExtension(@RequestBody FileExtensionDto dto) {
        log.info("Trying to update the file extension with id '{}'...", dto.getId());
        return ResponseEntity.ok().body(fileExtensionService.updateFileExtension(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> removeFileExtension(@PathVariable Long id) {
        log.info("Trying to remove the file extension with id '{}'...", id);
        return ResponseEntity.ok().body(fileExtensionService.removeFileExtension(id));
    }

}
